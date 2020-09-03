package it.sky.mdw.api.registry.osb;

import java.util.Properties;
import java.util.concurrent.Callable;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import org.apache.log4j.Logger;

public class OSBPipelineProcessor implements Callable<Void>{

	private static Logger logger = Logger.getLogger(OSBPipelineProcessor.class);

	private ObjectName osbResourceConfiguration;
	private MBeanServerConnection connection;

	public OSBPipelineProcessor(ObjectName osbResourceConfiguration) {
		super();
		this.osbResourceConfiguration = osbResourceConfiguration;
		this.connection = OSBRegistryContext.getInstance().getConnection();
	}

	@Override
	public Void call() throws Exception {
		String resourceName = osbResourceConfiguration.getKeyProperty("Name");

		if(resourceName.startsWith("Pipeline$") || resourceName.startsWith("PipelineTemplate$")){
			Properties properties = new Properties();
			properties.put("nodeType", "Pipeline");

			OSBRegistryContext.getInstance().getApiNetwork().addEntity(
					resourceName.replaceAll("\\W", "/"), osbResourceConfiguration,
					properties);	
			try {
				CompositeDataSupport metadata =
						(CompositeDataSupport)connection.getAttribute(osbResourceConfiguration,
								"Metadata");
				String[] dependencies =
						(String[])metadata.get("dependencies");
				exploreDependencies(resourceName.replaceAll("\\W", "/"), dependencies);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return null;
	}

	private void exploreDependencies(String pipelineName, String[] dependencies) {
		for (int i = 0; i < dependencies.length; i++) {
			String dependency = dependencies[i];
			logger.debug("Dependency found - " + dependency + "\n");
			String depNormalizedName = dependency.replaceAll("\\W", "/");
			OSBRegistryContext.getInstance().getApiNetwork().addConnection(pipelineName, depNormalizedName);
		}		
	}
}
