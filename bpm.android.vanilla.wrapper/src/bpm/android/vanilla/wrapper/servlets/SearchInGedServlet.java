package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class SearchInGedServlet
 */
public class SearchInGedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ComponentAndroidWrapper component;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchInGedServlet(ComponentAndroidWrapper component) {
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
		
		try{
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();
			
			resp = prepareResponse(req, session);
		}catch(Exception ex){
			component.getLogger().error("Error Searching in Ged - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}
	
	private String prepareResponse(String request, SessionContent session) {
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		String words = getSearch(request);
		String response = "";
		try {
			List<AndroidDocumentDefinition> docs = manager.searchInGed(words);
			
			response = prepareXml(docs, "");
		}
		catch (Throwable e) {
			e.printStackTrace();
			response = prepareXml(new ArrayList<AndroidDocumentDefinition>(), e.toString());
		}
		return response;
	}
	
	private String getSearch(String request) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(request);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
			
		for(Element e : (List<Element>)document.getRootElement().elements("userEntry")){
			try{
				if (e.element("searchword") != null){
					
					Element d = e.element("searchword");
					if(d != null){
						return d.getStringValue();
					}
				}
			}catch(Throwable ex){
				ex.printStackTrace();
			}
			
		}
		return "";
	}

	private String prepareXml(List<AndroidDocumentDefinition> results, String error){
		if(error.isEmpty()){
			String toResp;
			toResp  = "<rootElement>\n";
			toResp += "    <documents>\n";
			for(AndroidDocumentDefinition d : results){
				toResp += "        <document>\n";
				toResp += "            <title>" + d.getTitle() + "</title>\n";
				toResp += "            <id>" + d.getId() + "</id>\n";
				toResp += "            <version>" + d.getVersion() + "</version>\n";
				toResp += "            <summary>" + d.getSummary() + "</summary>\n";
				toResp += "        </document>\n";
			}
			toResp += "    </documents>\n";
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
