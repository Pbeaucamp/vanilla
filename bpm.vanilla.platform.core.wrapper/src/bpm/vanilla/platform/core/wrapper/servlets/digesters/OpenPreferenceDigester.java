package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.OpenPreference;


public class OpenPreferenceDigester {
	private OpenPreference o;

	@SuppressWarnings(value = "unchecked")
	public OpenPreferenceDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "open";

		dig.addObjectCreate(root , OpenPreference.class);

		dig.addCallMethod(root + "/id", "setStringId", 0);
		dig.addCallMethod(root + "/itemId", "setStringItemId", 0);
		dig.addCallMethod(root + "/itemName", "setItemName", 0);
		dig.addCallMethod(root + "/userId", "setStringUserId", 0);

		try {
			o = (OpenPreference) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OpenPreference getOpenPreference() {
		return o;
	}

	public class ErrorHandler implements org.xml.sax.ErrorHandler {
		public void warning(SAXParseException ex) throws SAXParseException {
			throw ex;
		}

		public void error(SAXParseException ex) throws SAXParseException {
			throw ex;
		}

		public void fatalError(SAXParseException ex) throws SAXParseException {
			throw ex;
		}
	}
}


