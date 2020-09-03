package it.sky.mdw.api.logging;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.xml.DOMConfigurator;

public final class LoggerConfigurator {

	public static void configureLogger(String loggerConfigurationFile) {
		try {
			if(Files.exists(Paths.get(loggerConfigurationFile)))
				DOMConfigurator.configure(loggerConfigurationFile);
			else
				System.out.println("Configuring default logger...");
		} catch (Exception e) {
			System.out.println("Configuring default logger...");
		}
	}
}
