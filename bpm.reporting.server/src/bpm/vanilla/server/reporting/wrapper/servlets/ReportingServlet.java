package bpm.vanilla.server.reporting.wrapper.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.server.reporting.server.ReportingManagerImpl;

import com.thoughtworks.xstream.XStream;

public class ReportingServlet extends HttpServlet {

	private static final long serialVersionUID = 8869591855221581303L;
	
	private Logger logger = Logger.getLogger(this.getClass());

	private ReportingManagerImpl reportingManager;

	private XStream xstream;

	public ReportingServlet(ReportingManagerImpl reportingManager) {
		this.reportingManager = reportingManager;

		xstream = new XStream();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			
			logger.debug("Reporting servlet");
			
			resp.setCharacterEncoding("UTF-8");
		
			String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
			reportingManager.init(sessionId);
			
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
							actionResult = reportingManager.getMemory();
							break;
						case GET_SERVER_CONFIG:
							actionResult = reportingManager.getServerConfig();
							break;
						case HISTORIZE:
							reportingManager.historize();
							break;
						case IS_STARTED:
							actionResult = reportingManager.isStarted();
							break;
						case REMOVE_TASK:
							reportingManager.removeTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case RESET_SERVER_CONFIG:
							reportingManager.resetServerConfig((ServerConfigInfo) args.getArguments().get(0));
							break;
						case START_SERVER:
							reportingManager.startServer();
							break;
						case STOP_SERVER:
							reportingManager.stopServer();
							break;
						case STOP_TASK:
							reportingManager.stopTask((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_RUNNING_TASKS:
							actionResult = reportingManager.getRunningTasks();
							break;
						case GET_TASK_INFO:
							actionResult = reportingManager.getTasksInfo((IRunIdentifier) args.getArguments().get(0));
							break;
						case GET_TASKS_INFO:
							actionResult = reportingManager.getTasksInfo();
							break;
						case GET_WAITING_TASKS:
							actionResult = reportingManager.getWaitingTasks();
							break;
						case GET_PREVIOUS_TASKS:
							actionResult = reportingManager.getPreviousInfos((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1), (Integer)args.getArguments().get(2));
							break;
					}
				} catch (Exception ex) {
					logger.error(ex);
					ex.printStackTrace();
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
			}
			else if (action.getActionType() instanceof ReportingComponent.ActionType) {
				ReportingComponent.ActionType type = (ReportingComponent.ActionType) action.getActionType();
				
				try {
					switch (type) {
						case BUILD_RPT_DESIGN:
							serializeReport(reportingManager.buildRptDesignFromFWR((IReportRuntimeConfig) args.getArguments().get(0), new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(1)))), resp.getOutputStream());
							return;
						case CHECK_RUN_ASYNCH_STATE:
							actionResult = reportingManager.checkRunAsynchState((IRunIdentifier) args.getArguments().get(0));
							break;
						case GENERATE_DISCO_PACKAGE:
							serializeReport(reportingManager.generateDiscoPackage((IObjectIdentifier) args.getArguments().get(0), (Integer) args.getArguments().get(1), (User) args.getArguments().get(2)), resp.getOutputStream());
							return;
						case LIST_ALTERNATE_DATASOURCES:
							actionResult = new AlternateDataSourceHolder(new HashMap<String, List<String>>());
							
//							actionResult = reportingManager.getAlternateDataSourcesConnections((IReportRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						case LOAD_RESULT:
							serializeReport(reportingManager.loadGeneratedReport((IRunIdentifier) args.getArguments().get(0)), resp.getOutputStream());
							return;
						case PARAMETER_VALUES:
							actionResult = reportingManager.getReportParameterValues((User) args.getArguments().get(0), (IReportRuntimeConfig) args.getArguments().get(1), (String) args.getArguments().get(2), (List<VanillaParameter>) args.getArguments().get(3));
							break;
						case PARAMETERS_DEFINITION:
							actionResult = reportingManager.getReportParameters((User) args.getArguments().get(0), (IReportRuntimeConfig) args.getArguments().get(1));
							break;
						case RUN:
							serializeReport(reportingManager.runReport((IReportRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1)), resp.getOutputStream());
							return;
						case RUN_ASYNCH:
							actionResult = reportingManager.runReportAsynch((IReportRuntimeConfig) args.getArguments().get(0), (User) args.getArguments().get(1));
							break;
						case RUN_FROM_DEFINITION:
							serializeReport(reportingManager.runReport((IReportRuntimeConfig) args.getArguments().get(0), new ByteArrayInputStream((byte[]) args.getArguments().get(1)), (User) args.getArguments().get(2), (Boolean) args.getArguments().get(3)), resp.getOutputStream());
							return;
						case STOCK_REPORT_BACKGROUND:
							actionResult = reportingManager.stockReportBackground((IRunIdentifier) args.getArguments().get(0));
							break;
					}
				} catch (Exception ex) {
					logger.error(ex);
					ex.printStackTrace();
					throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
				}
			}
			else {
				throw new Exception("ActionType not a Reporting Action");
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception ex) {
			if (!ex.getMessage().contains("FIND_IMG_4USER")) {
				logger.error(ex.getMessage(), ex);

				resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
				resp.getWriter().close();
			}
		}
	}

	private void serializeReport(InputStream is, OutputStream os) throws Exception{
		IOWriter.write(is, os, true, false);
	}
}
