package it.sky.mdw.api.application.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import it.sky.mdw.api.ApiRegistry;
import it.sky.mdw.api.Configuration;
import it.sky.mdw.api.ConfigurationKeys;
import it.sky.mdw.api.registry.existbus.ESBApiRegistry;
import it.sky.mdw.api.registry.existbus.ESBConfigurationKeys;
import it.sky.mdw.api.registry.osb.OSBApiRegistry;
import it.sky.mdw.api.registry.osb.OSBConfigurationKeys;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IParameterConsumer;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;

@Command(
		description={"Show useful information about commands."},
		mixinStandardHelpOptions=true,
		name="show")
public class RunCommandHelper implements Runnable {

	@Option(
			names={"-c", "--conf"},
			description={"Provide an example of configuration file. The FORMAT parameter must be <json> or <xml> or <txt>"},
			required=false,
			paramLabel="FORMAT",
			parameterConsumer=ShowConfigOption.class)
	private String conf;

	@Option(
			names={"-r", "--registry"},
			description={"Provide the available api registry types. For each registry type show an example of configuration."},
			required=false)
	private boolean registry;

	@Override
	public void run() {
		if(registry){
			Reflections reflections = new Reflections(new ConfigurationBuilder()
					.setUrls(ClasspathHelper.forPackage("it.sky.mdw.api"))
					.addScanners(new SubTypesScanner()));
			Set<Class<? extends ApiRegistry>> classes = reflections.getSubTypesOf(ApiRegistry.class);
			Iterator<Class<? extends ApiRegistry>> iterator = classes.iterator();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(iterator.hasNext()){
				Class<? extends ApiRegistry> clazz = iterator.next();
				try {
					Configuration conf = new Configuration();
					if(!Modifier.isAbstract(clazz.getModifiers())){
						conf.setProperty(ConfigurationKeys.BASE_DIR_PATH, ConfigurationKeys.BASE_DIR_PATH);
						conf.setProperty(ConfigurationKeys.ENV_DIR_NAME, ConfigurationKeys.ENV_DIR_NAME);
						conf.setProperty(ConfigurationKeys.REPOSITORY_DIR_NAME, ConfigurationKeys.REPOSITORY_DIR_NAME);
						conf.setProperty(ConfigurationKeys.WADL_DIR_NAME, ConfigurationKeys.WADL_DIR_NAME);
						conf.setProperty(ConfigurationKeys.WSDL_DIR_NAME, ConfigurationKeys.WSDL_DIR_NAME);
						conf.setProperty(ConfigurationKeys.XSD_DIR_NAME, ConfigurationKeys.XSD_DIR_NAME);
						conf.setProperty(ConfigurationKeys.ENV_NAME, ConfigurationKeys.ENV_NAME);
						conf.setProperty(ConfigurationKeys.ENV_BASE_URL, ConfigurationKeys.ENV_BASE_URL);
						baos.write((clazz.getSimpleName()+System.lineSeparator()).getBytes());
						if(clazz.getName().equals(OSBApiRegistry.class.getName())){
							conf.setProperty(OSBConfigurationKeys.WEBLOGIC_HOST, OSBConfigurationKeys.WEBLOGIC_HOST);
							conf.setProperty(OSBConfigurationKeys.WEBLOGIC_PORT, OSBConfigurationKeys.WEBLOGIC_PORT);
							conf.setProperty(OSBConfigurationKeys.USERNAME, OSBConfigurationKeys.USERNAME);
							conf.setProperty(OSBConfigurationKeys.PASSWORD, OSBConfigurationKeys.PASSWORD);
						}
						if(clazz.getName().equals(ESBApiRegistry.class.getName())){
							conf.setProperty(ESBConfigurationKeys.API_URL, ESBConfigurationKeys.API_URL);
							conf.setProperty(ESBConfigurationKeys.USERNAME, ESBConfigurationKeys.USERNAME);
							conf.setProperty(ESBConfigurationKeys.PASSWORD, ESBConfigurationKeys.PASSWORD);
						}

						conf.storeToJSON(baos);
						baos.write(System.lineSeparator().getBytes());

					}
				} catch (Exception e) {
					continue;
				}
			}

			System.out.println(new String(baos.toByteArray()));
		}
	}

	static class ShowConfigOption implements IParameterConsumer {
		@Override
		public void consumeParameters(Stack<String> arg0, ArgSpec arg1, CommandSpec arg2) {
			if(!arg0.isEmpty() && arg1.paramLabel().equals("FORMAT")){
				String arg = arg0.pop();
				Configuration conf = new Configuration();
				conf.setProperty(ConfigurationKeys.REPOSITORY_DIR_NAME, "repo");
				conf.setProperty(ConfigurationKeys.ENV_DIR_NAME, "osb_it");
				conf.setProperty(ConfigurationKeys.BASE_DIR_PATH, System.getProperty("user.dir"));
				conf.setProperty(ConfigurationKeys.ENV_BASE_URL, "https://<url>/<base path>");
				conf.setProperty(ConfigurationKeys.ENV_NAME, "OSB_IT");

				if(arg.equals("xml")){
					try {
						conf.storeToXML(System.out, "", "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(arg.equals("json")){
					try {
						conf.storeToJSON(System.out);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(arg.equals("txt")){
					try {
						conf.store(System.out, "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					System.err.println("Invalid FORMAT parameter.");
					CommandLine.usage(RunCommandHelper.class, System.out);
				}
			}
		}
	}

}
