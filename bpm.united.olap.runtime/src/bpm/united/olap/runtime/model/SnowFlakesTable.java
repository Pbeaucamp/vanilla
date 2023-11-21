package bpm.united.olap.runtime.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;

public class SnowFlakesTable implements ICacheable, Serializable {

	private List<DataObjectItem> columns;
	private List<List<String>> datas;
	private String tableName;
	private String schemaId;
	private String parentKey;
	private Date deathTime;
	private long expiration;
	
	public SnowFlakesTable(long expiration, String tableName, String schemaId) {
		this.schemaId = schemaId;
		this.tableName = tableName;
		this.expiration = expiration;
		deathTime = new Date(new Date().getTime() + expiration);
	}

	@Override
	public String getKey() {
		return CacheKeyGenerator.generateKey(this);
	}

	@Override
	public String getObjectName() {
		return schemaId + ":snowflakes:" + tableName;
	}

	@Override
	public String getParentKey() {
		return parentKey;
	}

	@Override
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public List<DataObjectItem> getColumns() {
		return columns;
	}

	public void setColumns(List<DataObjectItem> columns) {
		this.columns = columns;
	}

	public List<List<String>> getDatas() {
		return datas;
	}

	public void setDatas(List<List<String>> datas) {
		this.datas = datas;
	}

	public List<String> findKeys(DataObjectItem originItem, DataObjectItem destinationItem, String key) {
		
		int index = -1;
		for(int i = 0 ; i < columns.size() ; i++) {
			if(columns.get(i).getId().equals(originItem.getId())) {
				index = i;
				break;
			}
		}
		int dataIndex = datas.get(index).indexOf(key);
		
		List<Integer> dataIndexes = new ArrayList<Integer>();
		List<String> results = new ArrayList<String>();
		if (dataIndex == -1){
			return results;
		}
		
		for(int i = dataIndex ; i < datas.get(index).size() ; i++) {
			if(datas.get(index).get(i) != null && datas.get(index).get(i).equals(key)) {
				dataIndexes.add(i);
			}
		}
		
		
		int destIndex = -1;
		for(int i = 0 ; i < columns.size() ; i++) {
			if(columns.get(i).getId().equals(destinationItem.getId())) {
				destIndex = i;
				break;
			}
		}
		
		for(int i : dataIndexes) {
			if(!results.contains(datas.get(destIndex).get(i))) {
				results.add(datas.get(destIndex).get(i));
			}
		}
		
		return results;
	}

	@Override
	public Date getExpectedEndDate() {
		return deathTime;
	}

	@Override
	public void updateExpectedEndDate() {
		deathTime = new Date(new Date().getTime() + expiration);
	}

}
