package it.sky.mdw.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DefaultApiSpecification implements ApiSpecification {

	private String apiSpecEndpoint;
	private List<XSDSchema> xsdSchemas;

	@JsonIgnore
	private byte[] apiSpecContent;

	public DefaultApiSpecification() {
		this("");
	}

	public DefaultApiSpecification(String apiSpecEndpoint) {
		super();
		this.apiSpecEndpoint = apiSpecEndpoint;
		this.xsdSchemas = new ArrayList<>();
	}

	public List<XSDSchema> getXsdSchemas() {
		return xsdSchemas;
	}
	
	public void setXsdSchemas(List<XSDSchema> xsdSchemas) {
		this.xsdSchemas = xsdSchemas;
	}

	public String getApiSpecEndpoint() {
		return apiSpecEndpoint;
	}

	public void setApiSpecEndpoint(String apiSpecEndpoint) {
		this.apiSpecEndpoint = apiSpecEndpoint;
	}

	public byte[] getApiSpecContent() {
		return apiSpecContent;
	}

	public void setApiSpecContent(byte[] apiSpecContent) {
		this.apiSpecContent = apiSpecContent;
	}

}
