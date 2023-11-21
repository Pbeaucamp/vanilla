package bpm.mdm.model.storage;

import java.util.HashMap;


public class EntityStorageStatistics {

	private HashMap<Long, Long> rowsByChunk = new HashMap<Long, Long>();
	
	public void setRowsNumberForChunk(long chunkNumber, long rowNumber){
		for(Long l : rowsByChunk.keySet()){
			if (l.longValue() == chunkNumber){
				rowsByChunk.put(chunkNumber, rowNumber);
				return;
			}
		}
		rowsByChunk.put(chunkNumber, rowNumber);
	}
	
	public long getRowsNumberForChunk(long chunkNumber){
		for(Long l : rowsByChunk.keySet()){
			if (l.longValue() == chunkNumber){
				return rowsByChunk.get(chunkNumber);
				
			}
		}
		return 0;
	}
	
	/**
	 * @return the chunkNumber
	 */
	public long getChunkNumber() {
		return rowsByChunk.keySet().size();
	}
	
	
	public long getRowsNumber(){
		long total = 0;
		for(Long key : rowsByChunk.keySet()){
			Long val = rowsByChunk.get(key);
			if (key != null){
				total += val;
			}
		}
		return total;
	}
	
	
	
}
