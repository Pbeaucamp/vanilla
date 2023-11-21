package bpm.fwr.server.tools;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fwr.server.security.FwrSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;

public class PreviewMapServlet extends HttpServlet {
	
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
		
		String swrUrl = req.getParameter("swfURL");

		StringBuilder builder = new StringBuilder();
		builder.append("<html>");
		builder.append("    <head>");
		builder.append("        <title>Test Map</title>");
		builder.append("        <script language=\"JavaScript\" src=\"" + session.getVanillaRuntimeUrl() + "fusionMap/Maps/FusionMaps.js" +  "\"></script>");
		builder.append("    </head>");
		builder.append("    <body>");
		builder.append("                    <div id=\"mapdiv\" align=\"center\">");
		builder.append("                        FusionMaps.");
		builder.append("                    </div>");
		builder.append("                    <script type=\"text/javascript\">");
		builder.append("                        var map = new FusionMaps(\"" + session.getVanillaRuntimeUrl() + "fusionMap/Maps/" + swrUrl + "\", \"flan\", \"420\", \"225\", \"0\");");
		builder.append("                        map.setDataXML(\"<map showLabels='0'></map>\");");
		builder.append("                        map.render(\"mapdiv\");");
		builder.append("                    </script>");
		builder.append("     </body>");
		builder.append("</html>");
		
		resp.setContentType("text/html");
		
		PrintWriter writer = resp.getWriter();
		writer.append(builder.toString());
		writer.close();
	}

	
	
}
