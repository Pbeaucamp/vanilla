package bpm.fd.design.test.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;

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
			
//			//XXX Trick for the preview
//			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
//			html = html.replace(vanillaUrl, "http://localhost:9191");
			
			resp.getWriter().write(html);
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
