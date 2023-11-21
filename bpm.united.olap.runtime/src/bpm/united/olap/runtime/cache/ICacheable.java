package bpm.united.olap.runtime.cache;

import java.util.Date;


public interface ICacheable {
		
	public String getKey();

	public String getParentKey();
	
	public void setParentKey(String parentKey);
	
	public String getObjectName();
	
	public Date getExpectedEndDate();
	
	public void  updateExpectedEndDate();
	
}
