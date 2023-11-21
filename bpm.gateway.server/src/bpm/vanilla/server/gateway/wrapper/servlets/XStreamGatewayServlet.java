package bpm.vanilla.server.gateway.wrapper.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.server.gateway.server.GatewayManagerImpl;

import com.thoughtworks.xstream.XStream;

/**
 * Servlet used for administration Tasks
 * 
 * @author ludo
 * 
 */
public class XStreamGatewayServlet extends HttpServlet {

	private static final long serialVersionUID = -2519809817377736287L;

	private GatewayManagerImpl gatewayManager;
	private XStream xstream;

	public XStreamGatewayServlet(GatewayManagerImpl gatewayManager) {
		this.gatewayManager = gatewayManager;
		this.xstream = new XStream();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
			if (sessionId == null) {
				sessionId = (String) req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
			}

			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			IVanillaAPI api = new RemoteVanillaPlatform(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

			VanillaSession session = api.getVanillaSystemManager().getSession(sessionId);

			if (session == null) {
				throw new Exception("no vanilla sessio found");
			}
			
			gatewayManager.init(sessionId);

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
							actionResult = gatewayManager.getMemory();
							break;
						case GET_SERVER_CONFIG:
							actionResult = gatewayManager.getServerConfig();
							break;
						case HISTORIZE:
							gatewayManager.historize();
							break;
						case IS_STARTED:
							actionResult = gatewayManager.isStarted();
							break;
						case REMOVE_TASK:
							gatewayManager.removeTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case RESET_SERVER_CONFIG:
							gatewayManager.resetServerConfig((ServerConfigInfo) args.getArguments().get(0));
							break;
						case START_SERVER:
							gatewayManager.startServer();
							break;
						case STOP_SERVER:
							gatewayManager.stopServer();
							break;
						case STOP_TASK:
							gatewayManager.stopTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_RUNNING_TASKS:
							actionResult = gatewayManager.getRunningTasks();
							break;
						case GET_TASK_INFO:
							actionResult = gatewayManager.getTasksInfo((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_TASKS_INFO:
							actionResult = gatewayManager.getTasksInfo();
							break;
						case GET_WAITING_TASKS:
							actionResult = gatewayManager.getWaitingTasks();
							break;
						case GET_PREVIOUS_TASKS:
							actionResult = gatewayManager.getPreviousInfos((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1), (Integer)args.getArguments().get(2));
							break;
					}
				} catch (Exception ex) {
					throw new VanillaException(ex.getMessage());
				}
			}
			else if (action.getActionType() instanceof GatewayComponent.ActionType) {
				GatewayComponent.ActionType type = (GatewayComponent.ActionType) action.getActionType();
	
				try {
					switch (type) {
						case GENERATE_FMDT_TRANSFO:
							actionResult = gatewayManager.generateFmdtExtractionTransformation((GatewayModelGeneration4Fmdt) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						case GET_STATE:
							actionResult = gatewayManager.getRunState((IRunIdentifier) args.getArguments().get(0));
							break;
						case LIST_ALTERNATE_DATASOURCES:
							actionResult = gatewayManager.getAlternateDataSourcesConnections((IReportRuntimeConfig) args.getArguments().get(0));
							break;
						case RUN_ASYNCH:
							actionResult = gatewayManager.runGatewayAsynch((IGatewayRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						case RUN_GATEWAY:
							actionResult = gatewayManager.runGateway((IGatewayRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						default:
							throw new Exception("Unsupported action with type : " + type);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new VanillaException(ex.getMessage());
				}
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();

		} catch (Throwable ex) {
			ex.printStackTrace();
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}

	}

}
