package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.controler.Controler;
import bpm.fd.runtime.model.ui.jsp.JSPCanvasGenerator;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ParameterServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8"); 
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
				pValue = pValue.replace("&#39;","'");
			}catch(Exception ex){
				pValue = datas[1];
				pValue = pValue.replace("&#39;","'");
				Logger.getLogger(getClass()).warn("Could not decode parameterValue -> pValue= set at " + pValue);
			}
			
			//TODO chart drill color
			if(pValue.contains(":COLOR:")) {
				String[] colors = pValue.split(":COLOR:");
				pValue = colors[0];
				String color = colors[1];
				ComponentRuntime comp = instance.getDashBoard().getComponent(pName);
				List<ComponentRuntime> targets = comp.getTargets(false);
				for(ComponentRuntime target : targets) {
					if(target.getElement() instanceof ComponentChartDefinition) {
						String targetName = ((ComponentChartDefinition)target.getElement()).getName();
						instance.getState().setDrillColor(targetName, color);
					}
				}
			}
			
			l = instance.setParameter(pName, pValue);
			StringBuffer buf = new StringBuffer();
			boolean first = true;
			for(String s : l){
				if(first){first = false;}else{buf.append(";");}
				buf.append(s);
			}
			resp.getWriter().write(buf.toString());
		} catch(InstantiationException e){
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
