package bpm.vanilla.server.commons.pool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryContext;


public class Pool {
	private GenericKeyedObjectPool pool;
	private List<VanillaItemKey> storedKeys = new ArrayList<VanillaItemKey>();
	private Logger logger;
	private int poolSize = 10;
//	private long refreshRepositoriesTime = 60 * 1000 * 30; // 30 minutes;
//	private SocketPool socketPool;
	
	/**
	 * 
	 * @param poolSize : max simultaneous objects in the pool
	 * @param refreshRepositoriesTime : delay in milisec between to repository content reload
	 * @param logger
	 */
	public Pool(int poolSize/*, long refreshRepositoriesTime*/, PoolableModelFactory factory, Logger logger){
		this.logger = logger;
		if (this.logger == null){
			this.logger = Logger.getLogger(Pool.class);
		}
		this.poolSize = poolSize;
//		this.refreshRepositoriesTime = refreshRepositoriesTime;
		pool = new GenericKeyedObjectPool();
		pool.setFactory(factory);
		pool.setMaxTotal(poolSize);
		pool.setTestOnBorrow(true);
		
		
	}
	
//	protected void setSocketPool(SocketPool socketPool){
//		this.socketPool = socketPool;
//	}
//	
//	public SocketPool getSocketPool(){
//		return this.socketPool;
//	}
	
	public Pool(PoolableModelFactory factory, Logger logger){
		this.logger = logger;
		if (this.logger == null){
			this.logger = Logger.getLogger(Pool.class);
		}
		
		pool = new GenericKeyedObjectPool();
		pool.setFactory(factory);
		pool.setMaxTotal(poolSize);
		pool.setTestOnBorrow(true);
	}
	
	/**
	 * call returnObject just after this method to return the RepositrySockMOdel to the pool
	 * if it is not done, the caller may still be in object.wait()
	 * 
	 * 
	 * if the pool is full, all unused objects will be released
	 * @param repId
	 * @param login
	 * @param password
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public PoolableModel<?> borrow(IRepositoryContext repositoryCrx, int directoryItemId) throws Exception{
		VanillaItemKey key = getKey(repositoryCrx, directoryItemId);
		Object o = pool.borrowObject(key);
		PoolableModel<?> model = (PoolableModel<?>) o;
//		/*
//		 * check if reload is needed
//		 */
//		long current = Calendar.getInstance().getTime().getTime();
//		if (current - model.getLastRefresh().getTime() > refreshRepositoriesTime){
//			logger.info("Pool refreshing an object");
//			model.refresh();
//			logger.info("Pool refreshed an object");
//		}
		
		return model;
	}
	
	
	/**
	 * look for the registered Keys if aOn equals exists
	 * if not, create a new key and store it
	 * @param repId
	 * @param login
	 * @param password
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	private VanillaItemKey getKey(IRepositoryContext repositoryCrx, int directoryItemId) throws Exception{
		VanillaItemKey _key = null;
		for(VanillaItemKey key : storedKeys){
			if (key.getDirectoryItemId() == directoryItemId && key.getRepositoryContext().equals(repositoryCrx)){
				_key = key;
				logger.debug("Pool Key found");
				break;
			}
		}
		if (_key == null){
			_key = new VanillaItemKey(repositoryCrx, directoryItemId);
			logger.debug("Pool Key created");
			
			
			if (storedKeys.size() >= poolSize ){
				clearPool();
			}
		}
		

		storedKeys.add(_key);
		
		return _key;
	}



	public void returnObject(IRepositoryContext repositoryCrx, int directoryItemId, PoolableModel model)throws Exception{
		pool.returnObject(getKey(repositoryCrx,directoryItemId), model);
	}
	
	/**
	 * remove all unused Object from the pool
	 */
	public void clearPool(){
		
		logger.info("clear pool");
		pool.clear();
		storedKeys.clear();
	}
}
