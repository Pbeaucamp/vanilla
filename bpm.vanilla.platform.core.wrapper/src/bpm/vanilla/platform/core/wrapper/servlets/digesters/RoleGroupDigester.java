package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.RoleGroup;

public class RoleGroupDigester {

	private RoleGroup o;

	@SuppressWarnings(value = "unchecked")
	public RoleGroupDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "rolegroup";

		dig.addObjectCreate(root , RoleGroup.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/groupId", "setGroupId", 0);
		dig.addCallMethod(root + "/roleId", "setRoleId", 0);
		
		try {
			o = (RoleGroup) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RoleGroup getRoleGroup() {
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
