package it.sky.mdw.api.logging;

import org.apache.log4j.xml.DOMConfigurator;

public final class LoggerConfigurator {

	public static void configureLogger(String loggerConfigurationFile) {
		try {
			DOMConfigurator.configure(loggerConfigurationFile);
		} catch (Exception e) {
			System.out.println("Configuring default logger...");
		}
	}
}
