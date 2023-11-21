package bpm.vanilla.api.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JSONHelper {

	public static final String transformToJSon(APIAction action) throws Exception {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(action);
	}
}
