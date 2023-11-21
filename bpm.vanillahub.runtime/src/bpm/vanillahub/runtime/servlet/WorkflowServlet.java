package bpm.vanillahub.runtime.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.core.IHubWorkflowManager.ActionType;
import bpm.vanillahub.runtime.ComponentVanillaHub;
import bpm.vanillahub.runtime.security.HubSession;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.remote.internal.RemoteConstants;

import com.thoughtworks.xstream.XStream;

public class WorkflowServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ComponentVanillaHub component;
	protected XStream xstream;
	
	public WorkflowServlet(ComponentVanillaHub component) throws ServletException {
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String locale = req.getHeader(RemoteConstants.HTTP_HEADER_LOCALE);
			Locale loc = locale != null ? new Locale(locale) : null;
			
			String sessionId = req.getHeader(RemoteConstants.HTTP_HEADER_SESSION_ID);
			HubSession session = component.getSessionHolder().getSession(sessionId);
			
			if(session == null){
				throw new Exception("The session is not initialized. Please try to connect to a vanilla instance.");
			}
			
			IHubWorkflowManager manager = session.getWorkflowManager();
			session.setLocale(loc);
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidReportingManager");
			}
			
			ActionType type = (ActionType)action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case GET_WORKFLOWS:
					actionResult = manager.getWorkflows();
					break;
				case GET_WORKFLOWS_WITH_PAG:
					actionResult = manager.getWorkflows((String) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Boolean) args.getArguments().get(3), (DataSort) args.getArguments().get(4));
					break;
				case GET_WORKFLOW:
					actionResult = manager.getWorkflow((Integer) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case MANAGE_WORKFLOW:
					actionResult = manager.manageWorkflow((Workflow) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case REMOVE_WORKFLOW:
					manager.removeWorkflow((Workflow) args.getArguments().get(0));
					break;
				case INIT_WORKFLOW:
					actionResult = manager.initWorkflow((Workflow) args.getArguments().get(0));
					break;
				case RUN_WORKFLOW:
					manager.runWorkflow((Workflow) args.getArguments().get(0), (String) args.getArguments().get(1), (User) args.getArguments().get(2), (List<Parameter>) args.getArguments().get(3));
					break;
				case GET_PARAMETERS:
					actionResult = manager.getWorkflowParameters((Workflow) args.getArguments().get(0));
					break;
				case GET_WORKFLOW_PROGRESS:
					actionResult = manager.getWorkflowProgress((Workflow) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				case GET_WORKFLOW_RUNS:
					actionResult = manager.getWorkflowRuns((Workflow) args.getArguments().get(0));
					break;
				case GET_WORKFLOW_RUN:
					actionResult = manager.getWorkflowRun((WorkflowInstance) args.getArguments().get(0));
					break;
				case STOP_WORKFLOW:
					manager.stopWorkflow((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				case DUPLICATE_WORKFLOW:
					actionResult = manager.duplicateWorkflow((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				}
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
