package it.sky.mdw.api.application.cli;

import java.io.File;

import it.sky.mdw.api.application.cli.command.MdwApiRegistryCommand;
import it.sky.mdw.api.logging.LoggerConfigurator;
import picocli.CommandLine;

public class MdwApiRegistryCLI {

	public static void main(String[] args) {
		LoggerConfigurator.configureLogger(System.getProperty("user.dir") + File.separator + "log4j.xml");
//				args = new String[]{
//						"registry", 
//						"-c=esb_it_conf.json", 
//						"-r=ESBApiRegistry", 
//						"-f"
//				};
//				args = new String[]{
//						"registry", 
//						"-c=osb_it_conf.json", 
//						"-r=OSBApiRegistry", 
//						"-f"
//				};
				args = new String[]{
						"repository",
						"-c=repository.json",
						"-a=init"
				};
//				args = new String[]{
//						"repository",
//						"-c=repository.json",
//						"-a=update"
//				};
//				args = new String[]{
//						"report",
//						"-c=report.json"
//				};
//				args = new String[]{
//						"pbe",
//						"a text"
//				};
		new CommandLine(new MdwApiRegistryCommand()).execute(args);

	}

}
