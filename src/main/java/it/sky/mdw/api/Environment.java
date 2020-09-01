package it.sky.mdw.api;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value="environment")
public interface Environment {

	Registry getRegistry();
	
	String getReferenceName();
	
	String getBaseUrl();
	
	String getEnvDir();
	
}
