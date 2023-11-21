package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Repository;


public class RepositoryDigester {

	private Repository o;

	@SuppressWarnings(value = "unchecked")
	public RepositoryDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "repository";

		dig.addObjectCreate(root , Repository.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/url", "setUrl", 0);
		dig.addCallMethod(root + "/societe", "setSociete", 0);
		dig.addCallMethod(root + "/key", "setKey", 0);
		
		try {
			o = (Repository) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Repository getRepository() {
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
