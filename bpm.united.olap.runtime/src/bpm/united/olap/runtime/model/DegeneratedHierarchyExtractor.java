package bpm.united.olap.runtime.model;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.improver.DegeneratedHierarchyLevelImprover;
import bpm.united.olap.runtime.model.improver.FactoryLevelImprover;
import bpm.united.olap.runtime.query.OdaQueryRunner;

public class DegeneratedHierarchyExtractor extends StarHierarchyExtractor {

	private DegeneratedHierarchyLevelImprover improver;
	public DegeneratedHierarchyExtractor(Hierarchy hierarchy, ICacheServer cacheServer) {
		super(hierarchy, cacheServer);
		improver = FactoryLevelImprover.getDegeneratedHierarchyImprover(getOdaInput(null));
		improver.createIndexDatas(hierarchy);
	}

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext runtimeContext) throws Exception {
		
		try{
			if (lastLevel != null){
				if (lastLevel.get(lvl) != null){
					return lastLevel.get(lvl);
				}
			}
			
			
			
			HashMap<Level,LevelDatasCache> levelDatasCaches = new HashMap<Level,LevelDatasCache>();
			
			IQuery queryOda = getOdaQuery(runtimeContext);
			
			queryOda = improver.improveQuery(queryOda, getOdaInput(runtimeContext));
			
			
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
			
			
			IResultSet rs = null;
			try {
				rs = OdaQueryRunner.runQuery(queryOda);
				Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Unable to execute query " + getOdaInput(runtimeContext).getQueryText(),e);
				throw e;
			}
			
			try {
				while(rs.next()) {
					
					Level actualLevel = lvl;
					while(actualLevel != null) {
						
						if(levelDatasCaches.get(actualLevel) == null) {
							levelDatasCaches.put(actualLevel, new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), actualLevel.getUname(), actualLevel.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText()));
						}
						
						improver.getLevelData(improver.getIndexDatas(actualLevel), rs, levelDatasCaches.get(actualLevel));
//						getLevelData(actualLevel, rs, levelDatasCaches.get(actualLevel));
						
						actualLevel = actualLevel.getSubLevel();
					}
				}
			} catch (Exception e) {
				rs.close();
				queryOda.close();
				bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
				bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
				Logger.getLogger(getClass()).error("Error while executing the query : " + getOdaInput(runtimeContext).getQueryText(),e);
				throw e;
			}
			
			rs.close();
			queryOda.close();
//			bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
			bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
			
			for(Level l : levelDatasCaches.keySet()) {
				if (cacheServer != null){

					boolean b = cacheServer.addToCache(levelDatasCaches.get(l));
					lastLevel.put(l, levelDatasCaches.get(l));
				}
			}
			
			return levelDatasCaches.get(lvl);
		}catch(Exception ex){
			throw new Exception("Error hen getting LevelData for "  + lvl.getParentHierarchy().getUname() + "." + lvl.getUname() +" - " + ex.getMessage(), ex);
		}
		
	}

	@Override
	public void preloadLevelDatas(int currentLevel, int lastLevel, IRuntimeContext ctx) throws Exception {
		getLevelDatas(hierarchy.getLevels().get(currentLevel), ctx);
	}
	
}
