package it.sky.mdw.api.registry.osb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.bea.wli.sb.management.configuration.ALSBConfigurationMBean;

import it.sky.mdw.api.AbstractApiRegistry;
import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiNetwork;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.Configuration;
import it.sky.mdw.api.ConfigurationKeys;
import it.sky.mdw.api.Environment;
import it.sky.mdw.api.IntegrationScenario;
import it.sky.mdw.api.Registry;
import it.sky.mdw.api.RegistryContext;
import it.sky.mdw.api.network.NetworkNode;
import it.sky.mdw.api.security.PBE;
import weblogic.management.jmx.MBeanServerInvocationHandler;
import weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean;

public class OSBApiRegistry extends AbstractApiRegistry{

	private static Logger logger = Logger.getLogger(OSBApiRegistry.class);

	private String host = "";
	private Integer port = -1;
	private String username = "";
	private String password = "";
	private String env_base_url = "";

	private void retrieveParams(Configuration conf){
		if(conf.containsKey(OSBConfigurationKeys.WEBLOGIC_HOST))
			host = conf.getProperty(OSBConfigurationKeys.WEBLOGIC_HOST);

		if(conf.containsKey(OSBConfigurationKeys.WEBLOGIC_PORT))
			port = Integer.valueOf(conf.getProperty(OSBConfigurationKeys.WEBLOGIC_PORT));

		if(conf.containsKey(OSBConfigurationKeys.USERNAME))
			username = conf.getProperty(OSBConfigurationKeys.USERNAME);

		if(conf.containsKey(OSBConfigurationKeys.PASSWORD))
			password = conf.getProperty(OSBConfigurationKeys.PASSWORD);

		if(conf.containsKey(OSBConfigurationKeys.ENV_BASE_URL))
			env_base_url = conf.getProperty(OSBConfigurationKeys.ENV_BASE_URL);
	}

	private MBeanServerConnection getMBeanServerConnection() throws Exception {
		try {
			InitialContext ctx = new InitialContext();
			MBeanServer server = (MBeanServer) ctx.lookup("java:comp/env/jmx/runtime");
			return server;
		} catch (Exception e) {
			JMXConnector jmxcon = initRemoteConnection(host, port, username, password);
			return jmxcon.getMBeanServerConnection();
		}
	}

	private JMXConnector initRemoteConnection(String hostname, int port, String username,
			String password) throws Exception {
		String jndiroot = "/jndi/";
		String mserver = "weblogic.management.mbeanservers.domainruntime";
		JMXServiceURL serviceURL =
				new JMXServiceURL("t3", hostname, port, jndiroot + mserver);
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, username);
		h.put(Context.SECURITY_CREDENTIALS, new String(PBE.getInstance().decrypt(password.getBytes())));
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
		return JMXConnectorFactory.connect(serviceURL, h);
	}

	private Object findDomainRuntimeServiceMBean(MBeanServerConnection connection) {
		try {
			ObjectName objectName =
					new ObjectName(DomainRuntimeServiceMBean.OBJECT_NAME);
			return (DomainRuntimeServiceMBean)MBeanServerInvocationHandler.newProxyInstance(connection,
					objectName);
		} catch (MalformedObjectNameException e) {
			logger.error("", e);
			return null;
		}
	}

	@Override
	public RegistryContext getRegistryContext() {
		return OSBRegistryContext.getInstance();
	}

	@Override
	public Registry initializeRegistry(Configuration configuration) throws Exception {
		final List<Api<? extends ApiSpecification>> apis = new ArrayList<Api<? extends ApiSpecification>>();

		logger.info("Read parameters from configuration...");
		retrieveParams(configuration);

		logger.info("Init remote connection...");
		MBeanServerConnection connection = getMBeanServerConnection();
		DomainRuntimeServiceMBean domainRuntimeServiceMBean =
				(DomainRuntimeServiceMBean)findDomainRuntimeServiceMBean(connection);

		// Create an mbean instance to perform configuration operations in the created session.
		//
		// There is a separate instance of ALSBConfigurationMBean for each session.
		// There is also one more ALSBConfigurationMBean instance which works on the core data, i.e., the data which ALSB runtime uses.
		// An ALSBConfigurationMBean instance is created whenever a new session is created via the SessionManagementMBean.createSession(String) API.
		// This mbean instance is then used to perform configuration operations in that session.
		// The mbean instance is destroyed when the corresponding session is activated or discarded.
		final ALSBConfigurationMBean alsbConfigurationMBean =
				(ALSBConfigurationMBean)domainRuntimeServiceMBean.findService(ALSBConfigurationMBean.NAME,
						ALSBConfigurationMBean.TYPE,
						null);

		String domain = "com.oracle.osb";
		String objectNamePattern =
				domain + ":" + "Type=ResourceConfigurationMBean,*";

		OSBRegistryContext.getInstance().initializeContext(alsbConfigurationMBean, connection);

		ExecutorService executor = Executors.newWorkStealingPool();

		MapOfOSBReference mapOfRefs = MapOfOSBReference.getInstance();
		mapOfRefs.onStart(alsbConfigurationMBean);
		logger.info("Discovering APIs.....");

		Set<ObjectName> osbResourceConfigurations =
				connection.queryNames(new ObjectName(objectNamePattern), null);

		ApiNetwork osbNetwork = getRegistryContext().getApiNetwork();

		Collection<Future<Api<? extends ApiSpecification>>> osbProxyProcessors = 
				new ArrayList<Future<Api<? extends ApiSpecification>>>();

		Collection<Future<Void>> osbPipelineProcessors = 
				new ArrayList<Future<Void>>();

		for(ObjectName osbResourceConfiguration: osbResourceConfigurations) {
			String resourceName = osbResourceConfiguration.getKeyProperty("Name");
			if(resourceName.startsWith("ProxyService$")){
				try {
					osbProxyProcessors.add(
							executor.submit(
									new OSBProxyProcessor(osbResourceConfiguration, env_base_url)));
				} catch (Exception e) {
					logger.error("", e);
					continue;
				}
			}
			else if(resourceName.startsWith("Pipeline$") || resourceName.startsWith("PipelineTemplate$")){
				try {
					osbPipelineProcessors.add(
							executor.submit(new OSBPipelineProcessor(osbResourceConfiguration)));
				} catch (Exception e) {
					logger.error("", e);
					continue;
				}
			}
			else if(resourceName.startsWith("BusinessService$")){
				Properties properties = new Properties();
				properties.put("nodeType", "BusinessService");

				String normalizedName = resourceName.replaceAll("\\W", "/");
				osbNetwork.addEntity(normalizedName, osbResourceConfiguration, Optional.of(properties));	
			}
			else {
				String normalizedName = resourceName.replaceAll("\\W", "/");
				osbNetwork.addEntity(normalizedName, osbResourceConfiguration, Optional.empty());	
			}
		}

		osbProxyProcessors.forEach(new Consumer<Future<Api<? extends ApiSpecification>>>() {
			public void accept(Future<Api<? extends ApiSpecification>> t) {
				Api<? extends ApiSpecification> api;
				try {
					api = t.get(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
					if(api!=null && api.getApiSpecification()!=null){
						IntegrationScenario intScenario = getIntegrationScenario(api);
						api.setIntegrationScenario(intScenario);
						apis.add(api);
					}
				} catch (Exception e) {
					logger.error("", e);
				} 
			}

		});

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error("", e);
		}

		Registry repository = new Registry();
		repository.setApis(apis);
		logger.info("Discovering APIs..... Completed");
		return repository;
	}

	@Override
	public void onStoreRegistryCompleted(Environment environment, Configuration configuration) throws Exception {
		compactRepository(environment, configuration);		
	}

	private void compactRepository(Environment environment, Configuration configuration) {
		env_dir_str = configuration.getProperty(ConfigurationKeys.ENV_DIR_NAME);
		dir_path_str = configuration.getProperty(ConfigurationKeys.BASE_DIR_PATH);
		repo_dir_str = configuration.getProperty(ConfigurationKeys.REPOSITORY_DIR_NAME);
		File env_dir = new File(dir_path_str + File.separator + env_dir_str);
		File repo_dir = new File(env_dir.getAbsolutePath() + File.separator + repo_dir_str);
		logger.debug("Organize repository: " + repo_dir.getAbsolutePath());


		if(repo_dir.isDirectory()){
			for(Api<? extends ApiSpecification> api: environment.getRegistry().getApis()){
				try {
					compactApiDirectory(api, repo_dir);
				} catch (Exception e) {
					logger.error("", e);
					continue;
				}
			}
		}
	}

	private void compactApiDirectory(Api<? extends ApiSpecification> api, File repo_dir) throws Exception {
		File file = new File(dir_path_str + File.separator + api.getLocalPath());
		if(file.isDirectory()){
			String path = file.getName();
			String split = path.contains("_proxy_") ? "_proxy_" :
				path.contains("_Proxy_") ? "_Proxy_" :
					path.contains("_PS_") ? "_PS_" :
						"";
			if(split.length()>0){
				String[] names = path.split(split);
				String finalPath = repo_dir.getAbsolutePath() + File.separator+
						names[0].replace("_", File.separator) + File.separator + names[1];
				File newFile = new File(finalPath);
				Files.createDirectories(Paths.get(finalPath));
				logger.debug("Created directory: " + newFile.getName());
				Path srcPath = file.toPath();
				Path destPath = newFile.toPath();
				Files.walkFileTree(srcPath, new CopyDirVisitor(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING));

				Files.walk(srcPath)
				.sorted(Comparator.reverseOrder())
				.map(Path::toFile)
				.forEach(File::delete);
				logger.debug("Deleted directory: " + path);

				api.setLocalPath(env_dir_str + File.separator + repo_dir_str + File.separator + 
						finalPath.replace(repo_dir.getAbsolutePath()+File.separator, ""));
			}
		}
	}

	@Override
	public IntegrationScenario getIntegrationScenario(Api<? extends ApiSpecification> api) {
		RegistryContext registryContext = getRegistryContext();
		if(registryContext != null){
			try {
				NetworkNode apiNode = registryContext.getApiNetwork().findEntityByApiName(api.getName()).orElse(null);
				if(apiNode != null){

					Properties nodeProp = apiNode.getProperties();
					String service_type = (String) nodeProp.getOrDefault("service-type", "no_type");

					Collection<NetworkNode> connections = apiNode.getAllSuccessors();
					int numberOfBusinessServices = 0;
					Collection<String> processedBS = new ArrayList<>();
					for(NetworkNode t: connections){
						String label = t.getLabel();
						Properties prop = t.getProperties();
						String nodeType = (String) prop.getOrDefault("nodeType", "no_type");
						if(nodeType.equals("BusinessService") && 
								!label.contains("BS_LOG_ENQUEUE") &&
								!label.contains("BS_JMS_ENQUEUE_LOG_MDW_SERVICE") && 
								!label.contains("BS_SQL_INSERT_OR_UPDATE_SLAVE_NOTIFICATION_TRACKING")){
							if(!processedBS.contains(label)){
								processedBS.add(label);
								numberOfBusinessServices++;
							}
						}
					}

					if(numberOfBusinessServices >= 2)
						return IntegrationScenario.ORCHESTRATION;
					else if(service_type.equals("Messaging"))
						return IntegrationScenario.PROXY;
					else
						return IntegrationScenario.DATA_MANIPULATION;
				}


			} catch (Exception e) {
				logger.error("", e);
				return IntegrationScenario.NOT_DEFINED;
			}
		}

		return IntegrationScenario.NOT_DEFINED;
	}

}
