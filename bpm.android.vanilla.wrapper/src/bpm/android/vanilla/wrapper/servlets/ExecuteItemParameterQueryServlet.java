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

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * Servlet implementation class ExecuteItemParameterQueryServlet
 */
public class ExecuteItemParameterQueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ComponentAndroidWrapper component;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExecuteItemParameterQueryServlet(ComponentAndroidWrapper component) {
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
			component.getLogger().error("Error when executing parameterQuery - " + ex.getMessage(), ex);
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
			AndroidItem item = getItem(req, session);
			Parameter parameter = getParameter(req);
			
			List<Parameter> params = manager.getParameterValues(item, parameter);
			
			response = prepareXml(item.getId(), params, "");
		} catch (Exception e) {
			e.printStackTrace();
			response = prepareXml(0, new ArrayList<Parameter>(), e.toString());
		}
		
		return response;
	}

	private AndroidItem getItem(String req, SessionContent session) throws Exception {
		int begId = req.indexOf("<id>") + "<id>".length();
		int endId = req.indexOf("</id>");
		
		int itemId = Integer.parseInt(req.substring(begId, endId));

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(session.getRepositoryContext());
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);
		
		AndroidItem androidItem = new AndroidItem(item.getId(), item.getName(), item.getType(), item.getSubtype(), new ArrayList<Parameter>());
		return androidItem;
	}
	
	private Parameter getParameter(String req) {
		int begParent = req.indexOf("<paramParent>") + "<paramParent>".length();
		int endParent = req.indexOf("</paramParent>");
		String parent = req.substring(begParent, endParent);
		
		int begSel = req.indexOf("<selectedValue>") + "<selectedValue>".length();
		int endSel = req.indexOf("</selectedValue>");
		String selectedValue = req.substring(begSel, endSel);
		
		int begParam = req.indexOf("<param>") + "<param>".length();
		int endParam = req.indexOf("</param>");
		String selectedParam = req.substring(begParam, endParam);
		
		Parameter param = new Parameter();
		param.setParamName(selectedParam);
		param.setSelectedValue(selectedValue);

		if(parent != null && !parent.isEmpty()) {
			Parameter paramParent = new Parameter();
			paramParent.setParamName(parent);
			param.setParamParent(paramParent);
		}
		
		return param;
	}

	private String prepareXml(int itemId, List<Parameter> parameters, String error){
		if(error.equals("")){
			StringBuilder buf = new StringBuilder();;
			buf.append("<rootElement>\n");
			buf.append("    <item>\n");
			buf.append("        <id>" + itemId + "</id>\n");
			buf.append("        <parameters>\n");
			for(Parameter param : parameters){
				buf.append("            <parameter>\n");
				buf.append("               <parameterName>" + param.getParamName() + "</parameterName>\n");
				if(param.getParamChild() != null){
					buf.append("               <child>" + param.getParamChild().getParamName() + "</child>\n");
				}
				else {
					buf.append("                    <child></child>\n");
				}
				buf.append("                <parameterchoices>\n");
				//If the item has a list of parameter choices
				for(String paramChoice : param.getValues().keySet()){
					buf.append("                    <parameterchoice>\n");
					buf.append("                        <name>" + paramChoice + "</name>\n");
					buf.append("                    </parameterchoice>\n");
				}
				buf.append("                </parameterchoices>\n");
				buf.append("            </parameter>\n");
			}
			buf.append("        </parameters>\n");
			buf.append("    </item>\n");
			buf.append("</rootElement>");
			
			return buf.toString();
		}
		else{
			String toResp;
			toResp  = "<error>" + error + "</error>";
			
			return toResp;
		}
	}
}
