package bpm.gateway.core.tsbn.db;

public class TsbnConnectionManager {
	private static TsbnConnectionManager instance;
	private TsbnConnectionPool tsbnPool;

	private TsbnConnectionManager() throws Exception {
		tsbnPool = new TsbnConnectionPool();
	}
	
	public static TsbnConnectionManager getInstance() throws Exception {
		if(instance == null) {
			instance = new TsbnConnectionManager();
		}
		return instance;
	}
	
	public TsbnAppDaoComponent getTsbnAppDao(String url, String user, String password, String driverClass) throws Exception {
		return tsbnPool.getAppDao(url, user, password, driverClass);
	}
	
	public TsbnAffDaoComponent getTsbnAffDao(String url, String user, String password, String driverClass) throws Exception {
		return tsbnPool.getAffDao(url, user, password, driverClass);
	}
	
	public TsbnRefDaoComponent getTsbnRefDao(String url, String user, String password, String driverClass) throws Exception {
		return tsbnPool.getRefDao(url, user, password, driverClass);
	}
}
