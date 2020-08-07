package it.sky.mdw.api.application.cli;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.sky.mdw.api.SkyEnvironment;
import it.sky.mdw.api.application.ui.RegistryUIAlignment;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		description={"Create index.md for github pages."},
		mixinStandardHelpOptions=true,
		name="ui")
public class RegistryUIAlignmentCommand implements Runnable {

	private static Logger logger = Logger.getLogger(RegistryUIAlignmentCommand.class);

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
			names={"-p", "--git_pages"},
			description={"Provide the base direcotory of github pages repository."},
			required=true,
			type=String.class)
	private String githubPagesLocalRepoHome;

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
			File githubPagesLocalRepoHome_f = new File(githubPagesLocalRepoHome);
			URL githubURLBaseDoc_u = new URL(githubURLBaseDoc);
			if(
					(osbRegistryJsonFile_f.exists() && osbRegistryJsonFile_f.isFile())
					&& (esbRegistryJsonFile_f.exists() && esbRegistryJsonFile_f.isFile())
					&& (githubPagesLocalRepoHome_f.exists() && githubPagesLocalRepoHome_f.isDirectory())){
				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

				JsonNode jsonNode_osb = mapper.readTree(osbRegistryJsonFile_f).findPath("environment");
				SkyEnvironment env_osb = mapper.treeToValue(jsonNode_osb, SkyEnvironment.class);

				JsonNode jsonNode_esb = mapper.readTree(esbRegistryJsonFile_f).findPath("environment");
				SkyEnvironment env_esb = mapper.treeToValue(jsonNode_esb, SkyEnvironment.class);

				RegistryUIAlignment uiAlignment = new RegistryUIAlignment();
				uiAlignment.alignUI(env_osb, env_esb, 
						githubPagesLocalRepoHome_f.getAbsolutePath(), 
						githubURLBaseDoc_u);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
