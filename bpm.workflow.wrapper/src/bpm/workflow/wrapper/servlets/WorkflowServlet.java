package bpm.workflow.wrapper.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.runtime.WorkflowRuntimeImpl;

import com.thoughtworks.xstream.XStream;

public class WorkflowServlet extends HttpServlet{

	private static final long serialVersionUID = -1239036522486328011L;
	
	private IVanillaLogger logger;
	private XStream xstream;
	private WorkflowRuntimeImpl workflowRuntime;
	
	private IVanillaAPI vanillaApi;
	
	public WorkflowServlet(WorkflowRuntimeImpl workflowRuntime, IVanillaLogger logger) {
		this.logger = logger;
		this.workflowRuntime = workflowRuntime;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing WorkFlowServlet...");
		xstream = new XStream();
		
		VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();
		IVanillaContext vCtx = new BaseVanillaContext(
				cf.getVanillaServerUrl(), 
				cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				cf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		vanillaApi = new RemoteVanillaPlatform(vCtx);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}
	
	public IRuntimeConfig getRuntimeConfig(XmlArgumentsHolder args) {
		if(args.getArguments() != null && args.getArguments().size() > 0) {
			for(Object o : args.getArguments()) {
				if(o instanceof IRuntimeConfig) {
					return (IRuntimeConfig) o;
				}
			}
		}
		return null;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if(action.getActionType() instanceof IVanillaServerManager.ActionType) {
				IVanillaServerManager.ActionType type = (IVanillaServerManager.ActionType) action.getActionType();
	
				try {
					switch (type) {
						case GET_MEMORY:
							actionResult = workflowRuntime.getMemory();
							break;
						case GET_SERVER_CONFIG:
							actionResult = workflowRuntime.getServerConfig();
							break;
						case HISTORIZE:
							workflowRuntime.historize();
							break;
						case IS_STARTED:
							actionResult = workflowRuntime.isStarted();
							break;
						case REMOVE_TASK:
							workflowRuntime.removeTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case RESET_SERVER_CONFIG:
							workflowRuntime.resetServerConfig((ServerConfigInfo) args.getArguments().get(0));
							break;
						case START_SERVER:
							workflowRuntime.startServer();
							break;
						case STOP_SERVER:
							workflowRuntime.stopServer();
							break;
						case STOP_TASK:
							workflowRuntime.stopTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_RUNNING_TASKS:
							actionResult = workflowRuntime.getRunningTasks();
							break;
						case GET_TASK_INFO:
							actionResult = workflowRuntime.getTasksInfo((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_TASKS_INFO:
							actionResult = workflowRuntime.getTasksInfo();
							break;
						case GET_WAITING_TASKS:
							actionResult = workflowRuntime.getWaitingTasks();
							break;
						case GET_PREVIOUS_TASKS:
							actionResult = workflowRuntime.getPreviousInfos((Integer) args.getArguments().get(0), (Integer)args.getArguments().get(1), (Integer) args.getArguments().get(2));
							break;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new VanillaException(ex.getMessage());
				}
			}
			else if (action.getActionType() instanceof WorkflowService.ActionType) {
				WorkflowService.ActionType type = (WorkflowService.ActionType) action.getActionType();
				
				IRuntimeConfig conf = getRuntimeConfig(args);
				long start = new Date().getTime();
				
				try {
					switch (type) {
					case START:
						actionResult = workflowRuntime.startWorkflow((IRuntimeConfig)args.getArguments().get((0)));
						break;
					case INFO:
						actionResult = workflowRuntime.getInfos((IRunIdentifier) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
						break;
					case LIST_FORMS:
	//					actionResult = workflowRuntime.getHTMLForms((Integer)args.getArguments().get((0)));
						break;
					case START_ASYNC:
						actionResult = workflowRuntime.startWorkflowAsync((IRuntimeConfig)args.getArguments().get((0)));
						break;
					case GET_PREVIOUS_TASK:
						actionResult = workflowRuntime.getPreviousInfos((Integer) args.getArguments().get(0), (Integer)args.getArguments().get(1), (Integer) args.getArguments().get(2));
					}
				} catch (Exception ex) {
					logger.error(ex);
					
					VanillaLogs log = new VanillaLogs(
							VanillaLogs.Level.ERROR, 
							req.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), 
							type.toString(), 
							new Date(), 
							0, 
							-1, 
							-1,
							-1, req.getRemoteAddr(), ex.getMessage(), 0);
					vanillaApi.getVanillaLoggingManager().addVanillaLog(log);
					
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
				
				if(conf != null) {
					long end = new Date().getTime();
					VanillaLogs log = new VanillaLogs(
							VanillaLogs.Level.INFO, 
							req.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), 
							type.toString(), 
							new Date(), 
							0, 
							conf.getVanillaGroupId(), 
							conf.getObjectIdentifier().getRepositoryId(),
							conf.getObjectIdentifier().getDirectoryItemId(), req.getRemoteAddr(), "", end - start);
					vanillaApi.getVanillaLoggingManager().addVanillaLog(log);
				}
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			resp.getWriter().close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
