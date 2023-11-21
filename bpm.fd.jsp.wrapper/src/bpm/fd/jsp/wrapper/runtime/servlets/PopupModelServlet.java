package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

/**
 * This servlet is called to open a drill popup on a FdProject model page.
 * It perform the same thing as a Dashboard deployment but instead of
 * generating a JSP, it generate the full HTML using the DashInstance
 * matching to the sent UUID.
 * 
 *  Each component which want to open a popup on a model should call this servlet
 *  throught the js/ajax.js popupModelPage() function
 *  
 * @author ludo
 *
 */
public class PopupModelServlet extends HttpServlet{
	private Group group;
	private User user;
	
	public void config(Group group, User user){
		this.group = group;
		this.user = user;
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String instanceId = req.getParameter(JSPCanvasGenerator.REQUEST_PARAM_UUID);
		if (instanceId == null){
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing DashInstance UUID in the request");
			return;
		}
		
		try {
			resp.setCharacterEncoding("UTF-8"); 
			DashInstance instance = Controler.getInstance().getInstance(instanceId);
			
			String pageModelName = req.getParameter("modelPage");
			FdModel pageModel = null;
			for(FdModel p : ((MultiPageFdProject)instance.getDashBoard().getProject()).getPagesModels()){
				if (p.getName().equals(pageModelName)){
					pageModel = p;
					break;
				}
			}
			
			JSPCanvasGenerator canvas = new JSPCanvasGenerator(instance.getDashBoard().getMeta(), pageModel);
			String html = canvas.getHtml(instance);
			
			//trick : relative url not the same path
			html = html.replace("../../freedashboardRuntime", "../");
			
			resp.getWriter().write(html);
			
		}  catch(InstantiationException e){
			Logger.getLogger(getClass()).warn(e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
		} catch (Exception e) {
			
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		doPost(req, resp);
	}
}
