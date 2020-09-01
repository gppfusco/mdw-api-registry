package it.sky.mdw.api.application.cli.command;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import it.sky.mdw.api.SkyEnvironment;
import it.sky.mdw.api.report.ApiRegistryReport;
import it.sky.mdw.api.util.EnvironmentSerializationUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		description={"Create index.md for explorer documentation."},
		mixinStandardHelpOptions=true,
		name="report")
public class RegistryReportCommand implements Runnable {

	private static Logger logger = Logger.getLogger(RegistryReportCommand.class);

	@Option(
			names={"-o", "--osb"},
			description={"Provide the JSON registry file for OSB."},
			required=true,
			type=String.class)
	private String osbRegistryJsonFile;

	@Option(
			names={"-e", "--esb"},
			description={"Provide the JSON registry file for ESB."},
			required=true,
			type=String.class)
	private String esbRegistryJsonFile;

	@Option(
			names={"-p", "--explorer_dir"},
			description={"Provide the base direcotory to save do explorer documentation."},
			required=true,
			type=String.class)
	private String explorerLocalBasePath;

	@Option(
			names={"-d", "--git_doc"},
			description={"Provide the base URL for github documentation repository."},
			required=true,
			type=String.class)
	private String githubURLBaseDoc;

	@Override
	public void run() {
		try {

			logger.info("Checking configuration file...");
			File osbRegistryJsonFile_f = new File(osbRegistryJsonFile);
			File esbRegistryJsonFile_f = new File(esbRegistryJsonFile);
			File explorerLocalBasePath_f = new File(explorerLocalBasePath);
			URL githubURLBaseDoc_u = new URL(githubURLBaseDoc);
			if(
					(osbRegistryJsonFile_f.exists() && osbRegistryJsonFile_f.isFile())
					&& (esbRegistryJsonFile_f.exists() && esbRegistryJsonFile_f.isFile())
					&& (explorerLocalBasePath_f.exists() && explorerLocalBasePath_f.isDirectory())
					){

				SkyEnvironment env_osb = (SkyEnvironment) EnvironmentSerializationUtil.unmarshall(osbRegistryJsonFile_f);
				SkyEnvironment env_esb = (SkyEnvironment) EnvironmentSerializationUtil.unmarshall(esbRegistryJsonFile_f);

				ApiRegistryReport uiAlignment = new ApiRegistryReport();
				uiAlignment.report(env_osb, env_esb, 
						explorerLocalBasePath_f.getAbsolutePath(), 
						githubURLBaseDoc_u);
			}else{
				logger.info("Some file configurations are wrong. Check the following file path:" + 
						System.lineSeparator() + osbRegistryJsonFile_f + System.lineSeparator() + 
						esbRegistryJsonFile_f + System.lineSeparator() + 
						explorerLocalBasePath_f);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
