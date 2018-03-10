package akt.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;

/**
 * @author akt.
 */
public class ObjectMapperUtil {

	public static String toString(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		return objectMapper.writeValueAsString(object);
	}

	public static <T> T toObject(String content, Class<T> tClass) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		return objectMapper.readValue(content, tClass);
	}

}
