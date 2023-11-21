package bpm.united.olap.api.cache.impl;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import bpm.united.olap.api.cache.ICacheEntry;

public class CacheEntry implements ICacheEntry, Serializable{
	private File file;
	private Date lastRead = null;
	private EntryState state = EntryState.IDLE;
	private long cellNumber;
	private String mdx;
	private String groupName;
	
	private long size;
	private Date modificationDate;
	
	public CacheEntry(File file, long cellNumber, String mdx, String groupName){
		this.file = file;
		size = this.file.length();
		modificationDate = new Date(file.lastModified());
		this.cellNumber = cellNumber;
		this.mdx = mdx;
		this.groupName = groupName;
	}
	
	/**
	 * @return the mdx
	 */
	public String getMdx() {
		return mdx;
	}

	/**
	 * @param mdx the mdx to set
	 */
	public void setMdx(String mdx) {
		this.mdx = mdx;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public long getEntrySize() {
		if(size > 0) {
			return size;
		}
		return file.length();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public Date getLastAccess() {
		return lastRead;
	}

	@Override
	public Date getModificationDate() {
		if(modificationDate != null) {
			return modificationDate;
		}
		return new Date(file.lastModified());
	}

	@Override
	public EntryState getState() {
		return state;
	}

	@Override
	public boolean isCompressed() {
		return false;
	}

	@Override
	public void refreshAccess() {
		lastRead = new Date();
		
	}

	@Override
	public void setState(EntryState state) {
		this.state = state;
		
	}

	@Override
	public long getCellNumbers() {
		return cellNumber;
	}
	

}
