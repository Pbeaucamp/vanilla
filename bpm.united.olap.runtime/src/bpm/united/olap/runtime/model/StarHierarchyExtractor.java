package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.improver.FactoryLevelImprover;
import bpm.united.olap.runtime.model.improver.LevelImprover;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class StarHierarchyExtractor extends AbstractExtractor {
	
	protected HashMap<Level, LevelDatasCache> lastLevel = new HashMap<Level, LevelDatasCache>();
	
	private boolean isInStreamMode = false;
	private OdaInput input;
	
	private DataObject hierarchyTable; 
	
	public StarHierarchyExtractor(Hierarchy hierarchy, ICacheServer cacheServer) {
		this.hierarchy = hierarchy;
		this.cacheServer = cacheServer;
		this.hierarchyTable = hierarchy.getLevels().get(0).getItem().getParent();
		
		initOdaInput();
	}
	
	private void initOdaInput(){
		
		Datasource datasource = hierarchyTable.getParent();
		input = new OdaInput();
		input.setDatasourcePublicProperties(datasource.getPublicProperties());
		input.setDatasourcePrivateProperties(datasource.getPrivateProperties());
		input.setOdaExtensionDataSourceId(datasource.getDatasourceExtensionId());
		input.setName(datasource.getDatasourceExtensionId());
		input.setQueryText(hierarchyTable.getQueryText());
		
		
	}
	protected OdaInput getOdaInput(IRuntimeContext runtimeContext){
		return OdaInputOverrider.override(this.input, runtimeContext);
	}
	
	
	protected IQuery getOdaQuery(IRuntimeContext runtimeContext) throws Exception {
		OdaInput input = getOdaInput(runtimeContext);
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

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext runtimeContext) throws Exception {
		if (lastLevel != null){
			if (lastLevel.get(lvl) != null){
				return lastLevel.get(lvl);
			}
		}
		
		Date start = new Date();
		
		IQuery queryOda = getOdaQuery(runtimeContext);
		
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
		int mbParentNameIndex = -1;
		if(lvl.getParentLevel() != null) {
			mbParentNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getParentLevel().getItem());
		}
		int mbOrderIndex = -1;
		if(lvl.getOrderItem() != null) {
			mbOrderIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getOrderItem());
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
				foreignKeyIndex = i;
				break;
			}
		}
		
		int labelIndex = -1;
		if(lvl.getLabelItem() != null) {
			labelIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getLabelItem());
		}
		
		// improveQuery
		input = OdaInputOverrider.override(input, runtimeContext);
		LevelImprover improver = FactoryLevelImprover.createLevelImprover(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), input);
		lvlDatasCache = improver.createLevelDatas(lvl, input, queryOda, mbNameIndex, mbParentNameIndex, mbOrderIndex, propertiesIndexes, foreignKeyIndex, labelIndex);
		
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
