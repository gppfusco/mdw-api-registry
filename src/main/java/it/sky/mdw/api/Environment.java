package it.sky.mdw.api;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="environment")
public interface Environment {

	public Registry getRegistry();
	
	public String getReferenceName();
	
	public String getBaseUrl();
	
}
