package it.sky.mdw.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Configuration extends Properties implements ConfigurationKeys {

	private static final long serialVersionUID = -8864930321456175336L;

	public Configuration() {
		put(WSDL_DIR_NAME, "wsdl");
		put(WADL_DIR_NAME, "wadl");
		put(XSD_DIR_NAME, "xsd");
	}

	public Configuration loadFromJSON(InputStream is){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.readValue(is, Configuration.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new Configuration();
		} catch (IOException e) {
			e.printStackTrace();
			return new Configuration();
		}
	}

	public void storeToJSON(OutputStream out) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(out, this);
	}

}