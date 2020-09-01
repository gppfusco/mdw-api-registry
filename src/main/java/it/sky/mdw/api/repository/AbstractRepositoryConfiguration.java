package it.sky.mdw.api.repository;

public abstract class AbstractRepositoryConfiguration implements RepositoryConfiguration {

	private String directory, uri, username, password;

	public AbstractRepositoryConfiguration() {
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
