package bpm.smart.runtime;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.ISmartWorkflowManager;
import bpm.smart.runtime.security.AirSession;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.remote.internal.RemoteConstants;

import com.thoughtworks.xstream.XStream;

public class WorkflowManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private IVanillaLogger logger;
	private SmartManagerComponent rootComponent;
	
	private XStream xstream;
	
	
	public WorkflowManagerServlet(SmartManagerComponent componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.rootComponent = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing WorkflowManagerServlet...");
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType", IXmlActionType.class, ISmartManager.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			String locale = req.getHeader(VanillaConstants.HTTP_HEADER_LOCALE);
			Locale loc = locale != null ? new Locale(locale) : null;
			
			String sessionId = req.getHeader(RemoteConstants.HTTP_HEADER_SESSION_ID);
			AirSession session = rootComponent.getSessionHolder().getSession(sessionId);
			
			if(session == null){
				throw new Exception("The session is not initialized. Please try to connect to a vanilla instance.");
			}
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());

			SmartManagerService component = session.getServiceManager();
			component.setLocale(loc);
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof ISmartWorkflowManager.ActionType)){
				throw new Exception("ActionType not a IWorkflowManager");
			}
			
			Date startDate = new Date();
			
			ISmartWorkflowManager.ActionType type = (ISmartWorkflowManager.ActionType)action.getActionType();
			
			try{
				switch (type) {
					case DELETE_WORKFLOW:
						component.removeWorkflow((Workflow) args.getArguments().get(0));
						break;
					case GET_WORKFLOW_PROGRESS:
						actionResult = component.getWorkflowProgress((Workflow) args.getArguments().get(0), (String) args.getArguments().get(1));
						break;
					case GET_WORKFLOWS:
						actionResult = component.getWorkflows();
						break;
					case GET_WORKFLOW:
						actionResult = component.getWorkflow((Integer) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
						break;
					case GET_WORKLFOW_PARAMETERS:
						actionResult = component.getWorkflowParameters((Workflow) args.getArguments().get(0));
						break;
					case RUN_WORKFLOW:
						actionResult = component.runWorkflow((Workflow) args.getArguments().get(0), (String) args.getArguments().get(1), (User) args.getArguments().get(2), (List<Parameter>) args.getArguments().get(3));
						break;
					case MANAGE_WORKFLOW:
						actionResult = component.manageWorkflow((Workflow) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
						break;
//					case GET_WORKFLOW_BY_ID:
//						actionResult = component.getWorkflowById((Integer) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
//						break;
					case GET_WORKFLOW_RUNNING:
						actionResult = component.getWorkflowRunningInstances((Integer) args.getArguments().get(0));
						break;
					case MANAGE_RESOURCE:
						actionResult = component.manageResource((Resource) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
						break;
					case REMOVE_RESOURCE:
						component.removeResource((Resource) args.getArguments().get(0));
						break;
					case GET_RESOURCES:
						actionResult = component.getResources((TypeResource) args.getArguments().get(0));
						break;
					case VALID_SCRIPT:
						actionResult = component.validScript((Variable) args.getArguments().get(0));
						break;
					case RUN_INCOMPLETE_WORKFLOW:
						actionResult = component.runIncompleteWorkflow((Workflow) args.getArguments().get(0), (List<Parameter>) args.getArguments().get(1), (String) args.getArguments().get(2));
						break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			Date endDate = new Date();
			
			logger.trace("Execution time for : " + type.toString() + " -> " + (endDate.getTime() - startDate.getTime()) + " with arguments -> " + args.toString());
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}	
}
