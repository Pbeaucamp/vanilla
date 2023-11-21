package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.PublicUrl;

public class PublicUrlDigester {

	private PublicUrl o;

	@SuppressWarnings(value = "unchecked")
	public PublicUrlDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "publicurl";

		dig.addObjectCreate(root , PublicUrl.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/publickey", "setPublicKey", 0);
		dig.addCallMethod(root + "/groupid", "setGroupId", 0);
		dig.addCallMethod(root + "/userid", "setUserId", 0);
		dig.addCallMethod(root + "/adressableid", "setAdressableId", 0);
		dig.addCallMethod(root + "/creationdate", "setCreationDate", 0);
		dig.addCallMethod(root + "/enddate", "setEndDate", 0);
		dig.addCallMethod(root + "/active", "setActive", 0);
		dig.addCallMethod(root + "/deleted", "setDeleted", 0);
		dig.addCallMethod(root + "/outputformat", "setOutputFormat", 0);
		dig.addCallMethod(root + "/datasourceid", "setDatasourceId", 0);
	
		
		try {
			o = (PublicUrl) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PublicUrl getPublicUrl() {
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
