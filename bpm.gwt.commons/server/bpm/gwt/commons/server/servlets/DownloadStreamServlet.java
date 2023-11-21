package bpm.gwt.commons.server.servlets;

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

import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

public class DownloadStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(CommonConstants.STREAM_HASHMAP_NAME);
		String format = req.getParameter(CommonConstants.STREAM_HASHMAP_FORMAT);

		ByteArrayInputStream is = null;
		try {
			CommonSession session = getSession(req);

			is = session.getStream(name, format);
			
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
			resp.setHeader("Content-disposition", "attachment; filename=" + name + "." + format);

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
		buf.append("		<p>This document is not available at this time. Sorry for the inconveniance. <br/> The following informations can help you understand the problem :</p>");
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
