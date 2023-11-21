package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class ZoomChartServlet extends HttpServlet{
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
			
			String componentName = req.getParameter("componentName");
			int width = 100;
			try{
				width = Integer.parseInt(req.getParameter("width"));
			}catch(Exception ex){
				
			}
			int height = 50;
			try{
				height = Integer.parseInt(req.getParameter("height"));
			}catch(Exception ex){
				
			}
			
			
			JSPCanvasGenerator canvas = new JSPCanvasGenerator(instance.getDashBoard().getMeta(), instance.getDashBoard().getProject().getFdModel());
						
			String html = canvas.getZoomedHtml(instance, componentName, width, height);
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
