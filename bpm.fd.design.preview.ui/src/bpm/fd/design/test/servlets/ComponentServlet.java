package bpm.fd.design.test.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ComponentServlet extends HttpServlet{
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
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			IOWriter.write(req.getInputStream(), bos, true, true);
			String componentName = bos.toString();
			String html = instance.refreshComponent(componentName);
			
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
