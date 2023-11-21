package bpm.united.olap.api.cache;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public interface ICacheEntry {
	public static enum EntryState implements Serializable{
		READING, WRITING, IDLE;
	}
	
	public String getMdx();
	public String getGroupName();
	
	
	public long getCellNumbers();	
	public long getEntrySize();
	public Date getLastAccess();
	public Date getModificationDate();
	public EntryState getState();
	public File getFile();
	public boolean isCompressed();
	
	public void setState(EntryState state);
	public void refreshAccess();
}
