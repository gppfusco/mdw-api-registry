package it.sky.mdw.api.repository;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
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
	private RepositoryConfiguration repositoryConfiguration;
	private UsernamePasswordCredentialsProvider credentialProvider;

	@Override
	public void init(RepositoryConfiguration repositoryConfiguration) throws Exception {
		this.repositoryConfiguration = repositoryConfiguration;
		credentialProvider = new UsernamePasswordCredentialsProvider(repositoryConfiguration.getUsername(), 
				new String(PBE.getInstance().decrypt(
						repositoryConfiguration.getPassword().getBytes())));

		if(!open())
			if(!cloneRepo())
				init();
	}

	private boolean open(){
		try {
			logger.info("Trying to open repository " + repositoryConfiguration.getDirectory());
			repo = Git.open(new File(repositoryConfiguration.getDirectory()));
			PullCommand pull = repo.pull()
					.setCredentialsProvider(credentialProvider)
					.setRemoteBranchName(Constants.MASTER)
					.setRemote(Constants.DEFAULT_REMOTE_NAME);
			pull.call();
			return true;
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
					.setBranch(Constants.MASTER).call();
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
			PullCommand pull = repo.pull()
					.setCredentialsProvider(credentialProvider)
					.setRemoteBranchName(Constants.MASTER)
					.setRemote(Constants.DEFAULT_REMOTE_NAME);
			pull.call();
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}

	private void initRepoConfig() throws IOException {
		StoredConfig config = repo.getRepository().getConfig();
		config.setString("remote", Constants.DEFAULT_REMOTE_NAME, "url", repositoryConfiguration.getUri()+".git");
		config.setString("remote", Constants.DEFAULT_REMOTE_NAME, "fetch", "+refs/heads/*:refs/remotes/origin/*");
		config.setString("branch", Constants.MASTER, "remote", Constants.DEFAULT_REMOTE_NAME);
		config.setString("branch", Constants.MASTER, Constants.ATTR_MERGE, Constants.R_HEADS + "master");
		config.setString("user", null, "name", repositoryConfiguration.getUsername());
		config.save();
	}

	private void update(Environment environment) throws Exception {
		AddCommand addCommand = repo.add();

		addCommand.addFilepattern(environment.getEnvDir());
		addCommand.call();

		CommitCommand commit = repo.commit();
		commit.setCredentialsProvider(credentialProvider);
		commit.setMessage("Registry documentation update.");
		commit.call();

		PushCommand push = repo.push()
				.setRemote(Constants.DEFAULT_REMOTE_NAME)
				.add(Constants.MASTER)
				.setCredentialsProvider(credentialProvider)
				.setAtomic(true);
		push.call();
	}

	@Override
	public void update() throws Exception {
		for(String envDir: repositoryConfiguration.getEnvironmentDirEntry()){
			try {
				Environment env = EnvironmentSerializationUtil.unmarshall(new File(
						repositoryConfiguration.getDirectory() + File.separator + envDir));
				update(env);
			} catch (Exception e) {
				logger.error("", e);
				continue;
			}
		}
	}

}
