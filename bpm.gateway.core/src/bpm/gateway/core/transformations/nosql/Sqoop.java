package bpm.gateway.core.transformations.nosql;


public interface Sqoop {
	
	public String getSqoopUrl();
	public void setSqoopUrl(String sqoopUrl);
	
	public String getHdfsDirectory();
	public void setHdfsDirectory(String hdfsDirectory);
	
	public boolean isImport();
	public void setImport(boolean isImport);
}
