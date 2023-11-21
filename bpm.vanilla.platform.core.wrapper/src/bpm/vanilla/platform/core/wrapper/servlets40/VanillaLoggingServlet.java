package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaLoggingManager;
import bpm.vanilla.platform.core.beans.BiwLogs;
import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.LogBean;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogsProps;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaLoggingServlet extends AbstractComponentServlet {

	public VanillaLoggingServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger) {
		this.logger = logger;
		this.component = componentProvider;
	}

	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaLoggingServlet...");
		super.init();

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IVanillaLoggingManager.ActionType)) {
				throw new Exception("ActionType not a IRepositoryManager");
			}

			IVanillaLoggingManager.ActionType type = (IVanillaLoggingManager.ActionType) action.getActionType();

			try {
				switch (type) {
				case ADD_BIW_LOG:
					actionResult = addBiwLog(args);
					break;
				case ADD_LOG_BEAN:
					addLogBean(args);
					break;
				case ADD_VANILLA_LOG:
					actionResult = addVanillaLog(args);
					break;
				case ADD_VANILLA_LOG_PROP:
					actionResult = addVanillaLogProp(args);
					break;
				case DEL_LOG_BEAN:
					delLogBean(args);
					break;
				case FIND_BIW_LOG:
					actionResult = findBiwLog(args);
					break;
				case FIND_VANILLA_LOG:
					actionResult = findVanillaLog(args);
					break;
				case FIND_VANILLA_LOG_PROP:
					actionResult = findVanillaLogProp(args);
					break;
				case LIST_BEAN_LOG_4SERVER_URL:
					actionResult = listLogBeanForRuntimeUrl(args);
					break;
				case LIST_BEAN_LOG_FOR_INSTANCE:
					actionResult = listLogBeanForInstanceId(args);
					break;
				case LIST_BIW_LOGS:
					actionResult = listBiwLogs(args);
					break;
				case LIST_VANILLA_LOGS:
					actionResult = listVanillaLogs(args);
					break;
				case LIST_VANILLA_LOGS_PROPERTIES:
					actionResult = listVanillaLogsProps(args);
					break;
				case LIST_VANILLA_LOGS_PROPS_4LOG_ID:
					actionResult = listVanillaLogsProp4LogId(args);
					break;
				case UPDATE_BIW_LOG:
					updateBiwLog(args);
					break;
				case UPDATE_VANILLA_LOG:
					updateVanillaLog(args);
					break;
				case UPDATE_VANILLA_LOG_PROP:
					updateVanillaLogProp(args);
					break;
				case ADD_UOLAP_QUERY_BEAN:
					actionResult = addUolapQueryLog(args);
					break;
				case DEL_UOLAP_QUERY_BEAN:
					actionResult = delUolapQueryLog(args);
					break;
				case LIST_UOLAP_QUERY:
					actionResult = listUolapQueryLog(args);
					break;
				case LIST_FMDT_QUERY:
					actionResult = listFmdtQueryLog(args);
					break;
				case DEL_FMDT_QUERY_BEAN:
					actionResult = delFmdtQueryLog(args);
					break;
				case ADD_FMDT_QUERY_BEAN:
					actionResult = addFmdtQueryLog(args);
					break;
				case GET_SECURITY_LOGS:
					actionResult = getSecurityLogs(args);
					break;
				case SAVE_SECURITY_LOG:
					saveSecurityLog(args);
					break;
				case GET_VANILLA_LOGS_BY_ID:
					actionResult = getVanillaLogsById(args);
					break;
				}
			} catch (Exception ex) {
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
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

	private Object listUolapQueryLog(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().size() == 0) {
			argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getUolapQueries"), args);
			return component.getLoggingManager().getUolapQueries();

		}
		else {
			argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getUolapQueries", UOlapQueryBean.class), args);
			return component.getLoggingManager().getUolapQueries((IObjectIdentifier) args.getArguments().get(0));
		}

	}

	private Object delUolapQueryLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("deleteUolapQuery", UOlapQueryBean.class), args);
		return component.getLoggingManager().addUolapQuery((UOlapQueryBean) args.getArguments().get(0));

	}

	private Object addUolapQueryLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addUolapQuery", UOlapQueryBean.class), args);
		return component.getLoggingManager().addUolapQuery((UOlapQueryBean) args.getArguments().get(0));

	}

	private void updateVanillaLogProp(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("updateVanillaLogsProperty", VanillaLogsProps.class), args);
		component.getLoggingManager().updateVanillaLogsProperty((VanillaLogsProps) args.getArguments().get(0));

	}

	private void updateVanillaLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("updateVanillaLog", VanillaLogs.class), args);
		component.getLoggingManager().updateVanillaLog((VanillaLogs) args.getArguments().get(0));

	}

	private void updateBiwLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("updateBiwLog", BiwLogs.class), args);
		component.getLoggingManager().updateBiwLog((BiwLogs) args.getArguments().get(0));

	}

	private Object listVanillaLogsProp4LogId(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getVanillaLogsPropertiesForLogId", int.class), args);
		return component.getLoggingManager().getVanillaLogsPropertiesForLogId((Integer) args.getArguments().get(0));

	}

	private Object listVanillaLogsProps(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getVanillaLogsProperties();

	}

	private Object listVanillaLogs(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getListVanillaLogs();

	}

	private Object listBiwLogs(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getBiwLogs();

	}

	private Object listLogBeanForInstanceId(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getLogBeanForRunning", int.class), args);
		return component.getLoggingManager().getLogBeanForRunning((Integer) args.getArguments().get(0));

	}

	private Object listLogBeanForRuntimeUrl(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getUolapQueries", IObjectIdentifier.class), args);
		return component.getLoggingManager().getUolapQueries((IObjectIdentifier) args.getArguments().get(0));

	}

	private Object findVanillaLogProp(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getVanillaLogsById", int.class), args);
		return component.getLoggingManager().getVanillaLogsById((Integer) args.getArguments().get(0));

	}

	private Object findVanillaLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getVanillaLogsById", int.class), args);
		return component.getLoggingManager().getVanillaLogsById((Integer) args.getArguments().get(0));

	}

	private Object findBiwLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("getBiwLogsById", int.class), args);
		return component.getLoggingManager().getBiwLogsById((Integer) args.getArguments().get(0));

	}

	private void delLogBean(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("deleteLogBean", int.class), args);
		component.getLoggingManager().deleteLogBean((Integer) args.getArguments().get(0));

	}

	private Object addVanillaLogProp(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addVanillaLogsProperty", VanillaLogsProps.class), args);
		return component.getLoggingManager().addVanillaLogsProperty((VanillaLogsProps) args.getArguments().get(0));

	}

	private Object addVanillaLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addVanillaLog", VanillaLogs.class), args);
		return component.getLoggingManager().addVanillaLog((VanillaLogs) args.getArguments().get(0));

	}

	private Object addBiwLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addBiwLog", BiwLogs.class), args);
		return component.getLoggingManager().addBiwLog((BiwLogs) args.getArguments().get(0));
	}

	private void addLogBean(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addLogBean", LogBean.class), args);
		component.getLoggingManager().addLogBean((LogBean) args.getArguments().get(0));
	}

	private Object listFmdtQueryLog(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getFmdtQueries((Integer) args.getArguments().get(0));
	}

	private Object delFmdtQueryLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("deleteFmdtQuery", FMDTQueryBean.class), args);
		return component.getLoggingManager().deleteFmdtQuery((FMDTQueryBean) args.getArguments().get(0));
	}

	private Object addFmdtQueryLog(XmlArgumentsHolder args) throws Exception {
		argChecker.checkArguments(IVanillaLoggingManager.class.getMethod("addFmdtQuery", FMDTQueryBean.class), args);
		return component.getLoggingManager().addFmdtQuery((FMDTQueryBean) args.getArguments().get(0));
	}

	private Object getSecurityLogs(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getSecurityLogs((Integer) args.getArguments().get(0), (TypeSecurityLog) args.getArguments().get(1), (Date) args.getArguments().get(2), (Date) args.getArguments().get(3));
	}

	private void saveSecurityLog(XmlArgumentsHolder args) throws Exception {
		component.getLoggingManager().saveSecurityLog((SecurityLog) args.getArguments().get(0));
	}

	private Object getVanillaLogsById(XmlArgumentsHolder args) throws Exception {
		return component.getLoggingManager().getListVanillaLogs((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
	}
}
