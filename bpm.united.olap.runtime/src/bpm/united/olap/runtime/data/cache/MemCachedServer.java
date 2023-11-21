package bpm.united.olap.runtime.data.cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.MemcachedClient;

import org.apache.log4j.Logger;

import bpm.united.olap.runtime.cache.ICacheConfiguration;
import bpm.united.olap.runtime.cache.ICacheServer;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

public abstract class MemCachedServer implements ICacheServer {

	protected ICacheConfiguration configuration;
	private MemcachedClient client;
	private MemCacheDaemon<LocalCacheElement> server;
	
	public void dump(){}
	public void checkAndRestartServer(){
		if (server != null && server.isRunning()){
			Logger.getLogger(getClass()).debug("Memcache server is running, no need to restart it");
			return;
		}
		
//		SyncRequest d = null;
		Logger.getLogger(getClass()).debug("Restarting MemCached server ...");
		
		server = new MemCacheDaemon<LocalCacheElement>();
		int cacheSize = configuration.getCacheMemoryLimit()*1024*1024;
		CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap.create(ConcurrentLinkedHashMap.EvictionPolicy.LRU, configuration.getMaxCachedElements(), cacheSize);
		server.setCache(new CacheImpl(storage));
//		server.setBinary(true);
		server.setAddr(new InetSocketAddress(configuration.getHostName(), configuration.getPort()));
		server.setIdleTime(configuration.getCacheExpirationTime());
		server.setVerbose(false);
		server.start();
		
		Logger.getLogger(getClass()).debug("MemCached server started");
	}
	
	
	protected void initCacheClient(){
		try {
			List<InetSocketAddress> l = new ArrayList<InetSocketAddress>();
			l.add( new InetSocketAddress(configuration.getHostName(), configuration.getPort()));
			MemcacheConnectionFactory f = new MemcacheConnectionFactory(){
				@Override
				public long getOperationTimeout() {
					
					return 5000;
				}
			};
			
			client = new MemcachedClient(f, l);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void configure(ICacheConfiguration configuration) {
		this.configuration = configuration;

		checkAndRestartServer();
		
		initCacheClient();
	}
	

	@Override
	public ICacheConfiguration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void clearCache() {
		client.flush();
	}

	@Override
	public void killServer() {
		client.shutdown();
	}

	@Override
	public long getCacheMaxSize() {
		return server.getCache().getLimitMaxBytes();
	}

	@Override
	public long getCacheUsedSize() {
		return server.getCache().getCurrentBytes();
	}

	@Override
	public long getCachedItemsNumber() {
		return server.getCache().getCurrentItems();
	}
}
