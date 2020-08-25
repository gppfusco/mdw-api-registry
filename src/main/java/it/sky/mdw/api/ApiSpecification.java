package it.sky.mdw.api;

import java.util.List;

public interface ApiSpecification {

	String getApiSpecEndpoint();
	
	void setApiSpecEndpoint(String apiSpecEndpoint);
	
	List<XSDExternalRef> getXsdExternalRef();
	
	void setXsdExternalRef(List<XSDExternalRef> xsdExternalRef);
	
	byte[] getApiSpecContent();
	
	void setApiSpecContent(byte[] apiSpecContent);
}
