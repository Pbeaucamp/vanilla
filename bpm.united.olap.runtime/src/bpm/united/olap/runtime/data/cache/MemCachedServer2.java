package bpm.united.olap.runtime.data.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheDisk;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.disk.CacheDisk;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCachedServer2 extends MemCachedServer{
	private MemCachedClient client;
	private ICacheDisk cacheDisk = new CacheDisk();
	
	
	protected MemCachedClient getMemcacheClient(){
		return client;
	}
	
	@Override
	protected void initCacheClient() {
		this.client = new MemCachedClient();
		
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(new String[]{configuration.getHostName() + ":" + configuration.getPort()});
		pool.setWeights(new Integer[]{1});
		pool.setInitConn( 5 );
		pool.setMinConn( 5 );
		pool.setMaxConn( 250 );
		pool.setMaxIdle( 1000 * 60 * 60 * 6 );
		pool.setMaintSleep( 30 );
		pool.setMaxBusyTime(1000 * 60); //1 minute
		pool.setNagle( false );
		pool.setSocketTO( 3000 );
		pool.setSocketConnectTO( 0 );
		
		pool.initialize();
		

//		this.client.setCompressEnable( true );
//		this.client.setCompressThreshold( 64 * 1024 );
	}
	
	@Override
	public void clearCache() {
		client.flushAll();
			
	}
	
	@Override
	public void killServer() {
		cacheDisk.purge();
	}
	
	
	
	public Map<String, ICacheable> getBulkCached(List<String> keys) throws Exception{
		//BulkFuture<Map<String, Object>> map = client.asyncGetBulk(keys);
		if (client == null){
			return new HashMap<String, ICacheable>();
		}
		Map m = (Map)client.getMulti(keys.toArray(new String[keys.size()]));//map.get(keys.size() /500  , TimeUnit.SECONDS);
		
		Logger.getLogger(getClass()).debug("Bulk retrieve " + m.size() + " on " + keys.size());
		return m;
	}
	
//	protected ICacheable getCachedObject(String key) {
//		ICacheable result = null;
//		try {
//			result =  (ICacheable)client.get(key);
//		} catch (Exception e) {
//			Logger.getLogger(getClass()).debug("Error while get from cache : " + key,e);
//		}
//		return result;
//	}
	
	public void dump(){
		StringBuilder b = new StringBuilder();
		
		Map<String, Map<String, String>> map = client.stats();
		
		
		for(String s : map.keySet()){
			b.append(s + " :\n");
			
			for(String k : map.get(s).keySet()){
				b.append(k + "=" + map.get(s).get(k) + "\n"); 
			}
		}
		
		Logger.getLogger(getClass()).debug(b.toString());
		
	}
	
	
	
	
	

	@Override
	public boolean addToCache(ICacheable object) {
		try {
			if (client == null){
				return false;
			}
			boolean result = false;
			
					
			object.updateExpectedEndDate();
			result = client.add(object.getKey(), object, object.getExpectedEndDate());
			
			if (!result){
				result = client.set(object.getKey(), object, object.getExpectedEndDate());
			}
			return result;
		} catch(Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ICacheable getCached(String key) {
		if (client == null){
			return null;
		}
		Object o = client.get(key);
		if (o != null){
			return (ICacheable)o;
		}

		return null;
	}
	

	@Override
	public boolean unloadObject(Object  object)throws Exception {
		String key = null;
		
		if (object instanceof Schema){
			return unloadSchema((Schema)object);
		}
		else if (object instanceof Level){
			key = CacheKeyGenerator.generateKey((Level)object);
		}
		else if (object instanceof Cube){
			key = CacheKeyGenerator.generateKey((Cube)object);
		}
		else if (object instanceof ICubeInstance){
			key = CacheKeyGenerator.generateKey((ICubeInstance)object);
		}
		else{
			throw new Exception("Class " + object.getClass().getName() + " not supported");
		}
		
		boolean b = false;
		try {
			b=client.delete(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
		
	}

	@Override
	public boolean unloadSchema(Schema schema) {
		
		boolean performed = false;
		for(Cube c : schema.getCubes()){
			if(client != null) {
				performed = performed || client.delete(CacheKeyGenerator.generateKey(c));
			}
		}
		
		for(Dimension d : schema.getDimensions()){
			for(Hierarchy h : d.getHierarchies()){
				for(Level l : h.getLevels()){
					if(client != null) {
						performed = performed || client.delete(CacheKeyGenerator.generateKey(l));
					}
				}
			}
		}
		
		return performed;
	}

	
	private String getStat(String name){
		Map<String, Map<String, String>> map = client.stats();
		
		String val = null;
		for(String s : map.keySet()){
						
			val = map.get(s).get(name);
			if (val != null){
				return val;
			}
		}
		
		return null;
	}
	
	@Override
	public int getHits() {
		String s = getStat("get_hits");
		if (s == null){
			return 0;
		}
		return Integer.parseInt(s.trim());
	}

	@Override
	public int getMissed() {
		String s = getStat("get_misses");
		if (s == null){
			return 0;
		}
		return Integer.parseInt(s.trim());
	}

	@Override
	public ICacheDisk getCacheDisk() {
		return cacheDisk;
	}
}
