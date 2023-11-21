package bpm.vanilla.platform.core.components.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;

/**
 * Servlet providing admin user (superuser) with access to start/stop actions on component
 * 
 * @author manu
 *
 */
public class StartStopServlet extends HttpServlet{

	public static String ACTION_TYPE = "actionType";
	public static String TYPE_STOP = "stop";
	public static String TYPE_START = "start";
	
	public static String COMPONENT_TYPE = "componentType";
	
	private IVanillaComponentListenerService component;
	
	public StartStopServlet(IVanillaComponentListenerService component) {
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
//		Logger.getLogger(getClass()).info("Received Admin Request on component : " + 
//				component.getIdentifier().getComponentId()
//				+ " at " + component.getIdentifier().getComponentUrl());
		String componentNature = "";
		try {
			String actionType = req.getParameter(ACTION_TYPE);
			componentNature = req.getParameter(COMPONENT_TYPE);
			
			if (actionType == null) {
				throw new Exception("No action type in request : ActionType is null. Aborting.");
			}
			else if (componentNature == null) {
				throw new Exception("No component type id in request. Aborting");
			}
			else if (actionType.equals(TYPE_START)) {
				Logger.getLogger(getClass()).info("ActionType = " + TYPE_START);
				//component.start();
				component.startComponent(componentNature);
			}
			else if (actionType.equals(TYPE_STOP)) {
				Logger.getLogger(getClass()).info("ActionType = " + TYPE_STOP);
				//component.stop();
				component.stopComponent(componentNature);
			}
		} catch (Exception e) {
			String errorMsg = "Failed to perform action on the Component " + componentNature;
			Logger.getLogger(getClass()).error(errorMsg + " - " + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
		}
		
	}
}
