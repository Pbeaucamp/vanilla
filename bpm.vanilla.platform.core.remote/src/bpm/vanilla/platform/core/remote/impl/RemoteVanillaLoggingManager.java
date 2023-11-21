package bpm.vanilla.platform.core.remote.impl;

import java.util.Date;
import java.util.List;

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
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaLoggingManager implements IVanillaLoggingManager{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();

	}
	public RemoteVanillaLoggingManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public int addBiwLog(BiwLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVanillaLoggingManager.ActionType.ADD_BIW_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public int addVanillaLog(VanillaLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVanillaLoggingManager.ActionType.ADD_VANILLA_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public int addVanillaLogsProperty(VanillaLogsProps prop) throws Exception {
		XmlAction op = new XmlAction(createArguments(prop), IVanillaLoggingManager.ActionType.ADD_VANILLA_LOG_PROP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public void deleteLogBean(int logId) throws Exception {
		XmlAction op = new XmlAction(createArguments(logId), IVanillaLoggingManager.ActionType.DEL_LOG_BEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public List<BiwLogs> getBiwLogs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaLoggingManager.ActionType.LIST_BIW_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public BiwLogs getBiwLogsById(int logId) throws Exception {
		XmlAction op = new XmlAction(createArguments(logId), IVanillaLoggingManager.ActionType.FIND_BIW_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (BiwLogs)xstream.fromXML(xml);
	}

	@Override
	public List<VanillaLogs> getListVanillaLogs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaLoggingManager.ActionType.LIST_VANILLA_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<LogBean> getLogBeanForRunning(int runningInstanceId)throws Exception {
		XmlAction op = new XmlAction(createArguments(runningInstanceId), IVanillaLoggingManager.ActionType.LIST_BEAN_LOG_FOR_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<LogBean> getLogBeans(String runtimeServerUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeServerUrl), IVanillaLoggingManager.ActionType.LIST_BEAN_LOG_4SERVER_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public VanillaLogs getVanillaLogsById(int logId) throws Exception {
		XmlAction op = new XmlAction(createArguments(logId), IVanillaLoggingManager.ActionType.FIND_VANILLA_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaLogs)xstream.fromXML(xml);
	}

	@Override
	public List<VanillaLogsProps> getVanillaLogsProperties() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaLoggingManager.ActionType.LIST_VANILLA_LOGS_PROPERTIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<VanillaLogsProps> getVanillaLogsPropertiesForLogId(int logId)throws Exception {
		XmlAction op = new XmlAction(createArguments(logId), IVanillaLoggingManager.ActionType.LIST_VANILLA_LOGS_PROPS_4LOG_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public VanillaLogsProps getVanillaLogsPropertyById(int propertyId)throws Exception {
		XmlAction op = new XmlAction(createArguments(propertyId), IVanillaLoggingManager.ActionType.FIND_VANILLA_LOG_PROP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaLogsProps)xstream.fromXML(xml);
	}

	@Override
	public void updateBiwLog(BiwLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVanillaLoggingManager.ActionType.UPDATE_BIW_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void updateVanillaLog(VanillaLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVanillaLoggingManager.ActionType.UPDATE_VANILLA_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void updateVanillaLogsProperty(VanillaLogsProps prop)throws Exception {
		XmlAction op = new XmlAction(createArguments(prop), IVanillaLoggingManager.ActionType.UPDATE_VANILLA_LOG_PROP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void addLogBean(LogBean logBean) throws Exception {
		XmlAction op = new XmlAction(createArguments(logBean), IVanillaLoggingManager.ActionType.ADD_LOG_BEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public boolean addUolapQuery(UOlapQueryBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IVanillaLoggingManager.ActionType.ADD_UOLAP_QUERY_BEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}

	@Override
	public boolean deleteUolapQuery(UOlapQueryBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IVanillaLoggingManager.ActionType.LIST_BEAN_LOG_4SERVER_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}

	@Override
	public List<UOlapQueryBean> getUolapQueries() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVanillaLoggingManager.ActionType.LIST_UOLAP_QUERY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public List<UOlapQueryBean> getUolapQueries(IObjectIdentifier identifier)throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier), IVanillaLoggingManager.ActionType.LIST_BEAN_LOG_4SERVER_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public boolean addFmdtQuery(FMDTQueryBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IVanillaLoggingManager.ActionType.ADD_FMDT_QUERY_BEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}

	@Override
	public boolean deleteFmdtQuery(FMDTQueryBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IVanillaLoggingManager.ActionType.DEL_FMDT_QUERY_BEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean)xstream.fromXML(xml);
	}

	@Override
	public List<FMDTQueryBean> getFmdtQueries(int numberFiles) throws Exception {
		XmlAction op = new XmlAction(createArguments(numberFiles), IVanillaLoggingManager.ActionType.LIST_FMDT_QUERY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, type, startDate, endDate), IVanillaLoggingManager.ActionType.GET_SECURITY_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<SecurityLog>)xstream.fromXML(xml);
	}


	@Override
	public void saveSecurityLog(SecurityLog log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVanillaLoggingManager.ActionType.SAVE_SECURITY_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}


	@Override
	public List<VanillaLogs> getListVanillaLogs(int itemId, String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type), IVanillaLoggingManager.ActionType.GET_VANILLA_LOGS_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<VanillaLogs>) xstream.fromXML(xml);
	}

}
