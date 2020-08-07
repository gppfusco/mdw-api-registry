package it.sky.mdw.api;

import java.util.List;

public interface ApiSpecification {

	public String getApiSpecEndpoint();
	
	public void setApiSpecEndpoint(String apiSpecEndpoint);
	
	public List<XSDExternalRef> getXsdExternalRef();
	
	public void setXsdExternalRef(List<XSDExternalRef> xsdExternalRef);
	
	public byte[] getApiSpecContent();
	
	public void setApiSpecContent(byte[] apiSpecContent);
}
