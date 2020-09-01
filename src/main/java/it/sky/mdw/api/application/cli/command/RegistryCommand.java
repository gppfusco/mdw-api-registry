package it.sky.mdw.api.application.cli.command;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import it.sky.mdw.api.ApiRegistry;
import it.sky.mdw.api.Configuration;
import it.sky.mdw.api.Registry;
import it.sky.mdw.api.SkyEnvironment;
import it.sky.mdw.api.registry.osb.OSBConfigurationKeys;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		description={"Create a registry for all APIs from server."},
		mixinStandardHelpOptions=true,
		name="registry")
public class RegistryCommand implements Runnable {

	private static Logger logger = Logger.getLogger(RegistryCommand.class);

	@Option(
			names={"-c", "--conf"},
			description={"Provide configuration file."},
			required=true,
			type=String.class)
	private String configurationFile;

	@Option(
			names={"-r", "--registry"},
			description={"Provide the registry type for APIs discovering."},
			required=true,
			type=String.class)
	private String registryType;

	@ArgGroup(exclusive=false, multiplicity="0..1")
	private StoreMode storeMode;

	static class StoreMode {
		@Option(
				names = {"-f", "--full"}, 
				required = false,
				type = Boolean.class,
				description={"Specify if the full registry should be stored on file system."}) 
		static boolean fullMode;

		@Option(
				names = {"-l", "--light"}, 
				required = false, 
				type = Boolean.class,
				description={"Specify if only APIs should be stored on file system."}) 
		static boolean lightMode;
	}

	@Override
	public void run() {
		try {
			logger.info("Checking configuration file...");
			File confFile = new File(configurationFile);
			if(confFile.exists()){
				if(confFile.isFile()){

					Reflections reflections = new Reflections(new ConfigurationBuilder()
							.setUrls(ClasspathHelper.forPackage("it.sky.mdw.api"))
							.addScanners(new SubTypesScanner()));
					Set<Class<? extends ApiRegistry>> classes = reflections.getSubTypesOf(ApiRegistry.class);
					Iterator<Class<? extends ApiRegistry>> iterator = classes.iterator();
					Class<? extends ApiRegistry> registryClass = null;
					while(iterator.hasNext() && registryClass==null){
						Class<? extends ApiRegistry> clazz = iterator.next();
						if(clazz.getSimpleName().equals(registryType))
							registryClass = clazz;
					}
					if(registryClass == null)
						logger.error("Invalid registry type: " + registryType);
					else{
						Configuration conf = Configuration.load(confFile);

						ApiRegistry apiRegistry = registryClass.newInstance();

						long start = System.currentTimeMillis();
						Registry registry = apiRegistry.initializeRegistry(conf);
						long end = System.currentTimeMillis();

						SkyEnvironment env = new SkyEnvironment();
						env.setBaseUrl(conf.getProperty(OSBConfigurationKeys.ENV_BASE_URL));
						env.setReferenceName(conf.getProperty(OSBConfigurationKeys.ENV_NAME));
						env.setRegistry(registry);

						logger.info("Number of apis: " + registry.getApis().size());
						logger.info("Time in ms: " + (end-start));

						if(StoreMode.fullMode){
							start = System.currentTimeMillis();

							apiRegistry.storeFullRegistry(env, conf);

							end = System.currentTimeMillis();
							logger.info("Full registry stored in (ms): " + (end-start));
						}
						else if(StoreMode.lightMode){
							start = System.currentTimeMillis();

							apiRegistry.storeRegistryAPIs(env, conf);

							end = System.currentTimeMillis();
							logger.info("APIs stored in (ms): " + (end-start));
						}
					}
				}else
					logger.error(configurationFile + " is not a file.");
			}else
				logger.error(configurationFile + " not exist.");
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
