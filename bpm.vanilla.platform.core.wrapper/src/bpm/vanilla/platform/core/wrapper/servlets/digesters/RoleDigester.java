package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Role;


public class RoleDigester {

	private Role o;

	@SuppressWarnings(value = "unchecked")
	public RoleDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "role";

		dig.addObjectCreate(root , Role.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/comment", "setComment", 0);
		dig.addCallMethod(root + "/image", "setImage", 0);
		dig.addCallMethod(root + "/type", "setType", 0);
		dig.addCallMethod(root + "/custom1", "setCustom1", 0);
		dig.addCallMethod(root + "/grant", "setGrants", 0);
		
		try {
			o = (Role) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Role getRole() {
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
