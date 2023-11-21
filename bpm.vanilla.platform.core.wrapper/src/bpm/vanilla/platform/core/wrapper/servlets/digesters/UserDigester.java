package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.User;

public class UserDigester {

	private User o;

	@SuppressWarnings(value = "unchecked")
	public UserDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "user";

		dig.addObjectCreate(root , User.class);

		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/login", "setLogin", 0);
		dig.addCallMethod(root + "/name", "setName", 0);
		dig.addCallMethod(root + "/password", "setPassword", 0);
		dig.addCallMethod(root + "/surname", "setSurname", 0);
		dig.addCallMethod(root + "/function", "setFunction", 0);
		dig.addCallMethod(root + "/telephone", "setTelephone", 0);
		dig.addCallMethod(root + "/cellular", "setCellular", 0);
		dig.addCallMethod(root + "/skypeName", "setSkypeName", 0);
		dig.addCallMethod(root + "/skypeNumber", "setSkypeNumber", 0);
		dig.addCallMethod(root + "/businessMail", "setBusinessMail", 0);
		dig.addCallMethod(root + "/privateMail", "setPrivateMail", 0);
		dig.addCallMethod(root + "/image", "setImage", 0);
		dig.addCallMethod(root + "/fmUserId", "setFmUserId", 0);
		dig.addCallMethod(root + "/superUser", "setSuperUser", 0);
		dig.addCallMethod(root + "/passwordencrypted", "setPasswordEncrypted", 0);
		
		dig.addCallMethod(root + "/passwordReset", "setPasswordResetString", 0);
		dig.addCallMethod(root + "/passwordChange", "setPasswordChangeString", 0);
		
		try {
			o = (User) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
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
