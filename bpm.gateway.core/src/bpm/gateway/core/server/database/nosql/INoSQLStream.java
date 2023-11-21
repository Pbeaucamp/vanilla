package bpm.gateway.core.server.database.nosql;

import bpm.gateway.core.DataStream;

/**
 * Interface that should be implemented by transformation that will
 * have a Target Table
 * @author LCA
 *
 */
public interface INoSQLStream extends DataStream {

	/**
	 * 
	 * @return the Table Name
	 */
	public String getTableName();
	
	
	/**
	 * define if the target should be truncated first
	 * @param value
	 */
	public void setTruncate(boolean value);
	
	
	/**
	 * 
	 * @return true if the Table will be truncated at runtime before any insertions
	 */
	public boolean isTruncate();
	
	public String getColumnFamily();
	
	public void setColumnFamily(String columnFamily);
}
