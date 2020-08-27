package it.sky.mdw.api;

public abstract class AbstractApi<S extends ApiSpecification> implements Api<S>{

	private String endpoint, path, name, localPath, authentication = DEFAULT_AUTHENTICATION_TYPE;
	private S apiSpecification;
	private IntegrationScenario integrationScenario = IntegrationScenario.NOT_DEFINED;
	private boolean isCompressionSupported = false, isBufferingSupported = false;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getLocalPath() {
		return localPath;
	}
	
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public S getApiSpecification() {
		return apiSpecification;
	}

	public void setApiSpecification(S apiSpecification) {
		this.apiSpecification = apiSpecification;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public IntegrationScenario getIntegrationScenario() {
		return integrationScenario;
	}
	
	public void setIntegrationScenario(IntegrationScenario integrationScenario) {
		this.integrationScenario = integrationScenario;
	}
	
	public String getAuthentication() {
		return authentication;
	}
	
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public boolean isBufferingSupported() {
		return isBufferingSupported;
	}
	
	public boolean isCompressionSupported() {
		return isCompressionSupported;
	}
	
	public void setCompressionSupported(boolean isCompressionSupported) {
		this.isCompressionSupported = isCompressionSupported;
	}
	
	public void setBufferingSupported(boolean isBufferingSupported) {
		this.isBufferingSupported = isBufferingSupported;
	}
	
	@Override
	public String toString() {
		return name + " [" + endpoint + path + "]";
	}

}
