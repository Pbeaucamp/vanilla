package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Group;



public class GroupDigester {

	private Group o;

	@SuppressWarnings(value = "unchecked")
	public GroupDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "group";

		dig.addObjectCreate(root , Group.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/comment", "setComment", 0);
		dig.addCallMethod(root + "/creation", "setCreation", 0);
		dig.addCallMethod(root + "/image", "setImage", 0);
		dig.addCallMethod(root + "/custom1", "setCustom1", 0);
		dig.addCallMethod(root + "/parentId", "setParentId", 0);
		
		try {
			o = (Group) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Group getGroup() {
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
