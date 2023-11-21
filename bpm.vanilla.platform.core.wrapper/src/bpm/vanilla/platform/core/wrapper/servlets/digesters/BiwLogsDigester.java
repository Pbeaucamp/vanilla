package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.BiwLogs;

public class BiwLogsDigester {

	private BiwLogs o;

	@SuppressWarnings(value = "unchecked")
	public BiwLogsDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "log";

		dig.addObjectCreate(root , BiwLogs.class);
		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/repositoryID", "setRepositoryID", 0);
		dig.addCallMethod(root + "/directoryItem", "setDirectoryItem", 0);
		dig.addCallMethod(root + "/runid", "setRunidString", 0);
		dig.addCallMethod(root + "/runstart", "setRunstartString", 0);
		dig.addCallMethod(root + "/processID", "setProcessID", 0);
		dig.addCallMethod(root + "/activityID", "setActivityID", 0);
		dig.addCallMethod(root + "/startDate", "setStartDateString", 0);
		dig.addCallMethod(root + "/stopDate", "setStopDateString", 0);
		dig.addCallMethod(root + "/difference", "setDifference", 0);
		
		
		try {
			o = (BiwLogs) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BiwLogs getBiwLogs() {
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

