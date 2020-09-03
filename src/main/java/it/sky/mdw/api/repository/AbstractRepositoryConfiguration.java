package it.sky.mdw.api.repository;

import org.eclipse.jgit.lib.Constants;

public abstract class AbstractRepositoryConfiguration implements RepositoryConfiguration {

	private String directory, uri, username, password, branch;
	private boolean passwordEncrypted;

	public AbstractRepositoryConfiguration() {
		passwordEncrypted = false;
		branch = Constants.MASTER;
	}
	
	public String getBranch() {
		return branch;
	}
	
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public boolean isPasswordEncrypted() {
		return passwordEncrypted;
	}
	
	public void setPasswordEncrypted(boolean passwordEncrypted) {
		this.passwordEncrypted = passwordEncrypted;
	}
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
