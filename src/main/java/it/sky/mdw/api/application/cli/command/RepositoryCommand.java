package it.sky.mdw.api.application.cli.command;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import it.sky.mdw.api.repository.GitApiRepository;
import it.sky.mdw.api.repository.RepositoryConfiguration;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		description={"Initialize and update a repository for the Api registries."},
		mixinStandardHelpOptions=true,
		name="repo")
public class RepositoryCommand implements Runnable {

	private static final String INIT = "init";
	private static final String UPDATE = "update";
	private static Logger logger = Logger.getLogger(RepositoryCommand.class);

	@Option(
			names={"-c", "--conf"},
			description={"Provide configuration file."},
			required=true,
			type=String.class)
	private String configurationFile;

	@Option(
			names={"-a", "--action"},
			description={"<" + INIT + "> or <" + UPDATE + ">"},
			required=false,
			paramLabel="ACTION")
	private String action;

	@Override
	public void run() {
		try {
			logger.info("Checking configuration file...");
			File confFile = new File(configurationFile);
			if(confFile.exists()){
				if(confFile.isFile()){
					if(action!=null){
						RepositoryConfiguration conf = RepositoryConfiguration.loadFromJSON(new FileInputStream(confFile));
						if(action.equalsIgnoreCase(INIT)){
							GitApiRepository repo = new GitApiRepository();
							repo.init(conf);
						}else if(action.equalsIgnoreCase(UPDATE)){
							GitApiRepository repo = new GitApiRepository();
							repo.init(conf);
							repo.update();
						}else{
							logger.info("Invalid ACTION parameter.");
							CommandLine.usage(RepositoryCommand.class, System.out);
						}
					}else{
						logger.info("Missing ACTION parameter.");
						CommandLine.usage(RepositoryCommand.class, System.out);
					}
				}else
					logger.error(configurationFile + " is not a file.");
			}else
				logger.error(configurationFile + " not exist.");
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
