package bpm.gwt.commons.server.ged.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;

public class PreviewFile extends HttpServlet {

	private static final long serialVersionUID = -3976214568756753501L;

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		String key = req.getParameter("key");
		try {
			logger.info("Requested ged file with key : " + key);
			CommonSession session = getSession(req);

			DocumentVersion docVersion = session.getDocumentVersion(key);
			
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), session.getUser().getId());
			
			InputStream is = session.getGedComponent().loadGedDocument(config);

			String format = docVersion.getFormat();
			logger.info("File requested has a format of : " + format);
			
			
			resp.setHeader("Content-Disposition", "attachment; " +
					"filename=\"" + docVersion.getParent().getName() + "." + format + "\"" );
			
			String mime = "";
			for (Formats f : Formats.values()) {
				if (f.getExtension().equals(format)) {
					mime = f.getMime();
					break;
				}
			}
			logger.info("File requested has a mime type of : " + mime);
			if (!mime.equals("")) {
				resp.setContentType(mime);
			}
			ServletOutputStream out = resp.getOutputStream();
			
			byte buffer[]=new byte[512*1024]; 
			int nbLecture;


			while((nbLecture = is.read(buffer)) != -1 ){
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
