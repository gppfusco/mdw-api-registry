package it.sky.mdw.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Configuration extends Properties  {

	private static final long serialVersionUID = -8864930321456175336L;

	public Configuration() {
		put(ConfigurationKeys.WSDL_DIR_NAME, "wsdl");
		put(ConfigurationKeys.WADL_DIR_NAME, "wadl");
		put(ConfigurationKeys.XSD_DIR_NAME, "xsd");
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

	public static Configuration load(File confFile) throws Exception {
		Configuration conf = new Configuration();
		if(confFile.getName().endsWith(".json"))
			conf = new Configuration().loadFromJSON(new FileInputStream(confFile));
		else if(confFile.getName().endsWith(".xml"))
			conf.loadFromXML(new FileInputStream(confFile));
		else
			conf.load(new FileInputStream(confFile));

		return conf;
	}

}
