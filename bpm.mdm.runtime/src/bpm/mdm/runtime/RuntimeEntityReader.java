package bpm.mdm.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import bpm.mdm.model.Entity;
import bpm.mdm.model.api.IEntityReader;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.storage.IEntityStorage;

public class RuntimeEntityReader implements IEntityReader{
	private IEntityStorage store;
	
	private List<Row> loadedRows;
	private Iterator<Row> iterator;
	private int chunkNumber = -1;
	private long totalChunkNumber = 0;
	private Entity entity;
	
	public RuntimeEntityReader(IEntityStorage store, Entity entity){
		this.store = store;
		this.entity = entity;
	}


	@Override
	public void close() throws Exception {
		if (loadedRows != null){
			loadedRows.clear();
		}
		loadedRows = null;
		iterator = null;
		chunkNumber = -1;
		
	}


	@Override
	public boolean hasNext() throws Exception {
		if (chunkNumber < 0 ){
			throw new Exception("Has not been opened or has been close");
		}
		if (iterator == null){
			return false;
		}
		boolean b = iterator.hasNext();
		if (!b){
			if (chunkNumber + 1 < totalChunkNumber){
				return false;
			}
			else{
				readNextChunk();
				if (iterator == null){
					return false;
				}
				return iterator.hasNext();
			}
		}
		
		
		return true;
	}


	@Override
	public Row next() throws Exception {
		if (chunkNumber < 0 ){
			throw new Exception("Has not been opened or has been close");
		}
		
		if (hasNext()){
			return iterator.next();
		}
		
		throw new NoSuchElementException("No more element to read");
	}


	@Override
	public void open() throws Exception {
		if (chunkNumber != -1 ){
			throw new Exception("Already opened");
		}
		readNextChunk();
		
	}
	
	private void readNextChunk() throws Exception{
		chunkNumber++;
		totalChunkNumber = store.getStorageStatistics().getChunkNumber();
		if (chunkNumber >= totalChunkNumber){
			loadedRows = null;
			iterator = null;
			return;
		}
		try{
			loadedRows = store.getRows(chunkNumber);
			iterator = loadedRows.iterator();
		}catch(Exception ex){
			throw new Exception("Failed to read entity " + entity.getName() + " chunk " + chunkNumber);
		}
	}
}
