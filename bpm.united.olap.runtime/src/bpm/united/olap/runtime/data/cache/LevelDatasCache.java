package bpm.united.olap.runtime.data.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.model.LevelDatas;

public class LevelDatasCache implements ICacheable , Serializable{

	private List<LevelDatas> levelDatas;
	private HashMap<Object, Integer> foreignKeyIndexMap;
	private String levelUname;
	private String schemaId;
	private String parentKey;
	private long timeout;
	private Date deathTime;
	private String effectiveQuery;
	
	/**
	 * 
	 * @param timeout
	 * @param levelUname
	 * @param schemaId
	 * @param effectiveQuery : the real query used to gather the Datas(needed for security context to be able to differentiate the datas coming from different queries)
	 */
	public LevelDatasCache(long timeout, String levelUname, String schemaId, String effectiveQuery) {
		this.levelUname = levelUname;
		this.schemaId = schemaId;
		deathTime = new Date(new Date().getTime() + timeout);
		this.timeout = timeout;
		this.effectiveQuery = effectiveQuery;
	}
	
//	public LevelDatasCache(long timeout, String levelUname, String schemaId, String parentKey) {
//		this.levelUname = levelUname;
//		this.schemaId = schemaId;
//		this.parentKey = parentKey;
//		deathTime = new Date(new Date().getTime() + timeout);
//		this.timeout = timeout;
//	}
	
	@Override
	public String getKey() {
		return CacheKeyGenerator.generateKey(this);
	}

	@Override
	public String getObjectName() {
		return levelUname;
	}

	@Override
	public String getParentKey() {
		return parentKey;
	}

	@Override
	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}
	
	public void setLevelDatas(List<LevelDatas> leveldatas) {
		this.levelDatas = leveldatas;
	}
	
	public void addLevelDatas(LevelDatas levelDatas) {
		if(this.levelDatas == null) {
			this.levelDatas = new ArrayList<LevelDatas>();
		}
		this.levelDatas.add(levelDatas);
	}
	
	public List<LevelDatas> getLevelDatas() {
		if(this.levelDatas == null) {
			this.levelDatas = new ArrayList<LevelDatas>();
		}
		return this.levelDatas;
	}

	public void setLevelUname(String levelUname) {
		this.levelUname = levelUname;
	}

	public String getLevelUname() {
		return levelUname;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setForeignKeyIndexMap(HashMap<Object, Integer> foreignKeyIndexMap) {
		this.foreignKeyIndexMap = foreignKeyIndexMap;
	}

	public HashMap<Object, Integer> getForeignKeyIndexMap() {
		if(foreignKeyIndexMap == null) {
			foreignKeyIndexMap = new HashMap<Object, Integer>();
		}
		return foreignKeyIndexMap;
	}

	@Override
	public Date getExpectedEndDate() {
		return deathTime;
	}

	@Override
	public void updateExpectedEndDate() {
		deathTime = new Date(new Date().getTime() + timeout);
		
	}
	
	public String getEffectiveQuery(){
		return effectiveQuery;
	}
}
