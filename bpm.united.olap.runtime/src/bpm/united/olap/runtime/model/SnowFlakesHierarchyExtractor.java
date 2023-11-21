package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class SnowFlakesHierarchyExtractor extends AbstractExtractor {

	protected HashMap<Level, LevelDatasCache> lastLevel = new HashMap<Level, LevelDatasCache>();
	private HashMap<DataObject, SnowFlakesTable> lastTables = new HashMap<DataObject, SnowFlakesTable>();
	
	private boolean isInStreamMode = false;
	
	private PathFinder pathFinder;
	
	private DataObject factTable;
	
	public SnowFlakesHierarchyExtractor(Hierarchy h, ICacheServer cacheServer, PathFinder pathFinder, DataObject factTable) {
		this.hierarchy = h;
		this.cacheServer = cacheServer;
		this.pathFinder = pathFinder;
		this.factTable = factTable;
	}

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext ctx) throws Exception {
		if (lastLevel != null){
			if (lastLevel.get(lvl) != null){
				return lastLevel.get(lvl);
			}
		}
		
		Date start = new Date();
		
		IQuery queryOda = getOdaQuery(lvl, ctx);
		
		
		LevelDatasCache lvlDatasCache = new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), lvl.getUname(), lvl.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText());
		String cacheKey = CacheKeyGenerator.generateKey(lvlDatasCache);
		ICacheable cacheItem = null;
		
		if (cacheServer != null){
			cacheItem = cacheServer.getCached(cacheKey);
			if(cacheItem != null && cacheItem instanceof LevelDatasCache) {
				LevelDatasCache lvDatas = ((LevelDatasCache)cacheItem);
				lastLevel.put(lvl, lvDatas);
				return ((LevelDatasCache)cacheItem);
			}
		}
		
		
		
		//Find member informations indexes
		int mbNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getItem());
		int mbOrderIndex = -1;
		if(lvl.getOrderItem() != null) {
			mbOrderIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getOrderItem());
		}
		int labelIndex = -1;
		if(lvl.getLabelItem() != null) {
			labelIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getLabelItem());
		}
		
		List<Integer> propertiesIndexes = new ArrayList<Integer>();
		if(lvl.getMemberProperties() != null && lvl.getMemberProperties().size() > 0) {
			for(MemberProperty prop : lvl.getMemberProperties()) {
				propertiesIndexes.add(lvl.getItem().getParent().getItems().indexOf(prop.getValueItem()));
			}
		}
		
		List<Relation> factPath = new ArrayList<Relation>(pathFinder.findPath(factTable, lvl.getItem().getParent()));
		Relation factRel = factPath.get(0);
		
		int factFk = -1;
		if(factRel.getLeftItem().getParent() == factTable) {
			factFk = factTable.getItems().indexOf(factRel.getLeftItem());
		}
		else if(factRel.getRightItem().getParent() == factTable) {
			factFk = factTable.getItems().indexOf(factRel.getRightItem());
		}
		Collections.reverse(factPath);
		
		int factParentFk = -1;
		LevelDatasCache parentLevelDatas = null;
		if(lvl.getParentLevel() != null) {
			parentLevelDatas = getLevelDatas(lvl.getParentLevel(), ctx);
			factParentFk = factFk;
		}
		
		
		//create the levelDatas
		HashMap<Object, Integer> foreignKeyIndexMap = new HashMap<Object, Integer>();
		List<LevelDatas> levelDatas = new ArrayList<LevelDatas>();
		
		IResultSet rs = null;
		try {
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query ");// + input.getQueryText(),e);
			throw e;
		}
		
		try {
			while(rs.next()) {
				
				LevelDatas lvlData = new LevelDatas();
				
				lvlData.addData(LevelDataType.MEMBER_NAME, rs.getString(mbNameIndex + 1 ));//.replace(".", "_"));
				if(mbOrderIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(mbOrderIndex + 1));
				}
				else{
					lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(mbNameIndex + 1));
				}
				
				if(labelIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(labelIndex + 1));
				}
				else {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(mbNameIndex + 1));
				}
				
				List<Object> properties = new ArrayList<Object>();
				for(int propInd : propertiesIndexes) {
					if (propInd > - 1){
						properties.add(rs.getString(propInd + 1));
					}
					
				}
				lvlData.addData(LevelDataType.MEMBER_PROPERTIES, properties);
				
				if (factFk > - 1){
					List<String> fk = findForeignKeys(factFk, factPath, lvl.getItem().getParent(), rs, ctx);
					
					lvlData.addData(LevelDataType.FOREIGN_KEY, fk);
				}
				else{
					lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(mbNameIndex + 1));
				}
				
				if(factParentFk > -1) {
					List<String> fks = ((List)lvlData.getData(LevelDataType.FOREIGN_KEY));
					for(String fk : fks) {
						Integer fkIndex = parentLevelDatas.getForeignKeyIndexMap().get(fk);
						if (fkIndex == null){
							continue;
						}
						String parMbName = parentLevelDatas.getLevelDatas().get(fkIndex).getData(LevelDataType.MEMBER_NAME).toString();
						lvlData.addData(LevelDataType.PARENT_NAME, parMbName);
						break;
					}
				}
				else{
					lvlData.addData(LevelDataType.PARENT_NAME, "All " + hierarchy.getParentDimension().getName());
				}
				
				boolean exists = false;
				for(LevelDatas data : levelDatas) {
					if(data.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.MEMBER_NAME))
						&& data.getData(LevelDataType.PARENT_NAME) != null && data.getData(LevelDataType.PARENT_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
						exists = true;
						
						data.addData(LevelDataType.FOREIGN_KEY, lvlData.getData(LevelDataType.FOREIGN_KEY));
						
						for(Object k : ((List)lvlData.getData(LevelDataType.FOREIGN_KEY))) {
							foreignKeyIndexMap.put(k, levelDatas.indexOf(data));
						}
						
						break;
					}
				}
				
				if(!exists) {
					for(Object k : ((List)lvlData.getData(LevelDataType.FOREIGN_KEY))) {
						foreignKeyIndexMap.put(k, levelDatas.size());
					}
					levelDatas.add(lvlData);
				}
			}
		} catch (Exception e) {
			rs.close();
			queryOda.close();
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			Logger.getLogger(getClass()).error("Error while executing the query : ");// + input.getQueryText(),e);
			throw e;
		}
		
		rs.close();
		queryOda.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		lvlDatasCache.setParentKey(CacheKeyGenerator.generateKey(lvl.getParentHierarchy().getParentDimension().getParentSchema()));
		lvlDatasCache.setLevelDatas(levelDatas);
		lvlDatasCache.setForeignKeyIndexMap(foreignKeyIndexMap);
		
		Date now = new Date();
		Logger.getLogger(getClass()).info("Loaded level " + lvl.getUname()+  " in "  + (now.getTime() - start.getTime()));
		
		if (cacheServer != null){
			boolean b = cacheServer.addToCache(lvlDatasCache);
			lastLevel.put(lvl, lvlDatasCache);
			Logger.getLogger(getClass()).info("Cached Loaded level datas " + lvl.getUname()+  " in "  + (new Date().getTime() - now.getTime()));
		}
		
		
		return lvlDatasCache;
	}

	private List<String> findForeignKeys(int factFk, List<Relation> factPath, DataObject destinationTable, IResultSet rs, IRuntimeContext ctx) throws Exception {
		String key = null; 
		if(factPath.get(0).getLeftItem().getParent() == destinationTable) {
			key = rs.getString(destinationTable.getItems().indexOf(factPath.get(0).getLeftItem()) + 1);
		}
		else if(factPath.get(0).getRightItem().getParent() == destinationTable) {
			key = rs.getString(destinationTable.getItems().indexOf(factPath.get(0).getRightItem()) + 1);
		}
		
		DataObject previousTable = destinationTable;
		List<String> previousKeys = new ArrayList<String>();
		previousKeys.add(key);
		for(int i = 0 ; i < factPath.size() - 1 ; i++) {
			Relation rel = factPath.get(i);
			if(rel.getLeftItem().getParent() == previousTable) {
				SnowFlakesTable table = getSnowFlakesTable(rel.getRightItem().getParent(), ctx);
				
				List<String> newKeys = new ArrayList<String>();
				for(String prevKey : previousKeys) {
					Relation nextRel = factPath.get(i + 1);
					
					if(nextRel.getLeftItem().getParent() == rel.getRightItem().getParent()) {
						newKeys.addAll(table.findKeys(rel.getRightItem(), nextRel.getLeftItem(), prevKey));
					}
					else {
						newKeys.addAll(table.findKeys(rel.getRightItem(), nextRel.getRightItem(), prevKey));
					}
				}
				previousKeys = newKeys;
				previousTable = rel.getRightItem().getParent();
			}
			else if(rel.getRightItem().getParent() == previousTable) {
				SnowFlakesTable table = getSnowFlakesTable(rel.getLeftItem().getParent(), ctx);	
				
				List<String> newKeys = new ArrayList<String>();
				for(String prevKey : previousKeys) {
					Relation nextRel = factPath.get(i + 1);
					if(nextRel.getLeftItem().getParent() == rel.getLeftItem().getParent()) {
						newKeys.addAll(table.findKeys(rel.getLeftItem(), nextRel.getLeftItem(), prevKey));
					}
					else {
						newKeys.addAll(table.findKeys(rel.getLeftItem(), nextRel.getRightItem(), prevKey));
					}
				}
				previousKeys = newKeys;
				previousTable = rel.getLeftItem().getParent();
			}
		}
		
		return previousKeys;
	}

	private SnowFlakesTable getSnowFlakesTable(DataObject parent, IRuntimeContext ctx) throws Exception {
		SnowFlakesTable resultTable = new SnowFlakesTable(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), parent.getName(), parent.getParent().getParent().getId());
		if(lastTables.get(parent) != null) {
			return lastTables.get(parent);
		}
		
		if (cacheServer != null){
			
			String cacheKey = CacheKeyGenerator.generateKey(resultTable);
			ICacheable cacheItem = cacheServer.getCached(cacheKey);
			if(cacheItem != null && cacheItem instanceof SnowFlakesTable) {
				resultTable = ((SnowFlakesTable)cacheItem);
				lastTables.put(parent, resultTable);
				return resultTable;
			}
		}
		
		List<DataObjectItem> usedColumns = pathFinder.findUsedColumns(parent);
		
		IQuery queryOda = getOdaQuery(parent, ctx);
		
		IResultSet rs = null;
		try {
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for table : " + parent.getName());
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query ");// + input.getQueryText(),e);
			throw e;
		}
		
		List<List<String>> keys = new ArrayList<List<String>>();
		for(DataObjectItem it : usedColumns) {
			keys.add(new ArrayList<String>());
		}
		
		try {
			while(rs.next()) {
				
				for(int i = 0 ; i < usedColumns.size() ; i++) {
					DataObjectItem it = usedColumns.get(i);
					int index = parent.getItems().indexOf(it) + 1;
					String key = rs.getString(index);
					keys.get(i).add(key);
				}
				
			}
		} catch (Exception e) {
			rs.close();
			queryOda.close();
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			Logger.getLogger(getClass()).error("Error while executing the query : ");// + input.getQueryText(),e);
			throw e;
		}
		
		rs.close();
		queryOda.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		resultTable.setColumns(usedColumns);
		resultTable.setDatas(keys);
		
		if (cacheServer != null){
			boolean b = cacheServer.addToCache(resultTable);
			lastTables.put(parent, resultTable);
			Logger.getLogger(getClass()).info("Cached Loaded table datas " + parent.getName());
		}
		
		return resultTable;
	}

	@Override
	public void clearLevelDatasInMemory() {
		synchronized (lastLevel) {
			lastLevel.clear();
		}
		synchronized (lastTables) {
			lastTables.clear();
		}
	}
	
	private IQuery getOdaQuery(DataObject table, IRuntimeContext ctx) throws Exception {
		
		OdaInput input = initOdaInput(table);
		input = OdaInputOverrider.override(input, ctx);
		
		//For this shitty postgres....
		if(input.getDatasourcePublicProperties().get("odaURL") != null && ((String)input.getDatasourcePublicProperties().get("odaURL")).contains("postgresql")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "true");
		}
		else {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = bpm.dataprovider.odainput.consumer.QueryHelper.buildquery(input);
			
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
			if(isInStreamMode) {
				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
					queryOda.setProperty("rowFetchSize", Integer.MIN_VALUE + "");
				}
				else {
					queryOda.setProperty("rowFetchSize", 1 + "");
				}
			}
			else {
				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
				else {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
			}
		}
			
		return queryOda;
	}
	
	private IQuery getOdaQuery(Level level, IRuntimeContext ctx) throws Exception {
		return getOdaQuery(level.getItem().getParent(), ctx);
	}

	private OdaInput initOdaInput(DataObject table){
		
		Datasource datasource = table.getParent();
		OdaInput input = new OdaInput();
		input.setDatasourcePublicProperties(datasource.getPublicProperties());
		input.setDatasourcePrivateProperties(datasource.getPrivateProperties());
		input.setOdaExtensionDataSourceId(datasource.getDatasourceExtensionId());
		input.setName(datasource.getDatasourceExtensionId());
		input.setQueryText(table.getQueryText());
		
		return input;
	}
	
}
