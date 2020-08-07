package it.sky.mdw.api.application.cli;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;

import picocli.CommandLine;

public class MdwApiRegistryCLI {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.xml");
		//		args = new String[]{
		//				"run", 
		//				"-c=esb_it_conf.json", 
		//				"-r=ESBApiRegistry", 
		//				"-f"
		//				"ui",
		//				"-p=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\mdw-api-explorer",
		//				"-o=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\OSB_IT.json",
		//				"-e=C:\\Users\\fuscogiu\\Documents\\Sky\\git_mdw_explorer\\ESB_IT.json",
		//				"-d=https://github.com/gppfusco/mdw-api-explorer/tree/master"
		//				};
		new CommandLine(new MdwApiRegistryCommand()).execute(args);

	}

}
