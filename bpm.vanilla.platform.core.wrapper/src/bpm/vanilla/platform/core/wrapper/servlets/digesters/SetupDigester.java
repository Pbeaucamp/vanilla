package bpm.vanilla.platform.core.wrapper.servlets.digesters;

import java.io.InputStream;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXParseException;

import bpm.vanilla.platform.core.beans.VanillaSetup;

public class SetupDigester {

	private VanillaSetup o;

	@SuppressWarnings(value = "unchecked")
	public SetupDigester(InputStream is) {
		Digester dig = new Digester();
		dig.setValidating(false);
		dig.setErrorHandler(new ErrorHandler());

		String root = "setup";

		dig.addObjectCreate(root , VanillaSetup.class);
		dig.addCallMethod(root + "/id", "setId", 0);
		dig.addCallMethod(root + "/language", "setPortalLanguage", 0);
		dig.addCallMethod(root + "/authentification", "setAuthentification", 0);
		dig.addCallMethod(root + "/birtViewerPath", "setBirtViewerPath", 0);
		dig.addCallMethod(root + "/birtViewer", "setBirtViewer", 0);
		dig.addCallMethod(root + "/fasdWebServer", "setFasdWebServer", 0);
		dig.addCallMethod(root + "/freeAnalysisServer", "setFreeAnalysisServer", 0);
		dig.addCallMethod(root + "/freeDashboardServer", "setFreeDashboardServer", 0);
		dig.addCallMethod(root + "/freeMetricsServer", "setFreeMetricsServer", 0);
		dig.addCallMethod(root + "/freeWebReportServer", "setFreeWebReportServer", 0);
		dig.addCallMethod(root + "/securityServer", "setSecurityServer", 0);
		dig.addCallMethod(root + "/workbook", "setWorkbook", 0);
		dig.addCallMethod(root + "/vanillaServer", "setVanillaServer", 0);
		dig.addCallMethod(root + "/path", "setPath", 0);
		dig.addCallMethod(root + "/orbeonpath", "setOrbeonPath", 0);
		dig.addCallMethod(root + "/orbeonurl", "setOrbeonUrl", 0);
		dig.addCallMethod(root + "/schedulerServerUrl", "setSchedulerServer", 0);
		dig.addCallMethod(root + "/gatewayServerUrl", "setGatewayServerUrl", 0);
		dig.addCallMethod(root + "/fmdtServerUrl", "setFmdtServerUrl", 0);
		dig.addCallMethod(root + "/reportingServerUrl", "setReportingServerUrl", 0);
		dig.addCallMethod(root + "/googleKey", "setGoogleKey", 0);
		dig.addCallMethod(root + "/reportengine", "setReportEngine", 0);
		dig.addCallMethod(root + "/vanillafiles", "setVanillaFiles", 0);
		dig.addCallMethod(root + "/vanillaruntimeserverurl", "setVanillaRuntimeServersUrl", 0);
		
		try {
			o = (VanillaSetup) dig.parse(is);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VanillaSetup getVanillaSetup() {
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

