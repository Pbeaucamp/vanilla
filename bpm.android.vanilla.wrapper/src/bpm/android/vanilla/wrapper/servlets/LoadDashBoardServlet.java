package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.report.IRunItem;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class LoadDashBoardServlet
 */
public class LoadDashBoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadDashBoardServlet(ComponentAndroidWrapper component) {
        super();
       this.component = component;
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		String req = IOUtils.toString(is, "UTF-8");
		is.close();
		
		String resp =  null;
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try{
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();
			
			out.println(getRedirectionUrl(req, session));
		}catch(Exception ex){
			component.getLogger().error("Error when Loading dashboard - " + ex.getMessage(), ex);
			resp = ex.toString();
			out.println(resp);
		}
	}

	private String getRedirectionUrl(String req, SessionContent session){
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		AndroidItem selectedItem = getSelectedItem(req);
		
		String response = "";
		try {
			
			IRunItem runItem = manager.getDashboardUrl(selectedItem);

		    return runItem.getHtml();
		} catch (Throwable e) {
			e.printStackTrace();
			response = prepareXml("", e.toString());
		}
		
		return response;
	}
	
	private AndroidItem getSelectedItem(String req) {
		int beginIndex = req.indexOf("<id>") + "<id>".length();
		int endIndex = req.indexOf("</id>");
		int itemId = Integer.parseInt(req.substring(beginIndex, endIndex));
		
		AndroidItem item = new AndroidItem();
		item.setId(itemId);
		return item;
	}
	
	private String prepareXml(String html, String error){
		if(error.equals("")){
			String toResp;
			toResp  = "<rootElement>\n";
			toResp += "    <htmlpage>\n";
			toResp += html;
			toResp += "    </htmlpage>\n";
			toResp += "</rootElement>";
			
			return toResp;
		}
		else{
			String toResp;
			toResp  = "<error>" + error + "</error>";
			
			return toResp;
		}
	}

}
