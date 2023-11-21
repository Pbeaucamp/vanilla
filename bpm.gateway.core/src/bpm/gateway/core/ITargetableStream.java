package bpm.gateway.core;

/**
 * Interface that should be implemented by transformation that will
 * have a Target Table
 * @author LCA
 *
 */
public interface ITargetableStream extends DataStream {

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
	 * @return tru if the Table will be truncated at runtime before any insertions
	 */
	public boolean isTruncate();
}
