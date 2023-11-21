package bpm.united.olap.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.IUnitedOlapCacheManager;
import bpm.united.olap.api.cache.impl.CacheIndex;
import bpm.united.olap.api.cache.impl.DiskCacheStatistics;
import bpm.united.olap.api.cache.impl.MemoryCacheStatistics;
import bpm.united.olap.runtime.cache.ICacheConfiguration;
import bpm.united.olap.runtime.cache.ICacheDisk;
import bpm.united.olap.runtime.data.cache.MemCachedConfiguation;
import bpm.united.olap.runtime.data.cache.MemCachedServer2;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.utils.IOWriter;

import com.ibm.icu.text.SimpleDateFormat;

public class UnitedOlapCacheService extends MemCachedServer2 implements IUnitedOlapCacheManager{

	public static final String ID = "bpm.united.olap.runtime.memcacheservice";	
	
	
	public UnitedOlapCacheService(){
		
	}
	
	public void desactivate(ComponentContext ctx){
		Logger.getLogger(getClass()).info("Desactvating UnitedOlapCache...");
		
		clearCache();
		Logger.getLogger(getClass()).info("Flushed memory cache");
		
		persistCacheDisk();
		
		Logger.getLogger(getClass()).info("UnitedOlapCache Desactvated");
		
		killServer();
		
	}
	
	public void persistCacheDisk(){
		getCacheDisk().saveIndex();;
	}
	
	@Override
	protected void finalize() throws Throwable {
		getCacheDisk().saveIndex();
		super.finalize();
	}
	
	public void activate(ComponentContext ctx){
		Logger.getLogger(getClass()).info("init cache server");
		
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(System.getProperty("bpm.vanilla.configurationFile")));
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass()).error("configuration file not found");
			e.printStackTrace();
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("configuration file not found");
			e.printStackTrace();
		}
		String useIt = p.getProperty("bpm.memcached.server.useMemcached");
		if(Boolean.parseBoolean(useIt)) {
			try {
				String port = p.getProperty("bpm.memcached.server.port");
				String host = p.getProperty("bpm.memcached.server.host");
				String maxMemory = p.getProperty("bpm.memcached.server.memoryLimit");
				String idleTime = p.getProperty("bpm.memcached.server.idleTime");
				String maxElements = p.getProperty("bpm.memcached.server.maxElements");
				
				ICacheConfiguration config = createCacheConfiguration(host, port, maxMemory, idleTime, maxElements);
				
				Logger.getLogger(getClass()).info("Start the cache server on " + host + " on port " + port);
				Logger.getLogger(getClass()).info("Start the cache server maxMemory " + maxMemory + " idleTime " + idleTime + " maxElements " + maxElements);
				
				configure(config);
				
				Logger.getLogger(getClass()).info("Cache server started");
				
				
			} catch (Exception e) {
				Logger.getLogger(getClass()).info("Unable to start the cache server : " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		else {
			Logger.getLogger(getClass()).warn("No cache server activated");
		}

	}
	
	private ICacheConfiguration createCacheConfiguration(String host, String port, String maxMemory, String idleTime, String maxElements) {
		if(maxMemory == null) {
			maxMemory = "64";
		}
		if(idleTime == null) {
			idleTime = "600";
		}
		if(maxElements == null) {
			maxElements = "100000";
		}
		
		return new MemCachedConfiguation(host, Integer.parseInt(port), Integer.parseInt(idleTime), Integer.parseInt(maxMemory), Integer.parseInt(maxElements)/*, getLogger()*/);
	}

	@Override
	public void clearDiskCache() throws Exception {
		if (getCacheDisk() != null){
			getCacheDisk().purge();
		}
		else{
			Logger.getLogger(getClass()).warn("Asked t clear CacheDisk and it is not active");
		}
	}

	@Override
	public void clearMemoryCache() throws Exception {
		if (getMemcacheClient() != null){
			getMemcacheClient().flushAll();
		}
		else{
			Logger.getLogger(getClass()).warn("Asked t clear CacheMemory and it is not active");
		}
		
	}

	@Override
	public ICacheEntry getDiskCacheEntry(CacheKey key) throws Exception {
		if (getCacheDisk() == null){
			throw new Exception("CacheDisk is nt active");
		}
		
		return getCacheDisk().getIndex().getEntry(key);
	}

	@Override
	public List<CacheKey> getDiskCacheKeys() throws Exception {
		if (getCacheDisk() == null){
			throw new Exception("CacheDisk is nt active");
		}
		
		return getCacheDisk().getIndex().getKeys();
	}

	@Override
	public DiskCacheStatistics getDiskCacheStatistics() throws Exception {
		if (getCacheDisk() != null){
			return new DiskCacheStatistics(getCacheDisk().getIndex().getSize(), getCacheDisk().getIndex().getStorageSize());
		}
		
		return null;
	}

	@Override
	public MemoryCacheStatistics getMemoryCacheStatistics() throws Exception {
		if (getMemcacheClient() != null){
			return new MemoryCacheStatistics(getCachedItemsNumber(), getCacheMaxSize(), getCacheUsedSize(), getHits(), getMissed());
		}
		return null;
	}

	@Override
	public void removeFromCacheDisk(CacheKey key) throws Exception {
		getCacheDisk().remove(key);
		
	}

	@Override
	public void appendToCacheDisk(InputStream stream, boolean overrideExisting)throws Exception {
		ICacheDisk disk = getCacheDisk();
		
		
		ZipInputStream zip = new ZipInputStream(stream);
		ZipEntry entry = null;
		byte[] buf = new byte[2048];
		
		
		List<CacheKey> newKeys = new ArrayList<CacheKey>();
		
		while((entry = zip.getNextEntry()) != null){
			
			File f = new File(disk.getConfig().getFolderCacheLocation(), entry.getName() +".ors");

			FileOutputStream fos = new FileOutputStream(f);
			int sz = -1;
			while( (sz = zip.read(buf, 0, 2048)) > -1){
				fos.write(buf, 0, sz);
			}
			fos.close();

			newKeys.add(new CacheKey(entry.getName()));
		}
		
		persistCacheDisk();
		disk.getIndex().reloadIndex(disk.getConfig().getFolderCacheLocation());

	}

	@Override
	public InputStream loadCacheEntry(CacheKey key) throws Exception {
		ICacheEntry entry = getDiskCacheEntry(key);
		
		if (entry == null){
			throw new Exception("No entry cached for this key");
		}
		
		File f = entry.getFile();
		if (!f.exists()){
			throw new Exception("The file for this key does not exist anymore.");
		}
		
		if (!f.isFile()){
			throw new Exception("The entry match to a folder....");
		}
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		IOWriter.write(fis, bos, true, true);
		
		return new ByteArrayInputStream(bos.toByteArray());
	}

}
