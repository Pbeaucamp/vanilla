package bpm.faweb.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.faweb.server.security.FaWebSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;

public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("reportKey");
		int keySession = Integer.parseInt(req.getParameter("keySession"));

		FaWebSession session = null;
		ByteArrayInputStream is = null;
		try {
			session = getSession(req);
		
			is = session.getReport(keySession, name);
			
//			if(format != null){
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals("html")) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					resp.setContentType(mime);
				}
//			}
			
//			if(format.equalsIgnoreCase("odt")){
//				format = format + ".xml";
//			}
//			
//			if(checkout != null && checkout.equalsIgnoreCase("true")){
//				resp.setHeader("Content-disposition", "attachment; filename=" + name + "." + format);
//			}
//			else {
//				resp.setHeader("Content-disposition", "filename=" + name + "." + format);
//			}
			  
			ServletOutputStream out = resp.getOutputStream();
			  
			byte buffer[]=new byte[512*1024]; 
			int nbLecture;
			while((nbLecture = is.read(buffer)) != -1 ){
			   out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		} catch (Exception e) {
			is.close();
			e.printStackTrace();
		} finally {
			if(is != null){
				is.reset();
			}
		}
	}
	
	private FaWebSession getSession(HttpServletRequest req) throws ServletException {
		FaWebSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, FaWebSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}
		
		return session;
	}
}
