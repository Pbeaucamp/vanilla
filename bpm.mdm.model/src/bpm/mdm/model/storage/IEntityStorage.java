package bpm.mdm.model.storage;

import java.util.List;

import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;

public interface IEntityStorage {

	public void createRow(Row row) throws Exception;
	public void deleteRow(Row row) throws Exception;
	public void updateRow(Row row) throws Exception ;
	public Row lookup(Row row) throws Exception;
	public void flush() throws Exception ;
	public void cancel() throws Exception ;
	public List<Row> getRows(int chunkNumber) throws NonExistingChunkFileException, Exception ;
	
	/**
	 * 
	 * @return the rows that has invalid datas(rule not respected, 
	 * dataType invalid, primaryKey duplicated, nullValues,...)
	 * @throws Exception
	 */
	public List<Row> getInvalidRows() throws Exception ;
	public EntityStorageStatistics getStorageStatistics() throws Exception;
	/**
	 * 
	 * @param row
	 * @return the operationType registered for the row
	 * the row should have been used with createRow,deleteRow or updateRow
	 * otherwise null is returned
	 */
	public OperationType getType(Row row);
	
	/**
	 * remove the row from whatever operation will be perfomed
	 * when flushing the store
	 * @param row
	 */
	public void cancel(Row row);
}
