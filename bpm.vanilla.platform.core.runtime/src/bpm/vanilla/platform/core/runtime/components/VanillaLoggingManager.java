package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
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
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.runtime.dao.logs.BiwLogsDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.FmdtQueriesLoggerDao;
import bpm.vanilla.platform.core.runtime.dao.logs.LogDao;
import bpm.vanilla.platform.core.runtime.dao.logs.UolapQueriesLoggerDao;
import bpm.vanilla.platform.core.runtime.dao.logs.VanillaLogsDAO;
import bpm.vanilla.platform.core.runtime.dao.logs.VanillaLogsPropsDAO;
import bpm.vanilla.platform.core.runtime.dao.security.SecurityLogDAO;

public class VanillaLoggingManager extends AbstractVanillaManager implements IVanillaLoggingManager {

	private BiwLogsDAO biwLogDao;
	private LogDao logDao;
	private VanillaLogsDAO vanillaLogDao;
	private VanillaLogsPropsDAO vanillaLogPropDao;
	private UolapQueriesLoggerDao uolapQueryDao;
	private FmdtQueriesLoggerDao fmdtQueryDao;
	private SecurityLogDAO securityLogDao;

	private int logLevel;

	@Override
	public void updateBiwLog(BiwLogs d) {
		biwLogDao.update(d);
	}

	@Override
	public BiwLogs getBiwLogsById(int id) {
		return biwLogDao.findByPrimaryKey(id);
	}

	@Override
	public List<BiwLogs> getBiwLogs() {
		return biwLogDao.findAll();
	}

	@Override
	public int addBiwLog(BiwLogs d) {
		return biwLogDao.save(d);
	}

	@Override
	public List<LogBean> getLogBeans(String runtimeServerUrl) throws Exception {
		return logDao.getAll(runtimeServerUrl);
	}

	@Override
	public void deleteLogBean(int id) throws Exception {
		logDao.delete(id);
	}

	@Override
	public List<LogBean> getLogBeanForRunning(int id) throws Exception {
		return logDao.getForRunningInstanceId(id);
	}

	@Override
	public void updateVanillaLog(VanillaLogs d) {
		vanillaLogDao.update(d);
	}

	@Override
	public List<VanillaLogs> getListVanillaLogs() {
		return vanillaLogDao.findAll();
	}

	@Override
	public VanillaLogs getVanillaLogsById(int id) {
		return vanillaLogDao.findByPrimaryKey(id);
	}

	@Override
	public int addVanillaLog(VanillaLogs d) {
		if (logLevel <= VanillaLogs.Level.valueOf(d.getLevel()).getLevelId()) {
			try {
				return vanillaLogDao.save(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	public void updateVanillaLogsProperty(VanillaLogsProps d) {
		vanillaLogPropDao.update(d);
	}

	@Override
	public List<VanillaLogsProps> getVanillaLogsProperties() {
		return vanillaLogPropDao.findAll();
	}

	@Override
	public List<VanillaLogsProps> getVanillaLogsPropertiesForLogId(int id) {
		return vanillaLogPropDao.findByVanillaLogId(id);
	}

	@Override
	public VanillaLogsProps getVanillaLogsPropertyById(int id) {
		return vanillaLogPropDao.findByPrimaryKey(id);
	}

	// public VanillaLogsProps getVanillaLogsProps() {
	// return getListVanillaLogsProps().get(0);
	// }
	@Override
	public int addVanillaLogsProperty(VanillaLogsProps d) {
		return vanillaLogPropDao.save(d);
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {

		String level = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOG_LEVEL);
		if (level == null) {
			logLevel = 1;
		}
		else {
			logLevel = VanillaLogs.Level.valueOf(level).getLevelId();
		}

		this.biwLogDao = getDao().getBiwLogsDao();
		if (this.biwLogDao == null) {
			throw new Exception("Missing BiwLogDao!");
		}
		this.logDao = getDao().getLogDao();
		if (this.logDao == null) {
			throw new Exception("Missing LogDao!");
		}
		this.vanillaLogDao = getDao().getVanillaLogsDao();
		if (this.vanillaLogDao == null) {
			throw new Exception("Missing VanillaLogDao!");
		}
		this.vanillaLogPropDao = getDao().getVanillaLogsPropsDao();
		if (this.vanillaLogPropDao == null) {
			throw new Exception("Missing VanillaLogPropDao!");
		}

		this.uolapQueryDao = getDao().getUolapQueryLogDao();
		if (this.uolapQueryDao == null) {
			throw new Exception("Missing UolapQueryLogDao!");
		}

		this.fmdtQueryDao = getDao().getFmdtQueryLogDao();
		if (this.fmdtQueryDao == null) {
			throw new Exception("Missing FmdtQueryLogDao!");
		}

		this.securityLogDao = getDao().getSecurityLogDao();
		if (this.securityLogDao == null) {
			throw new Exception("Missing SecurityLogDao!");
		}

		getLogger().info("init done!");
	}

	@Override
	public void addLogBean(LogBean logBean) throws Exception {
		this.logDao.save(logBean);

	}

	@Override
	public boolean addUolapQuery(UOlapQueryBean bean) throws Exception {
		return uolapQueryDao.add(bean);
	}

	@Override
	public boolean deleteUolapQuery(UOlapQueryBean bean) throws Exception {
		return uolapQueryDao.delete(bean);
	}

	@Override
	public List<UOlapQueryBean> getUolapQueries() throws Exception {
		return uolapQueryDao.list();
	}

	@Override
	public List<UOlapQueryBean> getUolapQueries(IObjectIdentifier identifier) throws Exception {
		return uolapQueryDao.list(identifier);
	}

	@Override
	public boolean addFmdtQuery(FMDTQueryBean bean) throws Exception {
		return fmdtQueryDao.add(bean);
	}

	@Override
	public boolean deleteFmdtQuery(FMDTQueryBean bean) throws Exception {
		return fmdtQueryDao.delete(bean);
	}

	@Override
	public List<FMDTQueryBean> getFmdtQueries(int numberFiles) throws Exception {
//		List<FMDTQueryBean> logsToReturn = new ArrayList<>();

		List<FMDTQueryBean> logs = fmdtQueryDao.list();

//		if (logs != null) {
//			for (int i = 0; i < numberFiles; i++) {
//				if (logs.size() > numberFiles) {
//					logsToReturn.add(logs.get(i));
//				}
//			}
//		}
		return logs;
	}

	@Override
	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) throws Exception {
		return securityLogDao.getSecurityLogs(userId, type, startDate, endDate);
	}

	@Override
	public void saveSecurityLog(SecurityLog log) throws Exception {
		securityLogDao.save(log);
	}

	@Override
	public List<VanillaLogs> getListVanillaLogs(int itemId, String type) throws Exception {
		return vanillaLogDao.getListVanillaLogs(itemId, type);
	}
}
