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
	@Type(value = RestApi.class, name = "REST") , 
	@Type(value = MessagingApi.class, name = "Messaging") 
})
public interface Api<S extends ApiSpecification> {

	String getEndpoint();

	String getPath();

	String getLocalPath();

	void setLocalPath(String localPath);

	String getName();

	S getApiSpecification();

	void setApiSpecification(S apiSpecification);

	IntegrationScenario getIntegrationScenario();

	void setIntegrationScenario(IntegrationScenario integrationScenario);

	String getAuthentication();

	void setAuthentication(String auth);
	
	boolean isCompressionSupported();
	
	boolean isBufferingSupported();
	
	void setCompressionSupported(boolean isCompressionSupported);
	
	void setBufferingSupported(boolean isBufferingSupported);
	
	static final String DEFAULT_AUTHENTICATION_TYPE = "Undefined";

}
