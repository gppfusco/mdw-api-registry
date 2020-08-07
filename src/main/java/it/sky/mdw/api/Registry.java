package it.sky.mdw.api;

import java.util.ArrayList;
import java.util.Collection;

public class Registry {

	private Collection<Api<? extends ApiSpecification>> apis;
	
	public Registry() {
		apis = new ArrayList<Api<? extends ApiSpecification>>();
	}

	public Collection<Api<? extends ApiSpecification>> getApis() {
		return apis;
	}

	public void setApis(Collection<Api<? extends ApiSpecification>> apis) {
		this.apis = apis;
	}

}
