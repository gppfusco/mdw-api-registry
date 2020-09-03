package it.sky.mdw.api.application.cli.command;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import it.sky.mdw.api.report.DefaultApiRegistryReport;
import it.sky.mdw.api.report.DefaultReportConfiguration;
import it.sky.mdw.api.util.ConfigurationSerializationUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		description={"Create index.md for explorer documentation."},
		mixinStandardHelpOptions=true,
		name="report")
public class ReportCommand implements Runnable {

	private static Logger logger = Logger.getLogger(ReportCommand.class);

	@Option(
			names={"-c", "--conf"},
			description={"Provide the report configuration file."},
			required=true,
			type=String.class)
	private String reportConfiguration;

	public void run() {
		try {
			logger.info("Checking configuration file...");
			File reportConfigurationFile = new File(reportConfiguration);
			if(reportConfigurationFile.exists() && reportConfigurationFile.isFile()){
				DefaultReportConfiguration conf = ConfigurationSerializationUtil.loadFromJSON(
						new FileInputStream(reportConfigurationFile), DefaultReportConfiguration.class);
				DefaultApiRegistryReport uiAlignment = new DefaultApiRegistryReport();
				uiAlignment.report(conf);
			}else{
				logger.info("Wrong report configuration file.");
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
