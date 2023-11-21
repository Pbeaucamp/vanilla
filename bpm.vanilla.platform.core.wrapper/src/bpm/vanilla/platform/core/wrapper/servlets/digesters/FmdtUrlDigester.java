package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.FmdtUrl;


public class FmdtUrlDigester {

	private FmdtUrl o;

	@SuppressWarnings(value = "unchecked")
	public FmdtUrlDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "fmdturl";

		dig.addObjectCreate(root , FmdtUrl.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/repositoryid", "setRepositoryId", 0);
		dig.addCallMethod(root + "/itemid", "setItemId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/groupname", "setGroupName", 0);
		dig.addCallMethod(root + "/modelname", "setModelName", 0);
		dig.addCallMethod(root + "/packagename", "setPackageName", 0);
		dig.addCallMethod(root + "/description", "setDescription", 0);
		dig.addCallMethod(root + "/user", "setUser", 0);
		dig.addCallMethod(root + "/password", "setPassword", 0);
		
		try {
			o = (FmdtUrl) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FmdtUrl getFmdtUrl() {
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

