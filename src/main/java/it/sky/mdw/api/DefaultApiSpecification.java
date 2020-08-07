package it.sky.mdw.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DefaultApiSpecification implements ApiSpecification {

	private String apiSpecEndpoint;
	private List<XSDExternalRef> xsdExternalRef;

	@JsonIgnore
	private byte[] apiSpecContent;

	public DefaultApiSpecification() {
		this("");
	}

	public DefaultApiSpecification(String apiSpecEndpoint) {
		super();
		this.apiSpecEndpoint = apiSpecEndpoint;
		this.xsdExternalRef = new ArrayList<XSDExternalRef>();
	}

	public List<XSDExternalRef> getXsdExternalRef() {
		return xsdExternalRef;
	}

	public void setXsdExternalRef(List<XSDExternalRef> xsdExternalRef) {
		this.xsdExternalRef = xsdExternalRef;
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
