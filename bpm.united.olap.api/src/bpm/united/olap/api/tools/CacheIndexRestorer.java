package bpm.united.olap.api.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.impl.CacheIndex;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.utils.IOWriter;

public class CacheIndexRestorer {
	
	/**
	 * 
	 * @param stream : must be a ZipInputStream containing cache files(index and tables)
	 * @return
	 * @throws Exception
	 */
	public HashMap<CacheKey, ICacheEntry> restoreFromZip(InputStream stream) throws Exception{
		Logger.getLogger(getClass()).info("Exploring Cache from Stream");
		/*
		 * parse the Stream wich must be a Zip file containing the indexFile and 
		 * its cacheFiles
		 */
		File tempFolder = new File(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.united.olap.runtime.data.cache.disk.fileLocation") + "/uolapCacheTmp");
		if (!tempFolder.exists()){
			tempFolder.mkdirs();
			Logger.getLogger(getClass()).info("Created Folder " + tempFolder.getAbsolutePath());
		}
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;
		
		File zF = new File(tempFolder, "incomingStream_" + sfd.format(new Date()));
		
		try{
			FileOutputStream fos = new FileOutputStream(zF);
			IOWriter.write(stream,fos, true, true);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Could not write the incoming CacheStream - " + ex.getMessage(), ex);
			throw new Exception("Could not write the incoming CacheStream - " + ex.getMessage(), ex);
		}
		
		
		
		ZipFile zip = new ZipFile(zF);
		
		File indexFile = null;
		List<File> files = new ArrayList<File>();
		
		Enumeration<ZipEntry> entries = (Enumeration)zip.entries();
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			try{
				
				File f = new File(tempFolder, entry.getName());
				IOWriter.write(zip.getInputStream(entry), new FileOutputStream(f), true, true);
				
				if (entry.getName().equals("cacheIndex.uolap")){
					indexFile = f;
				}
				else{
					files.add(f);
				}
				Logger.getLogger(getClass()).info("Unzipped " + entry.getName() + " into " + f.getAbsolutePath());
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("Failed to unzip" + entry.getName() + " - " + ex.getMessage(), ex);
			}
		}
		zip.close();
		
		CacheIndex index = new CacheIndex();
		try{
			index.reloadIndex(tempFolder);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to rebuild CacheIndex - " + ex.getMessage(), ex);
			throw new Exception("Failed to rebuild CacheIndex - " + ex.getMessage(), ex);
			
		}
		
		
		for(File f : files){
			f.delete();
		}
		indexFile.delete();
		
		HashMap<CacheKey, ICacheEntry> map = new HashMap<CacheKey, ICacheEntry>();
		for(CacheKey key : index.getKeys()){
			map.put(key, index.getEntry(key));
		}
		return map;
	}



	public HashMap<CacheKey, ICacheEntry> restoreFromFolder(File folder) throws Exception{
		CacheIndex index = new CacheIndex();
		try{
			index.reloadIndex(folder);
		}catch(Exception ex){
			Logger.getLogger(getClass()).error("Failed to rebuild CacheIndex - " + ex.getMessage(), ex);
			throw new Exception("Failed to rebuild CacheIndex - " + ex.getMessage(), ex);
			
		}

		
		HashMap<CacheKey, ICacheEntry> map = new HashMap<CacheKey, ICacheEntry>();
		for(CacheKey key : index.getKeys()){
			map.put(key, index.getEntry(key));
		}
		return map;
	}
}
