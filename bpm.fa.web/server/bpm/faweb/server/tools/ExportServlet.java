package bpm.faweb.server.tools;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2366373193933769244L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String output = (String)req.getSession().getAttribute("output");
		InputStream report = (InputStream)req.getSession().getAttribute("faReport");
		String name = (String)req.getSession().getAttribute("reportName");
		
		if (output.equalsIgnoreCase("html")) {
		    resp.setContentType("text/html");
		}
		else if (output.equalsIgnoreCase("pdf")) {
		    resp.setContentType("application/pdf");
		}
		else if (output.equalsIgnoreCase("excel")) {
		    resp.setContentType("application/vnd.ms-excel");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".xls" + "\"");
		}
		else if (output.equalsIgnoreCase("weka")) {
		    resp.setContentType("application/octet-stream");
		    resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".arff" + "\"");
		}
		  
		ServletOutputStream out = resp.getOutputStream();
		  
		byte buffer[]=new byte[512*1024]; 
		int nbLecture;

		while((nbLecture = report.read(buffer)) != -1 ){
		   out.write(buffer, 0, nbLecture);
		}
		report.close();
		out.close();
	}
}
