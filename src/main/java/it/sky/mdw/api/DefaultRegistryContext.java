package it.sky.mdw.api;

import it.sky.mdw.api.network.ApiNetwork;
import it.sky.mdw.api.network.DefaultApiNetwork;

public class DefaultRegistryContext implements RegistryContext {

	protected ApiNetwork apiNetwork;
	
	public DefaultRegistryContext() {
		this.apiNetwork = new DefaultApiNetwork();
	}
	
	public final ApiNetwork getApiNetwork() {
		return apiNetwork;
	}
}
