package bpm.workflow.wrapper.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.workflow.runtime.WorkflowRuntimeImpl;

public class WorkflowSubmissionServlet extends HttpServlet {
	
	private WorkflowRuntimeImpl workflowRuntime;
	
	
	
	public WorkflowSubmissionServlet(WorkflowRuntimeImpl workflowRuntime){
		this.workflowRuntime = workflowRuntime;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String processInstanceUUID = req.getParameter(WorkflowService.HIDDEN_BONITA_PROCESS_INSTANCE_UUID);
		String activityInstanceUUID = req.getParameter(WorkflowService.HIDDEN_BONITA_ACTIVITY_INSTANCE_UUID);

		
		
		HashMap<String, String> values = new HashMap<String, String>();
		
		
		Enumeration<String> pNames = req.getParameterNames();
		
		while(pNames.hasMoreElements()){
			String pName =  pNames.nextElement();
			values.put(pName, req.getParameter(pName));
		}
		
		try {
//			workflowRuntime.submitManualTask(processInstanceUUID, activityInstanceUUID, values);
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
		}
	}
}
