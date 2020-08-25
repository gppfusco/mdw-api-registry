package it.sky.mdw.api.application.cli;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;

import picocli.CommandLine;

public class MdwApiRegistryCLI {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.xml");
//				args = new String[]{
//						"run", 
//						"-c=osb_it_conf.json", 
//						"-r=OSBApiRegistry", 
//						"-l"
//						};
//				args = new String[]{
//						"ui", 
//						"-o=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\mdw-api-explorer\\osb_it\\OSB_IT.json", 
//						"-e=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\mdw-api-explorer\\esb_it\\ESB_IT.json", 
//						"-p=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\mdw-api-explorer",
//						"-d=https://github.com/gppfusco/mdw-api-explorer"
//						};
		new CommandLine(new MdwApiRegistryCommand()).execute(args);

	}

}
