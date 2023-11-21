package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.impl.DateLevel;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.united.olap.runtime.model.improver.DegeneratedHierarchyLevelImprover;
import bpm.united.olap.runtime.model.improver.FactoryLevelImprover;
import bpm.united.olap.runtime.model.improver.DegeneratedHierarchyLevelImprover.IndexDatas;
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class DegeneratedDateHierarchyExtractor extends OneColumnDateHierarchyExtractor {

	private DegeneratedHierarchyLevelImprover improver;

	public DegeneratedDateHierarchyExtractor(Hierarchy h, ICacheServer cacheServer) {
		super(h, cacheServer);
		improver = FactoryLevelImprover.getDegeneratedHierarchyImprover(getOdaInput(null));
		improver.createIndexDatas(h);
	}
	
	protected OdaInput getOdaInput(IRuntimeContext runtimeContext){
		return OdaInputOverrider.override(this.input, runtimeContext);
	}

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext ctx) throws Exception {
		if (lastLevel != null){
			if (lastLevel.get(lvl) != null){
				return lastLevel.get(lvl);
			}
		}
		IQuery queryOda = getOdaQuery(ctx);
		
		queryOda = improver.improveQuery(queryOda, getOdaInput(ctx));
		
		if (cacheServer != null){
			LevelDatasCache actLevelDatas = new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), lvl.getUname(), lvl.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText());
			String cacheKey = CacheKeyGenerator.generateKey(actLevelDatas);
			ICacheable cacheItem = null;
			cacheItem = cacheServer.getCached(cacheKey);
			if(cacheItem != null && cacheItem instanceof LevelDatasCache) {
				actLevelDatas = ((LevelDatasCache)cacheItem);
				lastLevel.put(lvl, actLevelDatas);
				return actLevelDatas;
			}
		}
		
		HashMap<Level,LevelDatasCache> levelDatasCaches = new HashMap<Level,LevelDatasCache>();
		
		
		
		IResultSet rs = null;
		try {
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query " + input.getQueryText(),e);
			throw e;
		}
		
		try {
			Date start = new Date();
			while(rs.next()) {
				
				Level actualLevel = lvl;
				while(actualLevel != null) {
					
					if(levelDatasCaches.get(actualLevel) == null) {
						levelDatasCaches.put(actualLevel, new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), actualLevel.getUname(), actualLevel.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText()));
					}
					
					getLevelData(improver.getIndexDatas(actualLevel), (DateLevel) actualLevel, rs, levelDatasCaches.get(actualLevel));
					
					actualLevel = actualLevel.getSubLevel();
				}
			}
			Date end = new Date();
			System.out.println("fetch resultset in : " + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			rs.close();
			queryOda.close();
			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			Logger.getLogger(getClass()).error("Error while executing the query : " + input.getQueryText(),e);
			throw e;
		}
		
		rs.close();
		queryOda.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		for(Level l : levelDatasCaches.keySet()) {
			if (cacheServer != null){
				boolean b = cacheServer.addToCache(levelDatasCaches.get(l));
				lastLevel.put(l, levelDatasCaches.get(l));	
			}
		}
		
		return levelDatasCaches.get(lvl);
	}
	
	int i = 0;

	private void getLevelData(IndexDatas index, DateLevel lvl, IResultSet rs, LevelDatasCache levelDatasCache) throws Exception {
//		int mbNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getItem());
		int mbNameIndex = index.mbNameIndex;
		int mbParentNameIndex = -1;
		if(lvl.getParentLevel() != null) {
//			mbParentNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getParentLevel().getItem());
			mbParentNameIndex = index.mbParentNameIndex;
		}
		int mbOrderIndex = -1;
		if(lvl.getOrderItem() != null) {
//			mbOrderIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getOrderItem());
			mbOrderIndex = index.mbOrderIndex;
		}
		List<Integer> propertiesIndexes = new ArrayList<Integer>();
		if(lvl.getMemberProperties() != null && lvl.getMemberProperties().size() > 0) {
			for(MemberProperty prop : lvl.getMemberProperties()) {
				propertiesIndexes.add(lvl.getItem().getParent().getItems().indexOf(prop.getValueItem()));
			}
		}
		int foreignKeyIndex = -1;
		for(int i = 0 ; i < lvl.getItem().getParent().getItems().size() ; i++) {
			DataObjectItem item = lvl.getItem().getParent().getItems().get(i);
			if(item.isIsKey()) {
//				foreignKeyIndex = i;
				foreignKeyIndex = index.foreignKeyIndex;
				break;
			}
		}
		
		LevelDatas lvlData = new LevelDatas();
		
		String mbName = findDatePart(lvl, rs, mbNameIndex + 1);
		
		lvlData.addData(LevelDataType.MEMBER_NAME, mbName);
		if(mbParentNameIndex > -1) {
			String parentName = findDatePart((DateLevel) lvl.getParentLevel(), rs, mbParentNameIndex + 1);
			lvlData.addData(LevelDataType.PARENT_NAME, parentName);
		}
		else{
			lvlData.addData(LevelDataType.PARENT_NAME, "All " + lvl.getParentHierarchy().getParentDimension().getName());
		}
		if(mbOrderIndex > -1) {
			String orderName = findDatePart(lvl, rs, mbOrderIndex + 1);
			lvlData.addData(LevelDataType.MEMBER_ORDER, orderName);
		}
		else{
			lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(mbNameIndex + 1));
		}
		List<Object> properties = new ArrayList<Object>();
		for(int propInd : propertiesIndexes) {
			if (propInd > - 1){
				properties.add(rs.getString(propInd + 1));
			}
			
		}
		lvlData.addData(LevelDataType.MEMBER_PROPERTIES, properties);
		if (foreignKeyIndex > - 1){
			lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(foreignKeyIndex + 1));
		}
		else{
			lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(mbNameIndex + 1));
		}
		
		boolean exists = false;
		for(LevelDatas data : levelDatasCache.getLevelDatas()) {
			if(data.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.MEMBER_NAME))
				&& data.getData(LevelDataType.PARENT_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
				exists = true;
				
				data.addData(LevelDataType.FOREIGN_KEY, lvlData.getData(LevelDataType.FOREIGN_KEY));
				levelDatasCache.getForeignKeyIndexMap().put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatasCache.getLevelDatas().indexOf(data));
				
				break;
			}
		}
		
		if(!exists) {
			levelDatasCache.getForeignKeyIndexMap().put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatasCache.getLevelDatas().size());
			levelDatasCache.getLevelDatas().add(lvlData);
		}
	}

	@Override
	public void preloadLevelDatas(int currentLevel, int lastLevel, IRuntimeContext ctx) throws Exception {
		getLevelDatas(hierarchy.getLevels().get(currentLevel), ctx);
	}

	
	
}
