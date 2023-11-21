package bpm.united.olap.api.cache;

import java.io.InputStream;
import java.util.List;

import bpm.united.olap.api.cache.impl.DiskCacheStatistics;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;

/**
 * Use to manage the UnitedOlap Cache from the outside
 * @author ludo
 *
 */
public interface IUnitedOlapCacheManager {
	public MemoryCacheStatistics getMemoryCacheStatistics() throws Exception;
	
	public void clearMemoryCache() throws Exception;
	
	public void clearDiskCache() throws Exception;
	
	public DiskCacheStatistics getDiskCacheStatistics() throws Exception;
	
	public List<CacheKey> getDiskCacheKeys() throws Exception;
	
	public ICacheEntry getDiskCacheEntry(CacheKey key) throws Exception;

	public void removeFromCacheDisk(CacheKey key) throws Exception;
	
	public void persistCacheDisk() throws Exception;
	
	public void appendToCacheDisk(InputStream zippedFiles, boolean override) throws Exception;
	
	public InputStream loadCacheEntry(CacheKey key) throws Exception;
}
