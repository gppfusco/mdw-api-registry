package it.sky.mdw.api;

public class RestApiSpec extends DefaultApiSpecification {

	private SoapApiSpec hasSoapApiSpec;

	public RestApiSpec() {
		super();
	}

	public RestApiSpec(String apiSpecEndpoint) {
		super(apiSpecEndpoint);
	}

	public SoapApiSpec getSoapApiSpec() {
		return hasSoapApiSpec;
	}

	public void setSoapApiSpec(SoapApiSpec hasSoapApiSpec) {
		this.hasSoapApiSpec = hasSoapApiSpec;
	}

}
