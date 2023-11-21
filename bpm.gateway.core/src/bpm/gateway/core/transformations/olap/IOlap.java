package bpm.gateway.core.transformations.olap;


public interface IOlap {
	public String getCubeName();
	public void setCubeName(String cubeName);
	public Integer getDirectoryItemId();
	public void setDirectoryItemId(Integer id);
	public String getDirectoryItemName();
	public void setDirectoryItemName(String name);
	
	
}
