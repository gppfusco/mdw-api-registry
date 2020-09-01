package it.sky.mdw.api.report;

public class DefaultReportConfiguration implements ReportConfiguration {

	private String explorerLocalBasePath, githubURLBaseDoc, osbRegistryFile, esbRegistryFile;

	public DefaultReportConfiguration() {
	}

	public String getEsbRegistryFile() {
		return esbRegistryFile;
	}

	public void setEsbRegistryFile(String esbRegistryFile) {
		this.esbRegistryFile = esbRegistryFile;
	}

	public String getOsbRegistryFile() {
		return osbRegistryFile;
	}

	public void setOsbRegistryFile(String osbRegistryFile) {
		this.osbRegistryFile = osbRegistryFile;
	}

	public String getExplorerLocalBasePath() {
		return explorerLocalBasePath;
	}

	public void setExplorerLocalBasePath(String explorerLocalBasePath) {
		this.explorerLocalBasePath = explorerLocalBasePath;
	}

	public String getGithubURLBaseDoc() {
		return githubURLBaseDoc;
	}

	public void setGithubURLBaseDoc(String githubURLBaseDoc) {
		this.githubURLBaseDoc = githubURLBaseDoc;
	}

}
