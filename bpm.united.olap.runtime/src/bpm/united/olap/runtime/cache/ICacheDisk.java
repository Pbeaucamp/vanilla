package bpm.united.olap.runtime.cache;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.IIndexCacheDisk;
import bpm.united.olap.api.result.OlapResult;

public interface ICacheDisk {
	
	public OlapResult getCachedView(String repositoryId, String directoryItemId, String schemaId, String groupId, String mdxQuery) throws Exception;
	
	public void cacheView(String repositoryId, String directoryItemId, String schemaId, String groupId, String mdxQuery, OlapResult data);
	
	public void configure(ICacheDiskConfiguration config);
	
	public ICacheDiskConfiguration getConfig();
	
	public IIndexCacheDisk getIndex();
	
	public void purge();
	
	public void saveIndex();

	public void remove(CacheKey key);
	
	public void removeForSchema(String schemaId);
}
