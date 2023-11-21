package bpm.vanilla.platform.core;

import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.BiwLogs;
import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.LogBean;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogsProps;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IVanillaLoggingManager {
	public static enum ActionType implements IXmlActionType{
		ADD_BIW_LOG(Level.DEBUG), ADD_VANILLA_LOG(Level.DEBUG), ADD_VANILLA_LOG_PROP(Level.DEBUG), DEL_LOG_BEAN(Level.DEBUG), LIST_BIW_LOGS(Level.DEBUG),
		FIND_BIW_LOG(Level.DEBUG), LIST_VANILLA_LOGS(Level.DEBUG),LIST_BEAN_LOG_FOR_INSTANCE(Level.DEBUG),LIST_BEAN_LOG_4SERVER_URL(Level.DEBUG),
		FIND_VANILLA_LOG(Level.DEBUG),LIST_VANILLA_LOGS_PROPERTIES(Level.DEBUG),LIST_VANILLA_LOGS_PROPS_4LOG_ID(Level.DEBUG),
		FIND_VANILLA_LOG_PROP(Level.DEBUG),UPDATE_BIW_LOG(Level.DEBUG),UPDATE_VANILLA_LOG(Level.DEBUG),UPDATE_VANILLA_LOG_PROP(Level.DEBUG), ADD_LOG_BEAN(Level.DEBUG),
		ADD_UOLAP_QUERY_BEAN(Level.DEBUG), DEL_UOLAP_QUERY_BEAN(Level.DEBUG), LIST_UOLAP_QUERY(Level.DEBUG),
		ADD_FMDT_QUERY_BEAN(Level.DEBUG), DEL_FMDT_QUERY_BEAN(Level.DEBUG), LIST_FMDT_QUERY(Level.DEBUG), GET_SECURITY_LOGS(Level.DEBUG), SAVE_SECURITY_LOG(Level.DEBUG), GET_VANILLA_LOGS_BY_ID(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
		
	}
	public int addBiwLog(BiwLogs log) throws Exception;
	public void updateBiwLog(BiwLogs log) throws Exception;
	public List<BiwLogs> getBiwLogs() throws Exception;
	public BiwLogs getBiwLogsById(int logId) throws Exception;
	
	public void deleteLogBean(int logId) throws Exception;
	public List<LogBean> getLogBeans(String runtimeServerUrl) throws Exception;
	public List<LogBean> getLogBeanForRunning(int runningInstanceId) throws Exception;
	
	public int addVanillaLog(VanillaLogs log) throws Exception;
	public List<VanillaLogs> getListVanillaLogs() throws Exception;
	public List<VanillaLogs> getListVanillaLogs(int itemId, String type) throws Exception;
	public VanillaLogs getVanillaLogsById(int logId) throws Exception;
	public void updateVanillaLog(VanillaLogs log) throws Exception;
	
	public int addVanillaLogsProperty(VanillaLogsProps prop) throws Exception;
	public void updateVanillaLogsProperty(VanillaLogsProps prop) throws Exception;
	public List<VanillaLogsProps> getVanillaLogsProperties() throws Exception;
	public VanillaLogsProps getVanillaLogsPropertyById(int propertyId) throws Exception;
	public List<VanillaLogsProps> getVanillaLogsPropertiesForLogId(int logId) throws Exception;
	public void addLogBean(LogBean logBean) throws Exception;
	
	public List<UOlapQueryBean> getUolapQueries() throws Exception;
	public List<UOlapQueryBean> getUolapQueries(IObjectIdentifier identifier) throws Exception;
	public boolean addUolapQuery(UOlapQueryBean bean) throws Exception;
	public boolean deleteUolapQuery(UOlapQueryBean bean) throws Exception;

	
	public List<FMDTQueryBean> getFmdtQueries(int numberFiles) throws Exception;
//	public List<FMDTQueryBean> getFmdtQueries(IObjectIdentifier identifier) throws Exception;
	public boolean addFmdtQuery(FMDTQueryBean bean) throws Exception;
	public boolean deleteFmdtQuery(FMDTQueryBean bean) throws Exception;

	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) throws Exception;
	public void saveSecurityLog(SecurityLog log) throws Exception;
}
