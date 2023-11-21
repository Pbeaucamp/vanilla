package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.UserRep;

public class UserRepDigester {

	private UserRep o;

	@SuppressWarnings(value = "unchecked")
	public UserRepDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "userrep";

		dig.addObjectCreate(root , UserRep.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/userId", "setUserId", 0);
		dig.addCallMethod(root + "/repositoryId", "setRepositoryId", 0);
		
		try {
			o = (UserRep) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UserRep getUserRep() {
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
