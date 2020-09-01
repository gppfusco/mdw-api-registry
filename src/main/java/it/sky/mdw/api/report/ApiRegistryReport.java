package it.sky.mdw.api.report;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import it.sky.mdw.api.Api;
import it.sky.mdw.api.ApiSpecification;
import it.sky.mdw.api.Environment;
import it.sky.mdw.api.Registry;
import net.steppschuh.markdowngenerator.MarkdownSerializationException;
import net.steppschuh.markdowngenerator.link.Link;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.heading.Heading;

public class ApiRegistryReport {

	private static Logger logger = Logger.getLogger(ApiRegistryReport.class);

	public void report(Environment osbEvironment, Environment esbEvironment, String explorerLocalBasePath_f, URL gitHubBaseDoc) throws IOException, MarkdownSerializationException{
		logger.info("Starting to align registry UI...");
		Registry osbRegistry = osbEvironment.getRegistry();
		Registry esbRegistry = esbEvironment.getRegistry();
		File index = new File(explorerLocalBasePath_f + File.separator + "index.md");
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

		logger.info("UI alignment completed.");
	}

	private Table.Builder createTableOfAPIs(Registry registry, URL gitHubBaseDoc){
		Table.Builder tableBuilder = new Table.Builder()
				.withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
				.addRow("API Endpoint", "Documentation");

		registry.getApis().forEach(new Consumer<Api<? extends ApiSpecification>>() {
			@Override
			public void accept(Api<? extends ApiSpecification> api) {
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
		});

		return tableBuilder;
	}

}
