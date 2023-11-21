package bpm.fd.design.test.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.DrillState;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.fd.runtime.model.ui.jsp.renderer.ChartRenderer;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.utils.IOWriter;

public class DrillServlet extends HttpServlet{
	private static final String DRILL_DOWN = "drillDown";
	private static final String DRILL_UP = "drillUp";
	private static final String CAN_DRILL_UP = "canDrillUp";
	
	private static final String DRILL_TYPE = "drillType";
	private static final String DRILL_UP_LEVEL = "drillUpLevel";
	
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

		String type = req.getParameter(DRILL_TYPE);
		
		String res = null;
		
		try {
			resp.setCharacterEncoding("UTF-8"); 
			DashInstance instance = Controler.getInstance().getInstance(instanceId);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			IOWriter.write(req.getInputStream(), bos, true, true);
			
			if (DRILL_DOWN.equals(type)){
				
				String[] datas = bos.toString("UTF-8").split("\\=");
				
				String pName = null;
				String pValue = null;
				
				try{
					pName = URLDecoder.decode(datas[0], "UTF-8");
				}catch(Exception ex){
					pName = datas[0];
					Logger.getLogger(getClass()).warn("Could not decode parameterName -> pName= set at " + pName);
				}
				
				try{
					pValue = URLDecoder.decode(datas[1], "UTF-8");
				}catch(Exception ex){
					pValue = datas[1];
					Logger.getLogger(getClass()).warn("Could not decode parameterValue -> pValue= set at " + pValue);
				}
				
				res = instance.drillDown(pName, pValue);
			}
			else if (DRILL_UP.equals(type)){
				
				String componentName = bos.toString("UTF-8");
				
				String lvl = req.getParameter(DRILL_UP_LEVEL);
				if (lvl != null){
					try{
						if (Integer.parseInt(lvl) < 0){
							 instance.getState().getDrillState(componentName).reset();
							 res = componentName;
						}
						else{
							while(instance.getState().getDrillState(componentName).getCurrentLevel()-1 > Integer.parseInt(lvl)){
								res = instance.drillUp(componentName);
							}
						}
						
					}catch(Exception ex){
						res = instance.drillUp(componentName);
					}
				}
				else{
					instance.getState().getDrillState(componentName).reset();
					 res = componentName;
				}
				
			}
			else if (CAN_DRILL_UP.endsWith(type)){
				String componentName = bos.toString("UTF-8");
				DrillState state = instance.getState().getDrillState(componentName);
				res = ChartRenderer.generateArianeHtml(componentName, state);
				//res = instance.getDrillState(componentName).canDrillUp() + "";
			}
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		}
		
		
		try {
			resp.getWriter().write(res);
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
