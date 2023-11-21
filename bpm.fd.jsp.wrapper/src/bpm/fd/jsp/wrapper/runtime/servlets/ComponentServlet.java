package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.controler.InstanceException;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ComponentServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8"); 
//		String instanceid = (String)req.getSession().getAttribute(JSPCanvasGenerator.HTTP_HEADER_INSTANCE_UUID);
//		DashInstance instance = Controler.getInstance().getInstance(instanceid);
		String instanceId = req.getParameter(JSPCanvasGenerator.REQUEST_PARAM_UUID);
		if (instanceId == null){
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing DashInstance UUID in the request");
			return;
		}
		
		try {
			DashInstance instance = Controler.getInstance().getInstance(instanceId);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			IOWriter.write(req.getInputStream(), bos, true, true);
			String componentName = bos.toString();
			String html = instance.refreshComponent(componentName);
			resp.getWriter().write(html);
		} catch(InstanceException e){
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
