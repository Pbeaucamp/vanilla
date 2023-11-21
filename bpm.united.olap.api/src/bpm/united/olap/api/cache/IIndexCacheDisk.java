package bpm.united.olap.api.cache;

import java.io.File;
import java.util.List;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;

public interface IIndexCacheDisk {
	
	public ICacheEntry getEntry(CacheKey key);
	
	public boolean saveEntry(CacheKey key, ICacheEntry entry);
	
	
	public long getSize();
	
	public long getStorageSize();
	

	public boolean removeEntry(CacheKey key);
	
	
	public List<CacheKey> getKeys();

	public void clear();
	
	public void clearOldest(long spaceToFree);
	
	public void persistFileIndex(File folderDestination);
	
	public void reloadIndex(File folder);
	
}
