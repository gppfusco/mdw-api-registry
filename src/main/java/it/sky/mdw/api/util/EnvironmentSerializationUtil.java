package it.sky.mdw.api.util;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.sky.mdw.api.Environment;
import it.sky.mdw.api.SkyEnvironment;

public final class EnvironmentSerializationUtil {

	public static Environment unmarshall(String filePath) throws Exception{
		return unmarshall(new File(filePath));
	}
	
	public static Environment unmarshall(File filePath) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		JsonNode jsonNode = mapper.readTree(filePath).findPath("environment");

		return mapper.treeToValue(jsonNode, SkyEnvironment.class);
	}	
}
