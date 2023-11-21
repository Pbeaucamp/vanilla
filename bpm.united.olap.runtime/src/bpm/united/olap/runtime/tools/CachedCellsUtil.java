package bpm.united.olap.runtime.tools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.CachedCell;

/**
 * An helper class to find cells in cache and reuse them instead
 * of make the calcul again
 * @author Marc Lanquetin
 *
 */
public class CachedCellsUtil {
	private static ConcurrentLinkedQueue<CachedCell> cachingQueue = new ConcurrentLinkedQueue<CachedCell>();
	
	private static class QueueThread extends Thread{
		private ICacheServer server;
		
		public QueueThread(ICacheServer server){
			this.server = server;
		}
		public void run(){
			Logger.getLogger(getClass()).debug("Start to empty CacheQueue");
			
			
			while(!cachingQueue.isEmpty()){
				CachedCell cell = cachingQueue.poll();
				if (cell == null){
					return;
				}
				
				server.addToCache(cell);
//				Logger.getLogger(getClass()).debug("Cached cell{" + cell.getKey() + "," + cell.getValue() + "}");
			}
			Logger.getLogger(getClass()).debug("Cache queue empty, thread died");
		}
		
	}
	
	private static QueueThread cacher = null;
	
	
	/**
	 * Look in the cache and find the cached cells
	 * @param cells
	 * @param server
	 * @return An hashmap of cells as 
	 * 		   key : index of the cell, value : the dataCell
	 */
	public static LinkedHashMap<Integer, DataCell> findCachedCells(ICubeInstance cubeInstance, List<DataCell> cells, ICacheServer server, String effectiveQuery) {
		
		
		
		LinkedHashMap<Integer, DataCell> cachedCells = new LinkedHashMap<Integer, DataCell>();
		
		if (server == null){
			return cachedCells;
		}
		List<String> keys = new ArrayList<String>();
		int count = 0;
		for(int i = 0 ; i < cells.size() ; i++) {

			String cellKey = CacheKeyGenerator.generateKey(cubeInstance, cells.get(i).getIdentifier2(), effectiveQuery);//createKeyFromId(cells.get(i).getIdentifier());
			
			keys.add(cellKey);
			
			if (keys.size() % 2000 == 0 || i == cells.size() - 1){
				try{
					Map<String, ICacheable> map = server.getBulkCached(keys);
					for(int k = 0; k < keys.size(); k++){
						ICacheable  c = map.get(keys.get(k));
						if (c != null){
							DataCell cell = cells.get(k + count * 2000);
							if (((CachedCell)c).getValue() != null){
								cell.setResultValue(new Double(((CachedCell)c).getValue()));
							}
							else{
								cell.setResultValue(((CachedCell)c).getValue());
							}
							if (((CachedCell)c).getPersistedValue() != null){
								cell.persistValue(new Double(((CachedCell)c).getPersistedValue()));
							}
							else{
								cell.persistValue(((CachedCell)c).getPersistedValue());
							}
							cachedCells.put(k + count * 2000, cell);
						}
					}
					count++;
				}catch(Exception ex){
					Logger.getLogger(CachedCellsUtil.class).warn("Error when getting cached Cells - " + ex.getMessage(), ex);
				}
				keys.clear();
				
			}
		}
		

		return cachedCells;
	}

	
	/**
	 * Put cells in cache
	 * @param cells
	 * @param server
	 * @param cachedCells 
	 */
	public static void cacheCells(ICubeInstance cubeInstance, List<DataCell> cells, ICacheServer server, LinkedHashMap<Integer, DataCell> cachedCells, String effectiveQueryMd5) {
		int i = 0;
		int count = 0;
		
//		Logger.getLogger(CachedCellsUtil.class).debug("Caching cell for effectoveQuery:" + effectiveQuery);
		for(DataCell cell : cells) {
			String key = CacheKeyGenerator.generateKey(cubeInstance, cell.getIdentifier2(), effectiveQueryMd5);
						
			CachedCell cache = new CachedCell(cell.getCol(), cell.getRow(), cell.getResultValue(), cell.getPersistedValue(), key, CacheKeyGenerator.generateKey(cubeInstance), server.getConfiguration().getCacheExpirationTime());
			if(!cachedCells.containsKey(i)) {
				cachingQueue.add(cache);
				count++;
				
			}
			i++;
		}
		Logger.getLogger(CachedCellsUtil.class).debug("cell cached = " + count + " on " + cells.size());
		
		
		if (count > 0){
			if (cacher == null){
				cacher = new QueueThread(server);
				cacher.start();
			}
			else if (!cacher.isAlive()){
				cacher = new QueueThread(server);
				cacher.start();
			}
		}
	}
	
}
