package it.sky.mdw.api.report;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.Environment;
import it.sky.mdw.api.Registry;
import it.sky.mdw.api.util.EnvironmentSerializationUtil;
import net.steppschuh.markdowngenerator.link.Link;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;

public class DefaultApiRegistryReport implements ApiRegistryReport {

	private static Logger logger = Logger.getLogger(DefaultApiRegistryReport.class);

	public <C extends ReportConfiguration> void report(C reportConfiguration) throws Exception {
		if(reportConfiguration instanceof DefaultReportConfiguration){
			DefaultReportConfiguration conf = (DefaultReportConfiguration) reportConfiguration;
			report(conf);
		}else throw new IllegalArgumentException("Configuration must be an instance of " + 
				DefaultReportConfiguration.class.getCanonicalName());
	}

	private void report(DefaultReportConfiguration reportConfiguration) throws Exception {
		Objects.requireNonNull(reportConfiguration, "Report configuration cannot be null.");
		Environment osb = null, esb = null;
		String explorerLocalBasePath, githubURLBaseDoc;
		try {
			explorerLocalBasePath = reportConfiguration.getExplorerLocalBasePath();
			githubURLBaseDoc = reportConfiguration.getGithubURLBaseDoc();
			osb = EnvironmentSerializationUtil.unmarshall(explorerLocalBasePath + File.separator + 
					reportConfiguration.getOsbRegistryFile());
			esb = EnvironmentSerializationUtil.unmarshall(explorerLocalBasePath + File.separator + 
					reportConfiguration.getEsbRegistryFile());
		} catch (Exception e) {
			throw e;
		}

		report(osb, esb, explorerLocalBasePath, new URL(githubURLBaseDoc));
	}

	private void report(Environment osbEvironment, Environment esbEvironment, String explorerLocalBasePath_f, URL gitHubBaseDoc) throws Exception {
		logger.info("Starting to document api registry...");
		Registry osbRegistry = osbEvironment.getRegistry();
		Registry esbRegistry = esbEvironment.getRegistry();
		File index = new File(explorerLocalBasePath_f + File.separator + "README.md");
		StringBuilder builder = new StringBuilder();

		builder.append(new Heading("Introduction", 3));
		builder.append(System.lineSeparator());
		builder.append("Find information about web services hosted on [OSB](#osb) and [ESB](#esb). Enjoy!!");
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append(new Heading("<a name=\"osb\"></a>MDW Oacle Service Bus", 3));
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append(createTableOfAPIs(osbRegistry, gitHubBaseDoc).build());
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append(new Heading("<a name=\"esb\"></a>MDW Exist Bus", 3));
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append(createTableOfAPIs(esbRegistry, gitHubBaseDoc).build());

		FileUtils.writeStringToFile(index, builder.toString(), "UTF-8");

		logger.info("Documentation of api registry completed.");
	}

	private Table.Builder createTableOfAPIs(Registry registry, URL gitHubBaseDoc){
		Table.Builder tableBuilder = new Table.Builder()
				.withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
				.addRow("API Endpoint", "Documentation");

		for(Api<? extends ApiSpecification> api: registry.getApis()){
			String url = gitHubBaseDoc.toString() + "/" + api.getLocalPath().replace(File.separator, "/");
			try {
				Link apiLink = new Link("View doc", new URL(url).toString());
				logger.debug("Adding row for api: " + apiLink);
				tableBuilder.addRow(
						new Link(api.getPath(), api.getEndpoint()+api.getPath()), 
						//new Code(api.getName()), 
						apiLink);
			} catch (MalformedURLException e) {
				logger.error("Error while adding row for api: " + api.toString(), e);
			}
		}

		return tableBuilder;
	}

}
