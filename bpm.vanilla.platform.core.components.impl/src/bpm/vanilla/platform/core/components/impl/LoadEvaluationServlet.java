package bpm.vanilla.platform.core.components.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.Distributable;

/**
 * This servlet should be registered by each AbstractVanillaCOmponent that
 * implements the interface {@link bpm.vanilla.platform.core.components.Distributable}}
 * 
 * This servlet is not supposed to be called by Clients or Remote, it just used
 * by the VanillaDispatch to be able evaluate the Load of the Component.
 * 
 * 
 * @author ludo
 *
 */
public class LoadEvaluationServlet extends HttpServlet{
	private Distributable distrubtableComponent;
	
	public LoadEvaluationServlet(Distributable distrubtableComponent){
		this.distrubtableComponent = distrubtableComponent;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//just evaluate and return the result
		try {
			int loadEval = distrubtableComponent.computeLoadEvaluation();
			resp.getWriter().write(loadEval + "");
		} catch (Exception e) {
			String errorMsg = "Failed to evaluate the Component Load for " + distrubtableComponent.getIdentifier().getComponentId() + " at " + distrubtableComponent.getIdentifier().getComponentUrl();
			Logger.getLogger(getClass()).error(errorMsg + "-" + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
		}
		
	}
}
