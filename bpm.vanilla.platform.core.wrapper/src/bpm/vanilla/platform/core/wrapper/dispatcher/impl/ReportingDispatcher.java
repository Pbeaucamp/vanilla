package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.wrapper.dispatcher.DistributedComponentComponentEvaluator;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ReportingDispatcher extends RunnableDispatcher implements IDispatcher {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private static XStream xstream;
	
	static {
		xstream = new XStream();
	}

	private FactoryDispatcher factory;

	public ReportingDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory) {
		super(component);
		this.factory = factory;
	}

	private User extractUser(HttpServletRequest request) throws Exception {
		String sessionId = request.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null) {
			sessionId = (String) request.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}

		VanillaSession session = getComponent().getSystemManager().getSession(sessionId);
		return session.getUser();
	}
	
	private String extractComponentUrl(HttpServletRequest request) {
		String componentUrl = request.getHeader(VanillaConstants.HTTP_HEADER_COMPONENT_URL);
		return componentUrl != null && !componentUrl.isEmpty() ? componentUrl : null;
	}

	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = extractUser(request);
		String componentUrl = extractComponentUrl(request); 
		
		try {
			response.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(request.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}
			
			long start = new Date().getTime();
			IRuntimeConfig config = getRuntimeConfig(args);

			if (action.getActionType() instanceof IVanillaServerManager.ActionType) {
				IVanillaServerManager.ActionType type = (IVanillaServerManager.ActionType) action.getActionType();
							
				IVanillaComponentIdentifier component = loadBalance(null, componentUrl);
				
				try {
					switch (type) {
						case GET_MEMORY:
							actionResult = getServerRemote(component, user).getMemory();
							break;
						case GET_SERVER_CONFIG:
							actionResult = getServerRemote(component, user).getServerConfig();
							break;
						case HISTORIZE:
							getServerRemote(component, user).historize();
							break;
						case IS_STARTED:
							actionResult = getServerRemote(component, user).isStarted();
							break;
						case REMOVE_TASK:
							IRunIdentifier idArg = (IRunIdentifier)args.getArguments().get(0);
							getServerRemote(component, user).removeTask(getIdentifier(idArg));
							break;
						case RESET_SERVER_CONFIG:
							getServerRemote(component, user).resetServerConfig((ServerConfigInfo) args.getArguments().get(0));
							break;
						case START_SERVER:
							getServerRemote(component, user).startServer();
							break;
						case STOP_SERVER:
							getServerRemote(component, user).stopServer();
							break;
						case STOP_TASK:
							IRunIdentifier idStopArg = (IRunIdentifier)args.getArguments().get(0);
							getServerRemote(component, user).stopTask(getIdentifier(idStopArg));
							break;
						case GET_RUNNING_TASKS:
							actionResult = getServerRemote(component, user).getRunningTasks();
							break;
						case GET_TASK_INFO:
							IRunIdentifier idTaskArg = (IRunIdentifier)args.getArguments().get(0);
							actionResult = getServerRemote(component, user).getTasksInfo(getIdentifier(idTaskArg));
							break;
						case GET_TASKS_INFO:
							actionResult = getServerRemote(component, user).getTasksInfo();
							break;
						case GET_WAITING_TASKS:
							actionResult = getServerRemote(component, user).getWaitingTasks();
							break;
						case GET_PREVIOUS_TASKS:
							actionResult = getServerRemote(component, user).getPreviousInfos((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1), (Integer)args.getArguments().get(2));
							break;
					}
				} catch (Exception ex) {
					logger.error(ex);
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
			}
			
			else if (action.getActionType() instanceof ReportingComponent.ActionType) {
				
				IReportRuntimeConfig conf = getRuntimeConfig(args) != null ? (IReportRuntimeConfig) getRuntimeConfig(args) : null;
	
				IVanillaComponentIdentifier component = loadBalance(conf, null);
	
				ReportingComponent.ActionType type = (ReportingComponent.ActionType) action.getActionType();
	
				try {
					switch (type) {
						case RUN_ASYNCH:
							actionResult = getServerRemote(component, user).runReportAsynch((IReportRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							factory.addAsynchRun((RunIdentifier) actionResult);
							break;
						case CHECK_RUN_ASYNCH_STATE:
							RunIdentifier runId = checkRunAsynchState(((IRunIdentifier)args.getArguments().get(0)).getKey());
							actionResult = getServerRemote(component, user).checkRunAsynchState(runId);
							break;
						case LOAD_RESULT:
							IRunIdentifier idArg = (IRunIdentifier)args.getArguments().get(0);
							RunIdentifier identifier = getIdentifier(idArg);
							serializeReport(getServerRemote(component, user).loadGeneratedReport(identifier), response.getOutputStream());
							factory.removeAsynchRun(identifier);
							break;
						case BUILD_RPT_DESIGN:
							serializeReport(getServerRemote(component, user).buildRptDesignFromFWR((IReportRuntimeConfig)args.getArguments().get(0), 
									new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(1)))), response.getOutputStream());
							break;
						case GENERATE_DISCO_PACKAGE:
							serializeReport(getServerRemote(component, user).generateDiscoPackage((IObjectIdentifier)args.getArguments().get(0), 
									(Integer)args.getArguments().get(1), (User)args.getArguments().get(2)), response.getOutputStream());
							break;
						case LIST_ALTERNATE_DATASOURCES:
							actionResult = getServerRemote(component, user).getAlternateDataSourcesConnections((IReportRuntimeConfig)args.getArguments().get(0), (User)args.getArguments().get(1));
							break;
						case PARAMETER_VALUES:
							actionResult = getServerRemote(component, user).getReportParameterValues((User)args.getArguments().get(0), (IReportRuntimeConfig)args.getArguments().get(1), 
									(String)args.getArguments().get(2), (List<VanillaParameter>)args.getArguments().get(3));
							break;
						case PARAMETERS_DEFINITION:
							actionResult = getServerRemote(component, user).getReportParameters((User)args.getArguments().get(0), (IReportRuntimeConfig)args.getArguments().get(1));
							break;
						case RUN:
							serializeReport(getServerRemote(component, user).runReport((IReportRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1)), response.getOutputStream());
							break;
						case RUN_FROM_DEFINITION:
							serializeReport(getServerRemote(component, user).runReport((IReportRuntimeConfig) args.getArguments().get(0), new ByteArrayInputStream((byte[]) args.getArguments().get(1)), (User) args.getArguments().get(2), (Boolean) args.getArguments().get(3)), response.getOutputStream());
							break;
						case STOCK_REPORT_BACKGROUND:
							actionResult = getServerRemote(component, user).stockReportBackground((IRunIdentifier) args.getArguments().get(0));
							break;
					}
				} catch (Exception ex) {
					logger.error(ex);
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, response.getWriter());
			}
			
			long end = new Date().getTime();
			if (config != null) {
				log(VanillaLogs.Level.INFO, user, action.getActionType().toString(), request.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), request, config, "", end - start);
			}
			try {
				response.getWriter().close();
			} catch (Exception e) {
			}

		} catch (Exception ex) {
			if (!ex.getMessage().contains("FIND_IMG_4USER")) {
				logger.error(ex.getMessage(), ex);

				response.getWriter().write("<error>" + ex.getMessage() + "</error>");
				response.getWriter().close();
			}
		}
	}
	
	private RunIdentifier getIdentifier(IRunIdentifier idArg) {
		if(idArg instanceof SimpleRunTaskId) {
			RunIdentifier identifier = factory.getRunIdentifier(((SimpleRunTaskId) idArg).getTaskId());
			return identifier;
		}
		else {
			RunIdentifier identifier = factory.getRunIdentifier(((IRunIdentifier) idArg).getKey());
			return identifier;
		}
	}

	private void serializeReport(InputStream is, OutputStream os) throws Exception{
		IOWriter.write(is, os, true, false);
	}

	private ReportingComponent getServerRemote(IVanillaComponentIdentifier componentId, User user) throws Exception {
		ReportingComponent serverRemote = new RemoteReportRuntime(new BaseVanillaContext(componentId.getComponentUrl(), user.getLogin(), user.getPassword()), true);
		return serverRemote;
	}

	@Override
	public boolean needAuthentication() {
		return true;
	}

	private RunIdentifier checkRunAsynchState(String runIdentifierKey) throws Exception {
		RunIdentifier runId = factory.getRunIdentifier(runIdentifierKey);

		if (runId == null) {
			throw new Exception("This run has already be consumed");
		}
		
		return runId;
	}

	/**
	 * run the Heuristic function on all registered ReportComponent and return
	 * the less loaded. If no ReportComponent registered, an exception is thrown
	 * 
	 * @return
	 * @throws Exception
	 */
	private IVanillaComponentIdentifier loadBalance(IReportRuntimeConfig runtimeConfig, String componentUrl) throws Exception {

		IVanillaComponentIdentifier bestComponent = null;

		String runtimeUrl = componentUrl;
		if (runtimeConfig != null && runtimeConfig instanceof ReportRuntimeConfig) {
			ReportRuntimeConfig conf = (ReportRuntimeConfig) runtimeConfig;
			runtimeUrl = conf.getRuntimeUrl();
			if (runtimeUrl == null) {

				String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
				String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

				// If not let the vanilla evaluator choose the best cluster
				if (conf.getObjectIdentifier() != null && conf.getObjectIdentifier().getDirectoryItemId() > 0) {

					String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
					IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, login, password);

					IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
					Repository repository = api.getVanillaRepositoryManager().getRepositoryById(conf.getObjectIdentifier().getRepositoryId());

					Group group = new Group();
					group.setId(-1);
					IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);

					IRepositoryApi rep = new RemoteRepositoryApi(ctx);
					RepositoryItem item = rep.getRepositoryService().getDirectoryItem(conf.getObjectIdentifier().getDirectoryItemId());
					runtimeUrl = item.getRuntimeUrl();
				}

			}
		}

		bestComponent = DistributedComponentComponentEvaluator.computeLoad(this, runtimeUrl, factory);
		if (bestComponent == null) {
			throw new VanillaException("Something wrong happened when load balancing on ReportComponent, no one could be chosen.");
		}

		return bestComponent;
	}
}
