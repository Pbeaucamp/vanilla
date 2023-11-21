package bpm.gateway.core.tsbn.db;

import java.util.HashMap;
import java.util.UUID;

/**
 * 
 *To avoid having multiple DAO to load App or Aff from TSBN 
 *
 */
public class TsbnConnectionPool {
	
	private HashMap<String, TsbnAppDaoComponent> appDaos = new HashMap<String, TsbnAppDaoComponent>();
	private HashMap<String, TsbnAffDaoComponent> affDaos = new HashMap<String, TsbnAffDaoComponent>();
	private HashMap<String, TsbnRefDaoComponent> refDaos = new HashMap<String, TsbnRefDaoComponent>();

	public TsbnAppDaoComponent getAppDao(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (appDaos.get(key) != null) {
			TsbnAppDaoComponent con = appDaos.get(key);
			return con;
		}

		TsbnAppDaoComponent appDao = createAppDao(url, user, password, driverClass);
		appDaos.put(key, appDao);
		return appDao;
	}

	private TsbnAppDaoComponent createAppDao(String url, String user, String password, String driverClass) throws Exception {
		TsbnAppDaoComponent tsbnDaoComp = new TsbnAppDaoComponent();
		tsbnDaoComp.init(driverClass, url, user, password);
		return tsbnDaoComp;
	}
	
	public TsbnAffDaoComponent getAffDao(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (affDaos.get(key) != null) {
			TsbnAffDaoComponent con = affDaos.get(key);
			return con;
		}

		TsbnAffDaoComponent appDao = createAffDao(url, user, password, driverClass);
		affDaos.put(key, appDao);
		return appDao;
	}

	private TsbnAffDaoComponent createAffDao(String url, String user, String password, String driverClass) throws Exception {
		TsbnAffDaoComponent tsbnDaoComp = new TsbnAffDaoComponent();
		tsbnDaoComp.init(driverClass, url, user, password);
		return tsbnDaoComp;
	}
	
	public TsbnRefDaoComponent getRefDao(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (affDaos.get(key) != null) {
			TsbnRefDaoComponent con = refDaos.get(key);
			return con;
		}

		TsbnRefDaoComponent appDao = createRefDao(url, user, password, driverClass);
		refDaos.put(key, appDao);
		return appDao;
	}

	private TsbnRefDaoComponent createRefDao(String url, String user, String password, String driverClass) throws Exception {
		TsbnRefDaoComponent tsbnDaoComp = new TsbnRefDaoComponent();
		tsbnDaoComp.init(driverClass, url, user, password);
		return tsbnDaoComp;
	}

	private String generateConnectionKey(String url, String user, String password, String driverClass) {
		StringBuffer buf = new StringBuffer();
		buf.append(url);
		buf.append(user);
		buf.append(password);
		buf.append(driverClass);
		String key = UUID.nameUUIDFromBytes(buf.toString().getBytes()).toString();
		return key;
	}
}
