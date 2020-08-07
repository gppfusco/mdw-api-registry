package it.sky.mdw.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({ 
	@Type(value = SoapApi.class, name = "SOAP"), 
	@Type(value = RestApi.class, name = "REST") 
})
public interface Api<S extends ApiSpecification> {

	public String getEndpoint();

	public String getPath();
	
	public String getLocalPath();
	
	public void setLocalPath(String localPath);

	public String getName();

	public S getApiSpecification();

	public void setApiSpecification(S apiSpecification);
}
