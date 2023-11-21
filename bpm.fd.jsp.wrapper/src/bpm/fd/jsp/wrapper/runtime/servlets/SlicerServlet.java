package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.SlicerState;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.utils.IOWriter;

public class SlicerServlet extends HttpServlet{
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
		List<String> l;
		try {
			resp.setCharacterEncoding("UTF-8"); 
			DashInstance instance = Controler.getInstance().getInstance(instanceId);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			IOWriter.write(req.getInputStream(), bos, true, true);
			String[] datas = bos.toString("UTF-8").split(";");
			String componentName = req.getParameter("componentName");
			SlicerState state = instance.getState().getSlicerState(componentName);
			
			synchronized (state) {
				state.clear();
				for(int i = 0; i < datas.length; i++){
					
					String[] lvlValues = datas[i].split("_");
					
					state.addLevelValue(lvlValues.length -1 , URLDecoder.decode(lvlValues[lvlValues.length -1], "UTF-8"));
				}
			}

			StringBuffer buf = new StringBuffer();
			boolean first = true;
			//we call the setParameter to set the dirty state of the slicer's dependant items
			for(String c : instance.setParameter(componentName,  bos.toString("UTF-8"))){
				if(first){first = false;}else{buf.append(";");}
				buf.append(c);
			}

			resp.getWriter().write(buf.toString());
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
