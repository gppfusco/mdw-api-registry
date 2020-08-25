package it.sky.mdw.api;

public interface ApiRegistry {

	Registry initializeRegistry(Configuration configuration) throws Exception;

	void storeFullRegistry(Environment environment, Configuration configuration) throws Exception;

	void storeRegistryAPIs(Environment environment, Configuration configuration) throws Exception;
	
	IntegrationScenario getIntegrationScenario(Api<? extends ApiSpecification> api);
	
	RegistryContext getRegistryContext();
	
}
