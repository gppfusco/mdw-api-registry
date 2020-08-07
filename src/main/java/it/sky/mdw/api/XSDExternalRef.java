package it.sky.mdw.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class XSDExternalRef {

	private String apiEndpoint, xsdBasePath, xsdNameSpace, xsdEndpoint;

	@JsonIgnore
	private byte[] xsdSchema;

	public XSDExternalRef() {
		this("", "", "");
	}

	public XSDExternalRef(String apiEndpoint, String xsdBasePath, String xsdNameSpace) {
		super();
		this.apiEndpoint = apiEndpoint;
		this.xsdBasePath = xsdBasePath;
		this.xsdNameSpace = xsdNameSpace;
		xsdEndpoint = apiEndpoint + xsdBasePath;
	}

	public String getApiEndpoint() {
		return apiEndpoint;
	}

	public void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}

	public String getXsdBasePath() {
		return xsdBasePath;
	}

	public void setXsdBasePath(String xsdBasePath) {
		this.xsdBasePath = xsdBasePath;
	}

	public String getXsdNameSpace() {
		return xsdNameSpace;
	}

	public void setXsdNameSpace(String xsdNameSpace) {
		this.xsdNameSpace = xsdNameSpace;
	}

	public byte[] getXsdSchema() {
		return xsdSchema;
	}

	public void setXsdSchema(byte[] xsdSchema) {
		this.xsdSchema = xsdSchema;
	}
	
	public String getXsdEndpoint() {
		return xsdEndpoint;
	}
	
	public void setXsdEndpoint(String xsdEndpoint) {
		this.xsdEndpoint = xsdEndpoint;
	}

}
