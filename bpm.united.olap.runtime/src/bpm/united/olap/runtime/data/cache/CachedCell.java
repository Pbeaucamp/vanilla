package bpm.united.olap.runtime.data.cache;

import java.io.Serializable;
import java.util.Date;

import bpm.united.olap.runtime.cache.ICacheable;



public class CachedCell implements Serializable, ICacheable{
	private int col, row;
	private Double value;
	private String cellKey;
	private String parentKey;
	private Date deathTime;
	private long timeout;
	private Double persistedValue;
	
	public CachedCell(int col, int row, Double value, Double persistedValue, String cellKey, String parentkey, long timeOut){
		this.cellKey = cellKey;
		this.col = col;
		this.row = row;
		this.parentKey = parentkey;
		this.value = value;
		this.timeout = timeOut;
		this.deathTime = new Date(new Date().getTime() + timeOut);
		this.persistedValue = persistedValue;
	}
	
	@Override
	public String getKey() {
		return cellKey;
	}

	@Override
	public String getObjectName() {
		return col + ", " + row + " = " + value;
	}

	@Override
	public String getParentKey() {
		return parentKey;
	}

	@Override
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
		
	}

	public Double getValue() {
		return value;
	}

	@Override
	public Date getExpectedEndDate() {
		return this.deathTime;
	}

	@Override
	public void updateExpectedEndDate() {
		this.deathTime = new Date(new Date().getTime() + timeout);
		
	}

	public Double getPersistedValue() {
		return persistedValue;
	}

}
