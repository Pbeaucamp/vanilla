package bpm.fwr.server.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import bpm.fwr.server.security.FwrSession;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

public class RunServlet extends HttpServlet {

	private static final long serialVersionUID = 2366373193933769244L;

	// @Override
	// protected void service(HttpServletRequest req, HttpServletResponse resp)
	// throws ServletException, IOException {
	//
	// FwrSession session;
	// try {
	// session = CommonSessionHelper.getCurrentSession(req, FwrSession.class);
	// } catch(Exception e2) {
	// e2.printStackTrace();
	// throw new ServletException("Unable to get FWR Session: " +
	// e2.getMessage());
	// }
	//
	// FWRReport reportConfig = session.getSavedReport();
	//
	// FWRReport report = (FWRReport) reportConfig;
	//
	// ObjectIdentifier objectId = new
	// ObjectIdentifier(session.getCurrentRepository().getId(), -1);
	//
	// XStream xstream = new XStream();
	// String reportXML = xstream.toXML(report);
	// InputStream in = IOUtils.toInputStream(reportXML, "UTF-8");
	//
	// ServerReportHelper server = new
	// ServerReportHelper(session.getVanillaRuntimeUrl(),
	// session.getUser().getLogin(), session.getUser().getPassword());
	//
	// InputStream is = null;
	// try {
	// is = server.runReport(report.getOutput().toLowerCase(), in,
	// session.getCurrentGroup().getId(), objectId, session.getUser());
	// } catch(Exception e) {
	// e.printStackTrace();
	// }
	//
	// if(report.getOutput().equalsIgnoreCase("html")) {
	// resp.setContentType("text/html");
	// }
	// else if(report.getOutput().equalsIgnoreCase("pdf")) {
	// resp.setContentType("application/pdf");
	// // resp.setHeader("Content-disposition", "filename=" + "FWR_" + new
	// Object().hashCode() + ".pdf");
	// resp.setHeader("Content-disposition", "attachment; filename=" + "FWR_" +
	// new Object().hashCode() + ".pdf");
	// }
	// else if(report.getOutput().equalsIgnoreCase("excel") ||
	// report.getOutput().equalsIgnoreCase("Excel (Plain List)")) {
	// resp.setContentType("application/excel");
	// resp.setHeader("Content-disposition", "attachment; filename=" + "FWR_" +
	// new Object().hashCode() + ".xls");
	// }
	// else if(report.getOutput().equalsIgnoreCase("doc")) {
	// resp.setContentType("application/msword");
	// resp.setHeader("Content-disposition", "attachment; filename=" + "FWR_" +
	// new Object().hashCode() + ".doc");
	// }
	//
	// ServletOutputStream out = resp.getOutputStream();
	//
	// byte buffer[] = new byte[512 * 1024];
	// int nbLecture;
	//
	// while((nbLecture = is.read(buffer)) != -1) {
	// out.write(buffer, 0, nbLecture);
	// }
	// is.close();
	// out.close();
	// }

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(CommonConstants.REPORT_HASHMAP_NAME);
		String format = req.getParameter(CommonConstants.REPORT_OUTPUT);

		ByteArrayInputStream is = null;
		try {
			CommonSession session = getSession(req);

			ObjectInputStream rep = session.getReport(name);
			is = rep.getStream(format);

			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					resp.setContentType(mime);
				}
			}

			if (format.equalsIgnoreCase("html") || format.equalsIgnoreCase("pdf")) {
				resp.setHeader("Content-disposition", "filename=" + name + "." + format);
//				resp.setHeader("Content-disposition", "attachment; filename=" + "FWR_" + new Object().hashCode() + ".pdf");
			}
			else {
				resp.setHeader("Content-disposition", "attachment; filename=" + name + "." + format);
			}

			ServletOutputStream out = resp.getOutputStream();

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = is.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();

			InputStream errorStream = createHTMLErrorResponse(e);

			ServletOutputStream out = resp.getOutputStream();
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = errorStream.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			errorStream.close();
			out.close();
		} finally {
			if (is != null) {
				is.reset();
			}
		}
	}

	private InputStream createHTMLErrorResponse(Throwable caught) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("	<head></head>");
		buf.append("	<body>");
		buf.append("		<h1>Informations</h1>");
		buf.append("		<p>This document is not runnable at this time. Sorry for the inconveniance. <br/> The following informations can help you understand the problem :</p>");
		buf.append("		<p style=\"margin: 15px; background-color: #E6E6E6; padding: 20px; font-size: 12px;\">" + ExceptionUtils.getStackTrace(caught).replace("\n", "<br/>") + "</p>");
		buf.append("	</body>");
		buf.append("</html>");
		return IOUtils.toInputStream(buf.toString());
	}

	private FwrSession getSession(HttpServletRequest req) throws ServletException {
		FwrSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, FwrSession.class);
		} catch (Exception e2) {
			e2.printStackTrace();
			throw new ServletException("Unable to get Vanilla Report Session: " + e2.getMessage());
		}
		return session;
	}
}
