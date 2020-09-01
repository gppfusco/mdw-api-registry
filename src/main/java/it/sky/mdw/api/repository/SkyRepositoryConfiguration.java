package it.sky.mdw.api.repository;

import java.util.ArrayList;
import java.util.List;

public class SkyRepositoryConfiguration extends AbstractRepositoryConfiguration {

	private List<String> environmentDirEntries = new ArrayList<String>();

	public SkyRepositoryConfiguration() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getEnvironmentDirEntries() {
		return environmentDirEntries;
	}

	public void setEnvironmentDirEntries(List<String> environmentDirEntries) {
		this.environmentDirEntries = environmentDirEntries;
	}
}
