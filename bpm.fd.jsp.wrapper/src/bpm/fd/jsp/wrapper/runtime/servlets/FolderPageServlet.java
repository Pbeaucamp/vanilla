package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.controler.InstanceException;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.utils.IOWriter;

public class FolderPageServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String instanceId = req.getParameter(JSPCanvasGenerator.REQUEST_PARAM_UUID);
		boolean isAll = Boolean.parseBoolean(req.getParameter("_all"));
		
		if (instanceId == null){
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing DashInstance UUID in the request");
			return;
		}
		
		try {
			DashInstance instance = Controler.getInstance().getInstance(instanceId);
			
			if(!isAll) {
				String actual = instance.getState().getComponentValue("folder");
				resp.getWriter().write(actual);
			}
			else {
				ComponentRuntime component = instance.getDashBoard().getComponent("folder");
			
				String folders = "";
				boolean first = true;
				for(IBaseElement p : ((Folder)component.getElement()).getContent()) {
					if(first) {
						first = false;
					}
					else {
						folders += ";";
					}
					FolderPage page = (FolderPage) p;
					folders += page.getName();
				}
				resp.getWriter().write(folders);
			}
			
		} catch(InstanceException e){
			Logger.getLogger(getClass()).warn(e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
