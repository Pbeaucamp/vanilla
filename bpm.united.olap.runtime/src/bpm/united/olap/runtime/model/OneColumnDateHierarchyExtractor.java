package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
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
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.united.olap.runtime.tools.DimensionUtils;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;

import com.ibm.icu.text.SimpleDateFormat;

public class OneColumnDateHierarchyExtractor extends AbstractExtractor {

	protected HashMap<Level, LevelDatasCache> lastLevel = new HashMap<Level, LevelDatasCache>();
	
	private boolean isInStreamMode = false;
	protected OdaInput input;
	
	private DataObject hierarchyTable; 
	
	public OneColumnDateHierarchyExtractor(Hierarchy h, ICacheServer cacheServer) {
		this.hierarchy = h;
		this.cacheServer = cacheServer;
		this.hierarchyTable = hierarchy.getLevels().get(0).getItem().getParent();
		
		initOdaInput();
	}

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext ctx) throws Exception {
		DateLevel dateLevel = (DateLevel) lvl;
		if (lastLevel != null){
			if (lastLevel.get(lvl) != null){
				return lastLevel.get(lvl);
			}
		}
		
		
		
		Date start = new Date();
		
		IQuery queryOda = getOdaQuery(ctx);
		
		
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
		
		//create the levelDatas
		HashMap<Object, Integer> foreignKeyIndexMap = new HashMap<Object, Integer>();
		List<LevelDatas> levelDatas = new ArrayList<LevelDatas>();
		
		IResultSet rs = null;
		try {
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query " + input.getQueryText(),e);
			throw e;
		}
		
		try {
			while(rs.next()) {
				
				LevelDatas lvlData = new LevelDatas();
				
				String mbName = findDatePart(dateLevel, rs, mbNameIndex + 1);
				lvlData.addData(LevelDataType.MEMBER_NAME, mbName);
				if(mbParentNameIndex > -1) {
					String parentName = findDatePart((DateLevel) dateLevel.getParentLevel(), rs, mbParentNameIndex + 1);
					lvlData.addData(LevelDataType.PARENT_NAME, parentName);
				}
				else{
					lvlData.addData(LevelDataType.PARENT_NAME, "All " + hierarchy.getParentDimension().getName());
				}
				if(mbOrderIndex > -1) {
					String orderName = findDatePart(dateLevel, rs, mbOrderIndex + 1);
					lvlData.addData(LevelDataType.MEMBER_ORDER, orderName);
				}
				else{
					lvlData.addData(LevelDataType.MEMBER_ORDER, mbName);
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
				for(LevelDatas data : levelDatas) {
					if(data.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.MEMBER_NAME))
						&& data.getData(LevelDataType.PARENT_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
						exists = true;
						
						data.addData(LevelDataType.FOREIGN_KEY, lvlData.getData(LevelDataType.FOREIGN_KEY));
						foreignKeyIndexMap.put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatas.indexOf(data));
						
						break;
					}
				}
				
				if(!exists) {
					foreignKeyIndexMap.put(((List)lvlData.getData(LevelDataType.FOREIGN_KEY)).get(0), levelDatas.size());
					levelDatas.add(lvlData);
				}
			}
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

	protected String findDatePart(DateLevel level, IResultSet rs, int i) throws Exception {
		
		Date dateItem = null;
		if(level.getDateType().equals("Date")) {
			try {
				dateItem = rs.getDate(i);
			} catch (Exception e) {
				dateItem = new GregorianCalendar(0, 0, 1).getTime();
			}
		}
		else if(level.getDateType().equals("Long")) {
			String s = rs.getString(i);
			Long l = null;
			try {
				l = Long.parseLong(s);
			} catch (Exception e) {
				l = new GregorianCalendar(0, 0, 1).getTime().getTime();
			}
			dateItem = new Date(l);
		}
		else if(level.getDateType().equals("String")) {
			String s = rs.getString(i);
			SimpleDateFormat form = new SimpleDateFormat(level.getDatePattern());
			try {
				dateItem = form.parse(s);
			} catch (Exception e) {
				dateItem = new GregorianCalendar(0, 0, 1).getTime();
			}
		}
		else {
			throw new Exception("Unsupported date type : " + level.getDateType());
		}
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(dateItem);
		return DimensionUtils.findDatePart(level.getDatePart(), calendar);
	}

	@Override
	public void clearLevelDatasInMemory() {
		synchronized (lastLevel) {
			lastLevel.clear();
		}
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
	
	
	protected IQuery getOdaQuery(IRuntimeContext runtimeContext) throws Exception {
		OdaInput input = OdaInputOverrider.override(this.input, runtimeContext);
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

}
