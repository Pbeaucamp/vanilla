package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.Server;

public class ServerDigester {

	private Server o;

	@SuppressWarnings(value = "unchecked")
	public ServerDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "server";

		dig.addObjectCreate(root , Server.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/description", "setDescription", 0);
		dig.addCallMethod(root + "/url", "setUrl", 0);
		dig.addCallMethod(root + "/type", "setType", 0);
		
		
		try {
			o = (Server) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Server getServer() {
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
