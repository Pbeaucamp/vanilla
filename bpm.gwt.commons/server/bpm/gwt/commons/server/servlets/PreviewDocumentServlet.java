package bpm.gwt.commons.server.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.repository.ReportBackground;

public class PreviewDocumentServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String documentId = req.getParameter(CommonConstants.DOCUMENT_ID);
		String documentType = req.getParameter(CommonConstants.DOCUMENT_TYPE);
		String format = req.getParameter(CommonConstants.REPORT_OUTPUT);

		String reportName = "";
		boolean bigFile = false;
		
		InputStream is = null;
		try {
			CommonSession session = getSession(req);

			int docId = -1;
			int docType = -1;
			if (documentId != null) {
				try {
					docId = Integer.parseInt(documentId);
					docType = Integer.parseInt(documentType);
				} catch (Exception e) {
					throw new Exception("The document with the ID '" + documentId + "' is not available: " + e.getMessage());
				}
			}
			
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
			
			if (docType == CommonConstants.DOCUMENT_TYPE_BACKGROUND) {
				ReportBackground report = session.getRepositoryConnection().getReportHistoricService().getReportBackground(docId);
				reportName = report.getName();
				bigFile = report.getSize() > CommonConstants.BIG_FILE;
				
				is = new FileInputStream(report.getPath());
			}
			
			if (bigFile || format.equalsIgnoreCase("pht")) {
				resp.setHeader("Content-disposition", "attachment; filename=" + reportName + "." + format);
			}
			else {
				resp.setHeader("Content-disposition", "filename=" + reportName + "." + format);
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
				is.close();
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

	private CommonSession getSession(HttpServletRequest req) throws ServletException {
		CommonSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, CommonSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}

		return session;
	}
}
