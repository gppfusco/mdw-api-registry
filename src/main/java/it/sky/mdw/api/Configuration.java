package it.sky.mdw.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.sky.mdw.api.util.ConfigurationSerializationUtil;

public class Configuration extends Properties  {

	private static final long serialVersionUID = -8864930321456175336L;

	public Configuration() {
		put(ConfigurationKeys.WSDL_DIR_NAME, "wsdl");
		put(ConfigurationKeys.WADL_DIR_NAME, "wadl");
		put(ConfigurationKeys.XSD_DIR_NAME, "xsd");
		put(ConfigurationKeys.ENCRYPTION_ENABLED, false);
		put(ConfigurationKeys.NUMBER_OF_THREADS, 16);
	}

	public void storeToJSON(OutputStream out) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(out, this);
	}

	public static Configuration load(File confFile) throws Exception {
		Configuration conf = new Configuration();
		if(confFile.getName().endsWith(".json"))
			conf = ConfigurationSerializationUtil.loadFromJSON(new FileInputStream(confFile), Configuration.class);
		else if(confFile.getName().endsWith(".xml"))
			conf.loadFromXML(new FileInputStream(confFile));
		else
			conf.load(new FileInputStream(confFile));

		return conf;
	}

}
