package bpm.fwr.server.tools;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.server.security.FwrSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;

import com.thoughtworks.xstream.XStream;

public class PreviewComponentServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FwrSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, FwrSession.class);
		} catch (Exception e2) {
			e2.printStackTrace();
			throw new ServletException("Unable to get FWR Session: " + e2.getMessage());
		}
		
		String type = req.getParameter("type");
		String customSize = req.getParameter("customSize");
		
		IReportComponent comp = session.getSavedComponent();
		
		FWRReport report = Transformer.getReportForComponentPreview(comp, type, session, customSize);
		
		XStream xstream = new XStream();
		String reportXML = xstream.toXML(report);
		InputStream reportModel = IOUtils.toInputStream(reportXML, "UTF-8");

		ObjectIdentifier objectId = new ObjectIdentifier(session.getCurrentRepository().getId(), -1);
		
		ServerReportHelper server = new ServerReportHelper(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());
		InputStream is = null;
		try {
			is = server.runReport(report.getOutput().toLowerCase(), reportModel, session.getCurrentGroup().getId(), objectId, session.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (report.getOutput().equalsIgnoreCase("html")) {
		   resp.setContentType("text/html");
		}
		else if (report.getOutput().equalsIgnoreCase("pdf")) {
		   resp.setContentType("application/pdf");
		}
		  
		ServletOutputStream out = resp.getOutputStream();
		  
		byte buffer[]=new byte[512*1024]; 
		int nbLecture;

		while((nbLecture = is.read(buffer)) != -1 ){
		   out.write(buffer, 0, nbLecture);
		}
		is.close();
		out.close();
	}

	
	
}
