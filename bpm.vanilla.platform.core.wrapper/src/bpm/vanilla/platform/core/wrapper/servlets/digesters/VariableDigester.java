package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Variable;


public class VariableDigester {
	private Variable o;
	
	
	@SuppressWarnings(value = "unchecked")
	public VariableDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "variable";

		dig.addObjectCreate(root , Variable.class);

		dig.addCallMethod(root + "/id", "setStringId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/value", "setValue", 0);
		dig.addCallMethod(root + "/type", "setType", 0);
		dig.addCallMethod(root + "/source", "setSource", 0);
		dig.addCallMethod(root + "/scope", "setScope", 0);

		
		try {
			o = (Variable) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Variable getVariable() {
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

