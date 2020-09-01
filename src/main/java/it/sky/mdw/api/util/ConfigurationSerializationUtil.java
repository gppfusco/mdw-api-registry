package it.sky.mdw.api.util;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ConfigurationSerializationUtil {

	public static <C> C loadFromJSON(InputStream is, Class<C> configurationClass) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.readValue(is, configurationClass);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return configurationClass.newInstance();
		} catch (IOException e) {
			e.printStackTrace();
			return configurationClass.newInstance();
		}
	}

}
