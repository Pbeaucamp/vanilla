package bpm.gateway.core.transformations.googleanalytics;


public interface IConnection extends ITime {
	
	public String getUsername();
	
	public void setUsername(String username);
	
	public String getPassword();
	
	public void setPassword(String password);
	
	public String getTableId();
	
	public void setTableId(String tableId);
}
