package bpm.mdm.runtime.serializers.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.IndexRebuildException;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.util.RowUtil;
import bpm.mdm.runtime.serializers.Reader;

public class IndexBuilder {
	private IndexNode originalNode;
	private Reader reader;
	private Entity newEntity;
	
	
	public IndexBuilder(IndexNode originalNode, Reader reader, Entity newEntity) {
		super();
		this.originalNode = originalNode;
		this.reader = reader;
		this.newEntity = newEntity;
	}


	public IndexNode rebuildIndex() throws Exception{
		
		Logger.getLogger(getClass()).info("Rebuilding IndexNode for entity " + newEntity.getName());
		
		String key = originalNode.getKey();
		long chunk = originalNode.getChunkFileNumber();
		
		IndexNode rebuilt = new IndexNode(key, chunk);
		
		List<Row> rows = null;
		boolean hasChunk = true;
		int chunkNum = 0;
		
		
		List<Row> error = new ArrayList<Row>();
		
		while(hasChunk){
			try{
				rows = reader.readStreamRows(chunkNum);
				
				for(Row r : rows){
					String k = RowUtil.generateUUID(newEntity, r);
					try{
						rebuilt.addNode(k, chunkNum);
					}catch(RuntimeException ex){
						Logger.getLogger(getClass()).error("Error when creating node : " + ex.getMessage());
						error.add(r);
					}
				}
				chunkNum++;
			}catch (NonExistingChunkFileException e) {
				hasChunk = false;
			}
			
		}
		
		if (!error.isEmpty()){
			Logger.getLogger(getClass()).info("Rebuilding IndexNode for entity " + newEntity.getName() + " contains errors");
			throw new IndexRebuildException(newEntity, error);
		}
		
		return rebuilt;
		
		
	}
}	
