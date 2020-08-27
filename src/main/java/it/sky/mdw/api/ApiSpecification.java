package it.sky.mdw.api;

import java.util.List;

public interface ApiSpecification {

	String getApiSpecEndpoint();
	
	void setApiSpecEndpoint(String apiSpecEndpoint);
	
	List<XSDSchema> getXsdSchemas();
	
	void setXsdSchemas(List<XSDSchema> xsdExternalRef);
	
	byte[] getApiSpecContent();
	
	void setApiSpecContent(byte[] apiSpecContent);
}
