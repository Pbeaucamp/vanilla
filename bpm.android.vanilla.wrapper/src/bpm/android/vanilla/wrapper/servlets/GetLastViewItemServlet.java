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
 * Servlet implementation class GetLastViewItemServlet
 */
public class GetLastViewItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ComponentAndroidWrapper component;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLastViewItemServlet(ComponentAndroidWrapper component) {
        super();
        this.component = component;
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		String req = IOUtils.toString(is);
		is.close();
		
		String resp =  null;
		
		try{
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();
			
			resp = prepareResponse(req, session);
		}catch(Exception ex){
			component.getLogger().error("Error when getting last Items - " + ex.getMessage(), ex);
			resp = ex.toString();
		}
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}
	
	private String prepareResponse(String req, SessionContent session){
		IAndroidRepositoryManager manager = session.getRepositoryManager();
		
		AndroidItem selectedItem = getSelectedItem(req);
		
		String response = "";
		try {
			IRunItem runItem = manager.getItemLastView(selectedItem);

			String previewType = "text/html";
			response = prepareXml(runItem, previewType, "");
		} catch (Throwable e) {
			e.printStackTrace();
			try{
				prepareXml(null, "",e.toString());
			}catch(Exception ex){
				ex.printStackTrace();
			}
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
	
	private String prepareXml(IRunItem runItem, String previewType, String error){
		if(error.isEmpty()){
			String toResp;
			toResp  = "<rootElement>\n";
			toResp += "    <previewitem>\n";
			toResp += "        <htmlpage>\n" + runItem.getHtml() + "</htmlpage>\n";
			toResp += "        <previewtype>" + previewType + "</previewtype>\n";
			toResp += "    </previewitem>\n";
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
