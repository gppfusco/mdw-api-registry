package it.sky.mdw.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.InputStream;

public class ConfigurationSerializationUtil {

	public static <C> C loadFromJSON(InputStream is, Class<C> configurationClass) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.readValue(is, configurationClass);
		} catch (Exception e) {
			e.printStackTrace();
			return configurationClass.newInstance();
		}
	}

}
