package it.sky.mdw.api.application.cli;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;

import it.sky.mdw.api.application.cli.command.MdwApiRegistryCommand;
import picocli.CommandLine;

public class MdwApiRegistryCLI {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.dir") + File.separator + "log4j.xml");
		//		args = new String[]{
		//				"reg", 
		//				"-c=esb_it_conf.json", 
		//				"-r=ESBApiRegistry", 
		//				"-f"
		//		};
		//		args = new String[]{
		//				"reg", 
		//				"-c=osb_it_conf.json", 
		//				"-r=OSBApiRegistry", 
		//				"-f"
		//		};
		//		args = new String[]{
		//				"repo",
		//				"-c=repository.properties"
		//		};
		//		args = new String[]{
		//				"pbe",
		//				"a text"
		//		};
		//		args = new String[]{
		//				"report", 
		//				"-c=report.json"
		//		};
		//		args = new String[]{"registry", "-h"};
		new CommandLine(new MdwApiRegistryCommand()).execute(args);

	}

}
