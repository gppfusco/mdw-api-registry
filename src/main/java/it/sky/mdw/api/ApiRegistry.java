package it.sky.mdw.api;

public interface ApiRegistry {

	public Registry initializeRegistry(Configuration configuration) throws Exception;

	public void storeFullRegistry(Environment environment, Configuration configuration) throws Exception;

	public void storeRegistryAPIs(Environment environment, Configuration configuration) throws Exception;
	
}
