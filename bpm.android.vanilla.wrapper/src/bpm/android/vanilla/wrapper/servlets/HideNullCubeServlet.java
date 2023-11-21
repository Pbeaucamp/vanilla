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
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;


/**
 * Servlet implementation class HideNullCubeServlet
 */
public class HideNullCubeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private ComponentAndroidWrapper component;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HideNullCubeServlet(ComponentAndroidWrapper component) {
        super();
        this.component = component;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		String req = IOUtils.toString(is, "UTF-8");
		is.close();
		
		String resp =  null;
		
		try{
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();
			
			resp = prepareResponse(req, session);
		}catch(Exception ex){
			component.getLogger().error("Error when Hiding Empty cells - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}
	
	private String prepareResponse(String req, SessionContent session){
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		String response = "";
		try {
			String html = manager.hideNull();
			response = prepareXml(html, "");
		} catch (Throwable e) {
			e.printStackTrace();
			prepareXml("", e.toString());
		}
		
		return response;
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
