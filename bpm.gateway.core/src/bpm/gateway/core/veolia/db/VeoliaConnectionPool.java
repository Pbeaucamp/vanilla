package bpm.gateway.core.veolia.db;

import java.util.HashMap;
import java.util.UUID;

/**
 * 
 *To avoid having multiple DAO to load App or Aff from TSBN 
 *
 */
public class VeoliaConnectionPool {
	
	private HashMap<String, VeoliaAbonnesDaoComponent> abonneDaos = new HashMap<String, VeoliaAbonnesDaoComponent>();
	private HashMap<String, VeoliaPatrimoineDaoComponent> patrimoineDaos = new HashMap<String, VeoliaPatrimoineDaoComponent>();
	
	public VeoliaAbonnesDaoComponent getAbonneDao(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (abonneDaos.get(key) != null) {
			VeoliaAbonnesDaoComponent con = abonneDaos.get(key);
			return con;
		}

		VeoliaAbonnesDaoComponent appDao = createAbonneDao(url, user, password, driverClass);
		abonneDaos.put(key, appDao);
		return appDao;
	}

	private VeoliaAbonnesDaoComponent createAbonneDao(String url, String user, String password, String driverClass) throws Exception {
		VeoliaAbonnesDaoComponent veoliaDaoComp = new VeoliaAbonnesDaoComponent();
		veoliaDaoComp.init(driverClass, url, user, password);
		return veoliaDaoComp;
	}
	
	public VeoliaPatrimoineDaoComponent getPatrimoineDao(String url, String user, String password, String driverClass) throws Exception {
		String key = generateConnectionKey(url, user, password, driverClass);
		if (patrimoineDaos.get(key) != null) {
			VeoliaPatrimoineDaoComponent con = patrimoineDaos.get(key);
			return con;
		}

		VeoliaPatrimoineDaoComponent appDao = createPatrimoineDao(url, user, password, driverClass);
		patrimoineDaos.put(key, appDao);
		return appDao;
	}

	private VeoliaPatrimoineDaoComponent createPatrimoineDao(String url, String user, String password, String driverClass) throws Exception {
		VeoliaPatrimoineDaoComponent veoliaDaoComp = new VeoliaPatrimoineDaoComponent();
		veoliaDaoComp.init(driverClass, url, user, password);
		return veoliaDaoComp;
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
