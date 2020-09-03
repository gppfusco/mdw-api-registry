package it.sky.mdw.api.repository;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import it.sky.mdw.api.Environment;
import it.sky.mdw.api.security.PBE;
import it.sky.mdw.api.util.EnvironmentSerializationUtil;

public class GitApiRepository implements ApiRepository {

	private static Logger logger = Logger.getLogger(GitApiRepository.class);
	private Git repo;
	private SkyRepositoryConfiguration repositoryConfiguration;
	private UsernamePasswordCredentialsProvider credentialProvider;
	private String branch;

	public <C extends RepositoryConfiguration> void init(C repositoryConfiguration) throws Exception {
		SkyRepositoryConfiguration skyRepoConf = (SkyRepositoryConfiguration) repositoryConfiguration;
		init(skyRepoConf);
	}

	private void init(SkyRepositoryConfiguration repositoryConfiguration) throws Exception {
		this.repositoryConfiguration = repositoryConfiguration;
		branch = repositoryConfiguration.getBranch();
		credentialProvider = new UsernamePasswordCredentialsProvider(repositoryConfiguration.getUsername(), 
				repositoryConfiguration.isPasswordEncrypted() ? new String(PBE.getInstance().decrypt(
						repositoryConfiguration.getPassword().getBytes())) : repositoryConfiguration.getPassword());

		if(!open())
			if(!cloneRepo())
				init();
	}

	private boolean checkBranch(){
		try {
//			boolean existsBranch = repo.branchList().call().contains(branch);
//			if(!existsBranch){
//				// create branch
//				logger.info("Trying to create branch: " + branch);
//				repo.branchCreate()
//				.setName(branch)
//				.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
//				.setStartPoint(Constants.HEAD)
//				.call();
//			}
//			else{
				logger.info("Trying to pull files from: " + branch);
				PullCommand pull = repo.pull()
						.setCredentialsProvider(credentialProvider)
						.setRemoteBranchName(branch)
						.setRemote(Constants.DEFAULT_REMOTE_NAME);
				pull.call();				
//			}
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	private boolean open(){
		try {
			logger.info("Trying to open repository " + repositoryConfiguration.getDirectory());
			repo = Git.open(new File(repositoryConfiguration.getDirectory()));
			return checkBranch();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	private boolean cloneRepo(){
		try {
			logger.info("Trying to clone repository into " + repositoryConfiguration.getDirectory());
			repo = Git.cloneRepository().setDirectory(new File(repositoryConfiguration.getDirectory()))
					.setURI(repositoryConfiguration.getUri())
					.setCredentialsProvider(credentialProvider )
					.setBranch(branch)
					.call();
			initRepoConfig();
			return true;
		} catch (Exception e2) {
			logger.error("", e2);
			return false;
		}
	}

	private boolean init(){
		try {
			logger.info("Trying to initialize repository into " + repositoryConfiguration.getDirectory());
			repo = Git.init()
					.setDirectory(new File(repositoryConfiguration.getDirectory()))
					.call();
			initRepoConfig();
			return checkBranch();
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	private void initRepoConfig() throws IOException {
		StoredConfig config = repo.getRepository().getConfig();
		config.setString("core", null, "autocrlf", "false");
		config.setString("core", null, "longpaths", "true");
		config.setString("remote", Constants.DEFAULT_REMOTE_NAME, "url", repositoryConfiguration.getUri()+".git");
		config.setString("remote", Constants.DEFAULT_REMOTE_NAME, "fetch", "+refs/heads/*:refs/remotes/origin/*");
		config.setString("branch", branch, "remote", Constants.DEFAULT_REMOTE_NAME);
		config.setString("branch", branch, "merge", Constants.R_HEADS + branch);
		config.setString("user", null, "name", repositoryConfiguration.getUsername());
		config.save();
		logger.info("Git configuration created." + System.lineSeparator() + config.toText());
	}

	private void update(Environment environment) throws Exception {
		logger.info("Trying to add files for environment: " + environment.getReferenceName());
		AddCommand addCommand = repo.add();

		addCommand.addFilepattern(environment.getEnvDir());
		addCommand.call();

		logger.info("Trying to commit files for environment: " + environment.getReferenceName());
		CommitCommand commit = repo.commit();
		//commit.setCredentialsProvider(credentialProvider);
		commit.setMessage("Registry documentation update.");
		commit.call();

		logger.info("Trying to push files for environment: " + environment.getReferenceName());
		PushCommand push = repo.push()
				.setRemote(Constants.DEFAULT_REMOTE_NAME)
				.add(branch)
				.setCredentialsProvider(credentialProvider);
		push.call();

		logger.info("Environment " + environment.getReferenceName() + " updated successfully");
	}
	
	private void updateReadme() throws Exception {
		AddCommand addCommand = repo.add();

		addCommand.addFilepattern("README.md");
		addCommand.call();

		CommitCommand commit = repo.commit();
		//commit.setCredentialsProvider(credentialProvider);
		commit.setMessage("Update README.");
		commit.call();

		PushCommand push = repo.push()
				.setRemote(Constants.DEFAULT_REMOTE_NAME)
				.add(branch)
				.setCredentialsProvider(credentialProvider);
		push.call();

		logger.info("Readme updated successfully");
	}

	public void update() throws Exception {
		for(String envDir: repositoryConfiguration.getEnvironmentDirEntries()){
			try {
				logger.info("Loading environment: " + envDir);
				Environment env = EnvironmentSerializationUtil.unmarshall(new File(
						repositoryConfiguration.getDirectory() + File.separator + envDir));
				logger.info("Environment " + envDir + " loaded");
				update(env);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		
		updateReadme();
	}

}
