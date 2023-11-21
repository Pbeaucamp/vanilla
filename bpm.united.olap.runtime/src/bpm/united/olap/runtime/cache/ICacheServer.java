package bpm.united.olap.runtime.cache;

import java.util.List;
import java.util.Map;

import bpm.united.olap.api.model.Schema;

/**
 * Represents a cache server
 * @author Marc Lanquetin
 *
 */
public interface ICacheServer {
	
	
	public ICacheDisk getCacheDisk();
	
	/**
	 * dump cache datas 
	 */
	public void dump();
	
	/**
	 * check the if the server is running, if not it is restarted
	 */
	public void checkAndRestartServer();

//	/**
//	 * 
//	 * @param node
//	 * @return 
//	 */
//	public List<ICacheable> getCached(ICacheable node);
	
	
//	/**
//	 * return the Roots Cached Object(meaning the Schema's cached)
//	 */
//	public List<ICacheable> getRoots();
	
	/**
	 * 
	 * @param key
	 * @return the Cached Object with the given key
	 */
	public ICacheable getCached(String key);
	
//	public CacheNode getNode(String key, CacheNode dummyNode);
	
	/**
	 * put the given in the cache
	 * 
	 * The cacheNode is used to avoid loosing the treeStructure of the Cache
	 * It will be used to refresh nor re-cache the parents nodes of the cached Object
	 * 
	 * 
	 * @param object
	 * @param cacheNode
	 * @return true if the add has been successfull
	 */
	public boolean addToCache(ICacheable object);
	
	
//	public void checkAndUpdateTreeStructure(CacheNode parent, String addedChildKey);
	
	/**
	 * remove the given object from the cache
	 * (supported object : Schema, Level, Cube, ICubeInstance)
	 * @param key
	 */
	public boolean unloadObject(Object object) throws Exception;

	/**
	 * remove all elements from the cache for this shcema
	 * @param schema
	 */
	public boolean unloadSchema(Schema schema);
	
	
	/**
	 * load the object with the given keys
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public Map<String, ICacheable> getBulkCached(List<String> keys) throws Exception;
	/**
	 * 
	 * @return the server configuation
	 */
	public ICacheConfiguration getConfiguration();
	
//	/**
//	 * Get a cached object from its key
//	 * @param key
//	 * @return 
//	 */
//	Object getCachedObject(String key);
//	
//	/**
//	 * Cache an object for this key
//	 * If the key already exists, the previous cached object will be replaced
//	 * @param key
//	 * @param obj
//	 */
//	void cacheObject(String key, Object obj);
	
	/**
	 * Clear all cache on this server
	 */
	public void clearCache();
	
	/**
	 * remove the cached item for this key
	 * @param key
	 */
//	void removeCachedItem(String key);
	
	/**
	 * Kill the cache server
	 */
	public void killServer();
	
	/**
	 * get the actual cache used size in bytes
	 * @return
	 */
	public long getCacheUsedSize();
	
	/**
	 * get the number of cached item
	 * @return
	 */
	public long getCachedItemsNumber();
	
	/**
	 * get the max size of the cache server in bytes
	 * @return
	 */
	public long getCacheMaxSize();

	public int getHits();

	public int getMissed();
}
