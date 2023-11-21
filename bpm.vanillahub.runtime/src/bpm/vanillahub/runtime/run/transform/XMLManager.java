package bpm.vanillahub.runtime.run.transform;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import bpm.vanillahub.core.beans.managers.TransformManager;

public class XMLManager implements TransformManager {

	public static final String CONNECTOR_NAME = "XMLManager";

	@Override
	public ByteArrayInputStream buildFile(ByteArrayInputStream parentStream) throws Exception {
		String xml = IOUtils.toString(parentStream); 
		
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		return new ByteArrayInputStream(xmlJSONObj.toString().getBytes());
	}

}
