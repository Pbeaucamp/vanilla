package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.PublicParameter;




public class PublicParameterDigester {

	private PublicParameter o;

	@SuppressWarnings(value = "unchecked")
	public PublicParameterDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "publicparameter";

		dig.addObjectCreate(root , PublicParameter.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/publicurlid", "setPublicUrlId", 0);
		dig.addCallMethod(root + "/parametername", "setParameterName", 0);
		dig.addCallMethod(root + "/parametervalue", "setParameterValue", 0);

		
		try {
			o = (PublicParameter) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PublicParameter getPublicParameter() {
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
