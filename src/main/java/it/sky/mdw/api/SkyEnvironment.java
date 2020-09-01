package it.sky.mdw.api;

public class SkyEnvironment implements Environment{

	private String referenceName, baseUrl, envDir;
	private Registry registry;
	
	public SkyEnvironment() {
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public String getEnvDir() {
		return envDir;
	}
	
	public void setEnvDir(String envDir) {
		this.envDir = envDir;
	}

}
