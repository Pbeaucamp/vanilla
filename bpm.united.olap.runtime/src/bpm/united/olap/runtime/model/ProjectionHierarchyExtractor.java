package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.united.olap.runtime.projection.CrossMembersValues;
import bpm.united.olap.runtime.projection.ProjectionSerializationHelper;

public class ProjectionHierarchyExtractor extends AbstractExtractor {
	protected HashMap<Level, LevelDatasCache> lastLevel = new HashMap<Level, LevelDatasCache>();
	
	private Projection projection;
	
	public ProjectionHierarchyExtractor(Hierarchy hierarchy, Projection projection, ICacheServer iCacheServer) {
		this.hierarchy = hierarchy;
		this.projection = projection;
		this.cacheServer = iCacheServer;
	}
	
	@Override
	protected Member createChildMember(Member parent, LevelDatas datas) {
		Member mem = super.createChildMember(parent, datas);
		mem.setProjectionMember(true);
		return mem;
	}
	
	@Override
	public List<Member> getChilds(Member parent, IRuntimeContext runtimeContext) throws Exception {
		if (!parent.hasProjectionMembers()){
			loadChildFromDatas(parent, runtimeContext);
			parent.setHasProjectionMembers(true);
		}
		
		return parent.getSubMembers();
	}
	
	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext runtimeContext) throws Exception {
		if (lastLevel != null){
			if (lastLevel.get(lvl) != null){
				return lastLevel.get(lvl);
			}
		}
		
		Date start = new Date();
		
		//FIXME : a query (for cache), we use the projection key for now
		String projectionQuery = CacheKeyGenerator.generateKey(projection);
		
		LevelDatasCache lvlDatasCache = new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), lvl.getUname(), lvl.getParentHierarchy().getParentDimension().getParentSchema().getId(), projectionQuery);
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
		
		//FIXME : Find the level data
		
		
		HashMap<Object, Integer> foreignKeyIndexMap = new HashMap<Object, Integer>();
		List<LevelDatas> levelDatas = new ArrayList<LevelDatas>();
		
		for(ProjectionMeasure m : projection.getProjectionMeasures()) {
			for(int i = 1 ; ;i++) {
				
				List<CrossMembersValues> values = ProjectionSerializationHelper.deserialize(projection ,i , m.getUname());
				if(values != null) {
					for(CrossMembersValues val : values) {
						LevelDatas lvlData = new LevelDatas();
						
						String mbUname = val.getMemberUnames().get(0);
						
						String[] mbUnameParts = mbUname.split("\\]\\.\\[");
						
						int levelIndex = hierarchy.getLevels().indexOf(lvl);
						
						String mbName = mbUnameParts[levelIndex + 2].replace("[","").replace("]", "");
						
						lvlData.addData(LevelDataType.MEMBER_NAME, mbName);
						
						if(levelIndex == 0) {
							lvlData.addData(LevelDataType.PARENT_NAME, "All " + hierarchy.getParentDimension().getName());
						}
						else {
							lvlData.addData(LevelDataType.PARENT_NAME, mbUnameParts[levelIndex + 1]);
						}
	
						lvlData.addData(LevelDataType.MEMBER_ORDER, mbName);
	
						lvlData.addData(LevelDataType.FOREIGN_KEY, mbUname);
						
						boolean exists = false;
						for(LevelDatas data : levelDatas) {
							if(data.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.MEMBER_NAME))
								&& data.getData(LevelDataType.PARENT_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
								exists = true;
								
								if(!((List)data.getData(LevelDataType.FOREIGN_KEY)).contains(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0))) {
									data.addData(LevelDataType.FOREIGN_KEY, lvlData.getData(LevelDataType.FOREIGN_KEY));
									foreignKeyIndexMap.put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatas.indexOf(data));
								}
								break;
							}
						}
						
						if(!exists) {
							foreignKeyIndexMap.put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatas.size());
							levelDatas.add(lvlData);
						}
					}	
				}
				else {
					break;
				}
			}
		}
		

		
		lvlDatasCache.setLevelDatas(levelDatas);
		lvlDatasCache.setForeignKeyIndexMap(foreignKeyIndexMap);
		
		lvlDatasCache.setParentKey(CacheKeyGenerator.generateKey(lvl.getParentHierarchy().getParentDimension().getParentSchema()));
		
		Date now = new Date();
		Logger.getLogger(getClass()).info("Loaded level " + lvl.getUname()+  " in "  + (now.getTime() - start.getTime()));
		
		
		
		
		if (cacheServer != null){
			boolean b = cacheServer.addToCache(lvlDatasCache);
			lastLevel.put(lvl, lvlDatasCache);
			Logger.getLogger(getClass()).info("Cached Loaded level datas " + lvl.getUname()+  " in "  + (new Date().getTime() - now.getTime()));
		}
		
		
		return lvlDatasCache;
	}

	@Override
	public void clearLevelDatasInMemory() {
		synchronized (lastLevel) {
			lastLevel.clear();
		}
	}

}
