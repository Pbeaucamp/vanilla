package bpm.gateway.core.veolia.db;

public class VeoliaConnectionManager {
	private static VeoliaConnectionManager instance;
	private VeoliaConnectionPool tsbnPool;

	private VeoliaConnectionManager() throws Exception {
		tsbnPool = new VeoliaConnectionPool();
	}
	
	public static VeoliaConnectionManager getInstance() throws Exception {
		if(instance == null) {
			instance = new VeoliaConnectionManager();
		}
		return instance;
	}
	
	public VeoliaAbonnesDaoComponent getAbonneDao(String url, String user, String password, String driverClass) throws Exception {
		return tsbnPool.getAbonneDao(url, user, password, driverClass);
	}
	
	public VeoliaPatrimoineDaoComponent getPatrimoineDao(String url, String user, String password, String driverClass) throws Exception {
		return tsbnPool.getPatrimoineDao(url, user, password, driverClass);
	}
}
