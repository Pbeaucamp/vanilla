package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.FileNotFoundException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.platform.core.wrapper.dispatcher.DistributedComponentComponentEvaluator;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class GatewayDispatcher extends RunnableDispatcher implements IDispatcher {

	private static XStream xstream;
	static {
		xstream = new XStream();
	}
	
	private FactoryDispatcher factory;

	public GatewayDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory) {
		super(component);
		this.factory = factory;
	}

	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = extractUser(request);
		String componentUrl = extractComponentUrl(request) != null ? extractComponentUrl(request) : "default"; 

		try {
			response.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(request.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}
			long start = new Date().getTime();
			
			IRuntimeConfig conf = getRuntimeConfig(args);
			
			if (action.getActionType() instanceof IVanillaServerManager.ActionType) {
				IVanillaServerManager.ActionType type = (IVanillaServerManager.ActionType) action.getActionType();
				
				IVanillaComponentIdentifier component = DistributedComponentComponentEvaluator.computeLoad(this, componentUrl, factory);
				
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
					Logger.getLogger(getClass()).error(ex);
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
			}

			else if ((action.getActionType() instanceof GatewayComponent.ActionType)) {
	
	
				GatewayComponent.ActionType type = (GatewayComponent.ActionType) action.getActionType();
	
				IVanillaComponentIdentifier bestComponent = null;
	
				// TODO : MLA Maybe find another way for this
				// But I don't see how right now.
				IObjectIdentifier objId = null;
				String runtimeUrl = null;
				if (args.getArguments().get(0) instanceof IGatewayRuntimeConfig) {
					objId = ((IGatewayRuntimeConfig) args.getArguments().get(0)).getObjectIdentifier();
					runtimeUrl = ((GatewayRuntimeConfiguration) args.getArguments().get(0)).getRuntimeUrl();
				}
				else if (args.getArguments().get(0) instanceof GatewayModelGeneration4Fmdt) {
					int itemId = ((GatewayModelGeneration4Fmdt) args.getArguments().get(0)).getFmdtDirectoryItemId();
					int repId = ((GatewayModelGeneration4Fmdt) args.getArguments().get(0)).getFmdtRepositoryId();
					objId = new ObjectIdentifier(repId, itemId);
					runtimeUrl = ((GatewayModelGeneration4Fmdt) args.getArguments().get(0)).getRuntimeUrl();
				}
	
				if (runtimeUrl != null) {
					bestComponent = DistributedComponentComponentEvaluator.computeLoad(this, runtimeUrl, factory);
					if (bestComponent == null) {
						throw new VanillaException("No GatewayComponent " + " registered within Vanilla");
					}
				}
				else {
					bestComponent = DistributedComponentComponentEvaluator.computeLoad(this, objId, factory);
					if (bestComponent == null) {
						throw new VanillaException("No GatewayComponent " + " registered within Vanilla");
					}
				}
	
				
				
				
				try {
					switch (type) {
						case GET_STATE:
							IRunIdentifier idArg = (IRunIdentifier)args.getArguments().get(0);
							actionResult = getServerRemote(bestComponent, user).getRunState(getIdentifier(idArg));
							break;
						case RUN_ASYNCH:
							actionResult = getServerRemote(bestComponent, user).runGatewayAsynch((IGatewayRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							factory.addAsynchRun((RunIdentifier)actionResult);
							break;
						case GENERATE_FMDT_TRANSFO:
							actionResult = getServerRemote(bestComponent, user).generateFmdtExtractionTransformation((GatewayModelGeneration4Fmdt) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						case LIST_ALTERNATE_DATASOURCES:
							actionResult = getServerRemote(bestComponent, user).getAlternateDataSourcesConnections((IReportRuntimeConfig) args.getArguments().get(0));
							break;
						case RUN_GATEWAY:
							actionResult = getServerRemote(bestComponent, user).runGateway((IGatewayRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
					}
				} catch (VanillaComponentDownException ex) {
					log(VanillaLogs.Level.ERROR, user, type, request.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), request, conf, ex.getMessage());
					throw ex;
				} catch (FileNotFoundException e) {
					log(VanillaLogs.Level.ERROR, user, type, request.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), request, conf, e.getMessage());
					throw new VanillaComponentDownException(bestComponent, null);
				} catch (Exception ex) {
					log(VanillaLogs.Level.ERROR, user, type, request.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), request, conf, ex.getMessage());
					ex.printStackTrace();
					Logger.getLogger(getClass()).error(ex);
					throw new VanillaException("Operation " + type.name() + " failed - " + ex.getMessage());
	
				}
			}
			if (actionResult != null) {
				xstream.toXML(actionResult, response.getWriter());
			}
			long end = new Date().getTime();
			if (conf != null) {
				log(VanillaLogs.Level.INFO, user, "Big", request.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE), request, conf, "", end - start);
			}
			response.getWriter().close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);

			response.getWriter().write("<error>" + ex.getMessage() + "</error>");
			response.getWriter().close();
		}

	}
	
	private IRunIdentifier getIdentifier(IRunIdentifier idArg) {
		if(idArg instanceof SimpleRunTaskId) {
			RunIdentifier identifier = factory.getRunIdentifier(((SimpleRunTaskId) idArg).getTaskId());
			return identifier;
		}
		else {
			RunIdentifier identifier = factory.getRunIdentifier(((IRunIdentifier) idArg).getKey());
			return identifier;
		}
	}

	private GatewayComponent getServerRemote(IVanillaComponentIdentifier componentId, User user) throws Exception {
		String url = componentId.getComponentUrl();
		
		try {
			//rewrite the url to localhost if necessary
			String rewrite = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL_LOCALHOST_REWRITE);
			if(rewrite != null && Boolean.parseBoolean(rewrite)) {
				String baseUrlComp = getUrlHost(url);
				String baseUrlRuntime = getUrlHost(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
				if(baseUrlComp.equals(baseUrlRuntime)) {
					//extract the port
					String port = extractPort(baseUrlRuntime);
					String protocole = getProtocole(baseUrlRuntime);
					if(port.equals("")) {
						url = protocole + "localhost" + getUrlEnding(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
					}
					else {
						url = protocole + "localhost:" + port + getUrlEnding(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
					}
//					System.out.println("url changed to : " + url);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		GatewayComponent serverRemote = new RemoteGatewayComponent(new BaseVanillaContext(url, user.getLogin(), user.getPassword()), true);
		return serverRemote;
	}

	@Override
	public boolean needAuthentication() {
		return true;
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
}
