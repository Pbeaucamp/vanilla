package bpm.vanilla.server.commons.pool;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public abstract class PoolableModelFactory  extends BaseKeyedPoolableObjectFactory {

	protected Logger logger; 
	
	public PoolableModelFactory(Logger logger){
		this.logger = logger;
		if (this.logger == null){
			this.logger = Logger.getLogger(PoolableModelFactory.class);
		}
	}
	
	@Override
	public boolean validateObject(Object key, Object obj) {
		logger.debug("Validating PoolableModel");
		VanillaItemKey vKey = (VanillaItemKey)key;
		PoolableModel model = (PoolableModel)obj;
		
		try{
			IRepositoryApi sock = new RemoteRepositoryApi(vKey.getRepositoryContext());
			if (sock.getRepositoryService().checkItemUpdate(model.getDirectoryItem(), model.getDirectoryItem().getDateModification())){
				return false;
			}
		}catch(Exception ex){
			logger.warn("Error when validating PoolableModel - " + ex.getMessage(), ex);
			return false;
		}
		
		return true;
	}
	@Override
	public Object makeObject(Object _key) throws Exception {
		if (!(_key instanceof VanillaItemKey)){
			throw new Exception("Cannot make an object for a non VanillaItemKey key object");
		}
		
		VanillaItemKey key = (VanillaItemKey)_key;
		
		IRepositoryApi sock = new RemoteRepositoryApi(key.getRepositoryContext());
		
		//sock.getBrowseClient().getDirectoryItem(2);
		//sock.getBrowseClient().
		RepositoryItem repIt = sock.getRepositoryService().getDirectoryItem(key.getDirectoryItemId());
		if (repIt == null) {
			throw new Exception("Failed to load a repository object with dirItemId = " + key.getDirectoryItemId());
		}
		
		
		
		
		String xml = null;
		try{
			xml = sock.getRepositoryService().loadModel(repIt);
		}catch(Exception ex){
			throw new Exception("unable to load model xml from reppository", ex);
		}
		
		
		/*
		 * url replacement rule applications
		 */
		xml = ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(xml);
		
		
		
		return createPoolableModel(repIt, xml, key);
	}

	abstract protected PoolableModel createPoolableModel(RepositoryItem item, String modelXml, VanillaItemKey itemKey) throws Exception;
}
