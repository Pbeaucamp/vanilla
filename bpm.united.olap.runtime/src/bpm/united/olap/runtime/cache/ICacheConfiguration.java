package bpm.united.olap.runtime.cache;

import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Configuration for the cache server
 * @author Marc Lanquetin
 *
 */
public interface ICacheConfiguration {
	
	/**
	 * 
	 * @return cache server hostname
	 */
	String getHostName();
	
	/**
	 * 
	 * @return cache server port number
	 */
	int getPort();
	
	/**
	 * 
	 * @return cached datas expiration time (in seconds)
	 */
	int getCacheExpirationTime();
	
	
	/**
	 * this datas is used to persist some important datas like
	 * the Schema,ICubeInstance this time should be long otherwise the cache will be ineffective
	 * 
	 * @return cached datas expiration time (in seconds)
	 */
	int getMainDatasCacheExpirationTime();
	
	
	/**
	 * 
	 * @return the amount of memory allowed for cache (in MBytes)
	 */
	int getCacheMemoryLimit();
	
	/**
	 * 
	 * @return the max elements can be kept in cache
	 */
	int getMaxCachedElements();
	
	/**
	 * 
	 * @return
	 */
//	IVanillaLogger getLogger();
	
}
