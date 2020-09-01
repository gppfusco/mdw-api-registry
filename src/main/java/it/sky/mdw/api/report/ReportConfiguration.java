package it.sky.mdw.api.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.sky.mdw.api.repository.RepositoryConfiguration;

public class ReportConfiguration {
	private List<String> environmentDirEntry = new ArrayList<String>();
	private String directory, uri, username, password;

	public RepositoryConfiguration() {
	}

	public List<String> getEnvironmentDirEntry() {
		return environmentDirEntry;
	}

	public void setEnvironmentDirEntry(List<String> environmentDirEntry) {
		this.environmentDirEntry = environmentDirEntry;
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

	public static RepositoryConfiguration loadFromJSON(InputStream is){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.readValue(is, RepositoryConfiguration.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new RepositoryConfiguration();
		} catch (IOException e) {
			e.printStackTrace();
			return new RepositoryConfiguration();
		}
	}
	
}
