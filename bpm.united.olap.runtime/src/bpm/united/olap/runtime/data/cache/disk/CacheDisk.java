package bpm.united.olap.runtime.data.cache.disk;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.IIndexCacheDisk;
import bpm.united.olap.api.cache.impl.CacheEntry;
import bpm.united.olap.api.cache.impl.CacheIndex;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.runtime.cache.ICacheDisk;
import bpm.united.olap.runtime.cache.ICacheDiskConfiguration;
import bpm.united.olap.runtime.cache.OlapResultMarshaller;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class CacheDisk implements ICacheDisk{
	
	private OlapResultMarshaller marshaller = new OlapResultMarshaller();
	
	private static class Couple{
		CacheKey key;
		OlapResult result;
		String mdx;
		String groupName;
		public Couple(CacheKey key, OlapResult result, String mdx, String groupName){
			this.key = key;
			this.result = result;
			this.mdx = mdx;
			this.groupName = groupName;
		}
	}
	
	
	private class ThreadWriter extends Thread{
		public void run(){
			Logger.getLogger(getClass()).info("Cache Disk Writer started...");
			while(!cachingQueue.isEmpty()){
				Couple couple = cachingQueue.poll();
				try{
					ICacheEntry entry = marshall(couple.key, couple.result, couple.mdx, couple.groupName);
					Logger.getLogger(getClass()).debug("Marshalled " + couple.key);
					index.saveEntry(couple.key, entry);
					Logger.getLogger(getClass()).debug("Indexed " + couple.key + " " + entry.getFile().getAbsolutePath());
				}catch(Exception ex){
					Logger.getLogger(getClass()).error("Unable to marshall OlapResult for key " + couple.key.toString() + " - " + ex.getMessage(), ex);
				}
				
				if (index.getStorageSize() >= getConfig().getMaximumStorageSize() * 0.8){
					index.clearOldest(index.getStorageSize() - (long)(getConfig().getMaximumStorageSize() * 0.8));
				}
			}
			Logger.getLogger(getClass()).info("Cache Disk Writer stopped");
		}
	}
	
	/**
	 * contains the the couple that are currently been serialized
	 */
	private ConcurrentLinkedQueue<Couple> cachingQueue = new ConcurrentLinkedQueue<Couple>();
	
	/**
	 * the index of the cache
	 */
	private IIndexCacheDisk index = new CacheIndex();
	
	/**
	 * a Thread that will perform the marshalling part 
	 */
	private ThreadWriter writer;
	
	private ICacheDiskConfiguration config ;

	
	
	
	public CacheDisk(){
		ICacheDiskConfiguration config = null;
		if (System.getProperty("bpm.vanilla.configurationFile") != null){
			try{
				Properties p = new Properties();
				p.load(new FileInputStream(System.getProperty("bpm.vanilla.configurationFile")));
				
				if (p.getProperty("bpm.united.olap.runtime.data.cache.disk.fileLocation") != null){
					config =  new CacheDiskConfiguration(p.getProperty("bpm.united.olap.runtime.data.cache.disk.fileLocation"));
					Logger.getLogger(getClass()).info("CacheDisk location = " + config.getFolderCacheLocation().getAbsolutePath());
					
				}
			}catch(Exception ex){
				
			}
		}
		if (config == null){
			Logger.getLogger(getClass()).info("create default configuration...");
			config = new CacheDiskConfiguration("../tmp/uolap/cachedisk");
		}
		
		configure(config);
		
	}
	
	@Override
	public void cacheView(String repositoryId, String directoryItemId, String schemaId, String groupName, String mdxQuery, OlapResult data) {
		CacheKey key = new CacheKey(repositoryId, directoryItemId, md5(schemaId), md5(groupName), md5(mdxQuery));
		
		
		for(Couple couple : cachingQueue){
			if (couple.key.toString().equals(key.toString())){
				return;
			}
		}
		cachingQueue.add(new Couple(key, data, mdxQuery, groupName));

		synchronized (this) {
			if (writer == null || ! writer.isAlive()){
				writer = new ThreadWriter();
				writer.start();
			}
		}
		
		
		
	}

	@Override
	public void configure(ICacheDiskConfiguration config) {
		this.config = config;
		Logger.getLogger(getClass()).info("CacheDisk location = " + config.getFolderCacheLocation().getAbsolutePath());
		index.reloadIndex(config.getFolderCacheLocation());
		
	}
	
	
	@Override
	public void saveIndex(){
		index.persistFileIndex(config.getFolderCacheLocation());
	}
	

	@Override
	public OlapResult getCachedView(String repositoryId, String directoryItemId, String schemaId, String groupId, String mdxQuery) throws Exception{
		
		//FIXME : DON'T FORGET !!!!!!!!!!!!!
		
		String s = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_DISABLE_UOLAP_CACHE_DISK);
		if (s != null){
			try{
				if (Boolean.valueOf(s)){
					return null;
				}
			}catch(Exception ex){}
		}
		CacheKey key = new CacheKey(repositoryId,  directoryItemId, md5(schemaId), md5(groupId), md5(mdxQuery));
		ICacheEntry entry = index.getEntry(key);
		
		if (entry != null){
			entry.refreshAccess();
			return unmarshall(entry);
		}
		
		return null;
	}

	@Override
	public ICacheDiskConfiguration getConfig() {
		return config;
	}
	
	
	
	private ICacheEntry marshall(CacheKey key, OlapResult result, String mdx, String groupName) throws Exception{
		Date start = new Date();
		
		//fileName
		StringBuilder b = new StringBuilder();
		b.append(key.toString());
		b.append(".ors");
		
		File f = new File(getConfig().getFolderCacheLocation(), b.toString());
		marshaller.marshall(f, result);

		
		Date end = new Date();
		Logger.getLogger(getClass()).debug("Marshalled in " + (end.getTime() - start.getTime()));
		 
		CacheEntry entry = new CacheEntry(f, result.getLines().size() * result.getLines().get(0).getCells().size(), mdx, groupName);
		return entry;
		
	}
	
	private OlapResult unmarshall(ICacheEntry entry) throws Exception{
		Date start = new Date();
		OlapResult res = marshaller.unmarshall(entry.getFile());
		

		Date end = new Date();
		Logger.getLogger(getClass()).debug("Unmarshalled in " + (end.getTime() - start.getTime()));
		return res;
		
		
	}
	
	
	
	private static String md5(String value){
		byte[] uniqueKey = value.getBytes();
		byte[] hash = null;

		try {

		// on r�cup�re un objet qui permettra de crypter la chaine

			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);

		}catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");

		}
		StringBuffer hashString = new StringBuffer();

		for (int i = 0; i < hash.length; ++i) {

			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			}
			else {
				hashString.append(hex.substring(hex.length() - 2));
			}

		}

		return hashString.toString();
	}

	@Override
	public IIndexCacheDisk getIndex() {
		return index;
	}

	@Override
	public void purge() {
		Logger.getLogger(getClass()).info("Purging CacheIndex ....");
		synchronized (this) {
			if (writer != null && writer.isAlive()){
				try{
					writer.interrupt();
					Logger.getLogger(getClass()).info("Interupted ThreadWriter");
					writer = null;
				}catch(Exception ex){
					
				}
			}
			cachingQueue.clear();
			Logger.getLogger(getClass()).info("Cleared CachingQueue");
			
			
			for(CacheKey key : index.getKeys()){
				File f = index.getEntry(key).getFile();
				if (f.exists()){
					if (f.delete()){
						Logger.getLogger(getClass()).info("Deleted cache file " + f.getName());
					}
					else{
						Logger.getLogger(getClass()).info("Could not delete cache file " + f.getName());
					}
				}
			}
			index.clear();
			Logger.getLogger(getClass()).info("CacheIndex purged");
			
		}
		
		
		
	}

	@Override
	public void remove(CacheKey key) {
		synchronized (index) {
			ICacheEntry entry = index.getEntry(key);
			if (entry.getFile().exists()){
				entry.getFile().delete();
			}
			index.removeEntry(key);
			Logger.getLogger(getClass()).info(key.toString() + " removed from cacheDisk");
		}
		
	}

	@Override
	public void removeForSchema(String schemaId) {
		String schemaIdMd5 = md5(schemaId);
		for(CacheKey key : getIndex().getKeys()) {
			if(key.getSchemaId().equals(schemaIdMd5)) {
				remove(key);
			}
		}
	}
}
