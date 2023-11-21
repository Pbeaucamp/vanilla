package bpm.united.olap.api.cache.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import bpm.united.olap.api.cache.CacheKey;
import bpm.united.olap.api.cache.ICacheEntry;
import bpm.united.olap.api.cache.IIndexCacheDisk;



public class CacheIndex implements IIndexCacheDisk{
	private static class CacheKeyComparator implements Comparator<CacheKey>, Serializable{

		@Override
		public int compare(CacheKey o1, CacheKey o2) {
			return o1.toString().compareTo(o2.toString());
		}
		
	}
	
	
	private TreeMap<CacheKey, ICacheEntry> index;
	private boolean indexLoaded = false;
	public CacheIndex(){
		index = new TreeMap<CacheKey, ICacheEntry>(new CacheKeyComparator());
	}
	
	@Override
	public ICacheEntry getEntry(CacheKey key) {
		ICacheEntry entry =  index.get(key);
		if (entry == null){
			for(CacheKey k : index.keySet()){
				if (k.toString().equals(CacheKey.Unkown + CacheKey.separator + CacheKey.Unkown + CacheKey.separator + key.getSchemaId() + CacheKey.separator + key.getGroupId()  + CacheKey.separator+ key.getQueryId())){
					entry = index.get(k);
					if (entry != null){
						break;
					}
				}
			}
		}
		
		
		return entry;
	}

	@Override
	public long getSize() {
		return index.size();
	}

	@Override
	public long getStorageSize() {
		long sz = 0;
		
		for(CacheKey k : index.keySet()){
			sz += index.get(k).getEntrySize();
		}
		return sz;
	}

	@Override
	public boolean removeEntry(CacheKey key) {
		synchronized (index) {
			return index.remove(key) != null;
		}
		
	}

	@Override
	public boolean saveEntry(CacheKey key, ICacheEntry entry) {
		synchronized (index) {
			return index.put(key, entry) != null;
		}
	}

	@Override
	public List<CacheKey> getKeys() {
		List<CacheKey> l = new ArrayList<CacheKey>(index.keySet());
		return l;
	}

	@Override
	public void clear() {
		Logger.getLogger(getClass()).info("Clearing CacheDiskIndex ....");
		synchronized (index) {
			for(ICacheEntry entry : index.values()){
				if (entry.getFile().exists()){
					entry.getFile().delete();
					Logger.getLogger(getClass()).info("Deleted file " + entry.getFile().getAbsolutePath());
				}
			}
			index.clear();
			Logger.getLogger(getClass()).info("Cleared CacheDiskIndex");
		}
		
		
	}

	@Override
	public void clearOldest(long spaceToFree) {
		synchronized (index) {
			Logger.getLogger(getClass()).info("Freeing space...");
			List<CacheKey> keys = new ArrayList<CacheKey>(index.keySet());
			Collections.sort(keys, new Comparator<CacheKey>() {
				@Override
				public int compare(CacheKey o1, CacheKey o2) {
					Date e1 = index.get(o1).getLastAccess();
					if (e1 == null){
						e1 = index.get(o1).getModificationDate();
					}
					
					Date e2 = index.get(o2).getLastAccess();
					if (e2 == null){
						e2 = index.get(o2).getModificationDate();
					}
					
					if (e1 != null && e2 != null){
						if (e1.after(e2)){
							return -1;
						}
						else{
							return 1;
						}
					}
					else if (e1 == null){
						return -1;
					}
					else if (e2 == null){
						return 1;
					}
					
					return 0;
				}
			});
		
			int lastItem = 0;
			long space = 0;
			
			for(CacheKey k : keys){
				if (space < spaceToFree ){
					lastItem ++;
					space += index.get(k).getEntrySize();
				}
			}
			
			for(int i = 0; i < lastItem; i++){
				removeEntry(keys.get(i));
			}
			Logger.getLogger(getClass()).info(space + " space freed by removing the " + lastItem + " last used cached views");
		}
		
	}

	
	public void persistFileIndex(File folderDestination){
		
		synchronized (this) {
			try{
				File f = new File(folderDestination, "cacheIndex.uolap");
				Logger.getLogger(getClass()).info("persisting CacheIndex in  " + f.getAbsolutePath() +" ...");
				FileOutputStream fos = new FileOutputStream(f);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(index);
				out.close();
				Logger.getLogger(getClass()).info("CacheIndex persisted");
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to persist CacheIndex " + ex.getMessage(), ex);
			}
			
		}
	}
	
	public void reloadIndex(File folderDestination){
//		if (indexLoaded){
//			return;
//		}
		synchronized (this) {
			try{
				File f = new File(folderDestination, "cacheIndex.uolap");
				
				if (f.exists()){
					Logger.getLogger(getClass()).info("Loading CacheIndex from  " + f.getAbsolutePath() +" ...");
					FileInputStream fis = new FileInputStream(f);
					ObjectInputStream out = new ObjectInputStream(fis);
					index = (TreeMap<CacheKey, ICacheEntry>)out.readObject();
					out.close();
					Logger.getLogger(getClass()).info("CacheIndex file Loaded");
					
					
				}
				
				int count = 0;
				int total = 0;
				for(String s : folderDestination.list()){
					if (s.endsWith(".ors")){
						boolean loaded = false;
						for(CacheKey key : index.keySet()){
							if (s.equals(key.toString() + ".ors")){
								loaded = true;
								break;
							}
						}
						
						if (!loaded){
							total ++;
							Logger.getLogger(getClass()).debug("The CacheDisk file " + s + " is not part of the index. It will be loaded within the CacheDisk but only its data will be available.");
							
							try{
								CacheKey key = new CacheKey(s.replace(".ors", ""));
								CacheEntry entry = new CacheEntry(new File(folderDestination, s), -1, "Unknown Mdx", "Unknown GroupName");
								index.put(key, entry);
								count++;
								Logger.getLogger(getClass()).info(s + " loaded");
							}catch(Exception ex){
								Logger.getLogger(getClass()).warn("Unable to manually load the file " + s + " within the CacheDisk index - " + ex.getMessage());
							}
							

						}
						
					}
				}
				
				
				Logger.getLogger(getClass()).info("CacheDisk Index Loaded");
				Logger.getLogger(getClass()).info("CacheDisk Size : " + index.size() );
				Logger.getLogger(getClass()).info("File Not Stored within the persisted index :" + total);
				Logger.getLogger(getClass()).info("File Loaded Manually :" + count);
				Logger.getLogger(getClass()).info("Failed Files Number :" + (total - count));
				
				
			}catch(Throwable ex){
				ex.printStackTrace();
				Logger.getLogger(getClass()).error("Failed to load CacheIndex " + ex.getMessage(), ex);
			}
			
		}
		Logger.getLogger(getClass()).info("CacheIndex loaded");
		indexLoaded = true;
	}

}
