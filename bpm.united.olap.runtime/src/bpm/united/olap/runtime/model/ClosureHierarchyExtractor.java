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
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.model.impl.ClosureHierarchy;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.cache.ICacheable;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.united.olap.runtime.tools.OdaInputOverrider;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class ClosureHierarchyExtractor extends AbstractExtractor {

	private HashMap<Level, LevelDatasCache> lastLevels = new HashMap<Level, LevelDatasCache>();
	private boolean isInStreamMode = false;
	private OdaInput input;
	
	private DataObject hierarchyTable;
	
	public ClosureHierarchyExtractor(ClosureHierarchy hierarchy, ICacheServer cacheServer) {
		this.hierarchy = hierarchy;
		hierarchyTable = hierarchy.getParentItem().getParent();
		this.cacheServer = cacheServer;
		initOdaInput();
	}
	
	@Override
	protected Member createChildMember(Member parent, LevelDatas datas) {
		Level memberLevel = hierarchy.getLevels().get(0);
		Member member = ModelFactory.eINSTANCE.createMember();
		member.setName(datas.getData(LevelDataType.MEMBER_NAME).toString());
		if(datas.getData(LevelDataType.MEMBER_ORDER) != null) {
			member.setOrderValue(datas.getData(LevelDataType.MEMBER_ORDER).toString());
		}
		else{
			member.setOrderValue(datas.getData(LevelDataType.MEMBER_NAME).toString());
		}
		
		if(datas.getData(LevelDataType.MEMBER_LABEL) != null) {
			member.setCaption(datas.getData(LevelDataType.MEMBER_LABEL).toString());
		}
		else {
			member.setCaption(datas.getData(LevelDataType.MEMBER_NAME).toString());
		}
		
		if(memberLevel.getMemberProperties() != null) {
			for(int i = 0 ; i < memberLevel.getMemberProperties().size() ; i++) {
				MemberProperty prop = memberLevel.getMemberProperties().get(i);
				MemberProperty memProp = ModelFactory.eINSTANCE.createMemberProperty();
				memProp.setName(prop.getName());
				memProp.setType(prop.getType());
				memProp.setValueItem(prop.getValueItem());
				memProp.setValue(((List)datas.getData(LevelDataType.MEMBER_PROPERTIES)).get(i).toString());
			}
		}
		
		member.setUname(parent.getUname() + ".[" + member.getName() + "]");
		member.setMemberRelationsUname(member.getUname());
		if(member.getOrderValue() != null) {
			member.setOrderUname(parent.getOrderUname() + ".[" + member.getOrderValue() + "]");
		}
		else {
			member.setOrderUname(parent.getOrderUname() + ".[" + member.getName() + "]");
		}
		
		parent.getSubMembers().add(member);
		member.setParentLevel(memberLevel);
		member.setParentMember(parent);
		member.setParentHierarchy(hierarchy);
		return member;
	}

	@Override
	protected LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext ctx) throws Exception {
		
		if(!(lvl instanceof ClosureLevel)) {
			throw new Exception("Level type unsupported in closure hierarchy");
		}
		ClosureLevel level = (ClosureLevel) lvl;
		
		//look if levelDatas are in memory
		if (lastLevels != null){
			if (lastLevels.get(lvl) != null){
				return lastLevels.get(lvl);
			}
		}
		
		//find levelDatas by executing the query
		IQuery queryOda = getOdaQuery(ctx);
		
		//look if levelDatas are in cache
		Date start = new Date();
		LevelDatasCache lvlDatasCache = new LevelDatasCache(cacheServer.getConfiguration().getMainDatasCacheExpirationTime(), lvl.getUname(), lvl.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText());
		String cacheKey = CacheKeyGenerator.generateKey(lvlDatasCache);
		ICacheable cacheItem = null;
		
		if (cacheServer != null){
			cacheItem = cacheServer.getCached(cacheKey);
			if(cacheItem != null && cacheItem instanceof LevelDatasCache) {
				LevelDatasCache lvDatas = ((LevelDatasCache)cacheItem);
				lastLevels.put(lvl, lvDatas);
				return ((LevelDatasCache)cacheItem);
			}
		}
		
		
		
		//look for indexes
		int parentIndex = level.getParentItem().getParent().getItems().indexOf(level.getParentItem()) + 1;
		int childIndex = level.getChildItem().getParent().getItems().indexOf(level.getChildItem()) + 1;
		int itemIndex = level.getItem().getParent().getItems().indexOf(level.getItem()) + 1;
		int orderIndex = -1;
		if(level.getOrderItem() != null) {
			orderIndex = level.getOrderItem().getParent().getItems().indexOf(level.getOrderItem()) + 1;
		}
		int labelIndex = -1;
		if(lvl.getLabelItem() != null) {
			labelIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getLabelItem()) + 1;
		}
		List<Integer> propertiesIndexes = new ArrayList<Integer>();
		if(lvl.getMemberProperties() != null && lvl.getMemberProperties().size() > 0) {
			for(MemberProperty prop : lvl.getMemberProperties()) {
				propertiesIndexes.add(lvl.getItem().getParent().getItems().indexOf(prop.getValueItem()) + 1);
			}
		}
		int foreignKeyIndex = -1;
		for(int i = 0 ; i < lvl.getItem().getParent().getItems().size() ; i++) {
			DataObjectItem item = lvl.getItem().getParent().getItems().get(i);
			if(item.isIsKey()) {
				foreignKeyIndex = i + 1;
				break;
			}
		}
		
		//execute the query
		IResultSet rs = null;
		try {
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query " + input.getQueryText(),e);
			throw e;
		}
		
		//create levelDatas
		HashMap<Object, Integer> foreignKeyIndexMap = new HashMap<Object, Integer>();
		List<LevelDatas> levelDatas = new ArrayList<LevelDatas>();
		
		try {
			while(rs.next()) {
			
				LevelDatas lvlData = new LevelDatas();
				
				lvlData.addData(LevelDataType.MEMBER_NAME, rs.getString(itemIndex));//.replace(".", "_"));
				if(parentIndex > -1) {
					String parentName = rs.getString(parentIndex);
					if (parentName != null){
						parentName = parentName.replace(".", "_");
					}
					if(parentName == null || parentName.equals("")) {
						lvlData.addData(LevelDataType.PARENT_NAME, "All " + hierarchy.getParentDimension().getName());
					}
					else {
						lvlData.addData(LevelDataType.PARENT_NAME, parentName);
					}
				}
				else{
					lvlData.addData(LevelDataType.PARENT_NAME, "All " + hierarchy.getParentDimension().getName());
				}
				if(childIndex > -1) {
					lvlData.addData(LevelDataType.CHILD_NAME, rs.getString(childIndex));//.replace(".", "_"));
				}
				else{
					lvlData.addData(LevelDataType.CHILD_NAME, rs.getString(itemIndex));//.replace(".", "_"));
				}
				if(orderIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(orderIndex));
				}
				else{
					lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(itemIndex));
				}
				if(labelIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(labelIndex));
				}
				else {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(itemIndex));
				}
				List<Object> properties = new ArrayList<Object>();
				for(int propInd : propertiesIndexes) {
					if (propInd > - 1){
						properties.add(rs.getString(propInd));
					}
					
				}
				lvlData.addData(LevelDataType.MEMBER_PROPERTIES, properties);
				if (foreignKeyIndex > - 1){
					lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(foreignKeyIndex));
				}
				else{
					lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(itemIndex));
				}
				
				//look if already and add the foreignKey
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
			Logger.getLogger(getClass()).error("Error while executing the query : " + input.getQueryText() + "\n" + e.getMessage(),e);
			throw e;
		}
		
		rs.close();
		queryOda.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		//reparse to find foreignKeys for parent members
		for(LevelDatas lvlData : levelDatas) {
			findParentLevelDatas(lvlData, levelDatas, foreignKeyIndexMap);
		}
		
		lvlDatasCache.setParentKey(CacheKeyGenerator.generateKey(lvl.getParentHierarchy().getParentDimension().getParentSchema()));
		lvlDatasCache.setLevelDatas(levelDatas);
		lvlDatasCache.setForeignKeyIndexMap(foreignKeyIndexMap);
		
		Date now = new Date();
		Logger.getLogger(getClass()).info("Loaded level " + lvl.getUname()+  " in "  + (now.getTime() - start.getTime()));
		
		//put levelDatas in cache and memory
		if (cacheServer != null){
			boolean b = cacheServer.addToCache(lvlDatasCache);
			lastLevels.put(lvl, lvlDatasCache);
			Logger.getLogger(getClass()).info("Cached Loaded level datas " + lvl.getUname()+  " in "  + (new Date().getTime() - now.getTime()));
			
		}
		
		return lvlDatasCache;
	}

	private void findParentLevelDatas(LevelDatas lvlData, List<LevelDatas> levelDatas, HashMap<Object, Integer> foreignKeyIndexMap) {
		if(!lvlData.getData(LevelDataType.PARENT_NAME).equals("All " + hierarchy.getParentDimension().getName())) {
			
			for(LevelDatas parent : levelDatas) {
				if(parent.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
					List fks = (List) parent.getData(LevelDataType.FOREIGN_KEY);
					lvlData.addData(LevelDataType.CLOSURE_PARENTKEY, fks);
					break;
				}
			}
		}
	}

	@Override
	public void clearLevelDatasInMemory() {
		synchronized (lastLevels) {
			lastLevels.clear();
		}
	}

	@Override
	protected void loadChildFromDatas(Member parent, IRuntimeContext ctx) throws Exception {
		Level level = hierarchy.getLevels().get(0);
		LevelDatasCache datas = getLevelDatas(level, ctx);
		
		for(LevelDatas data : datas.getLevelDatas()) {
			if (parent.getName().equals(data.getData(LevelDataType.PARENT_NAME))){
				createChildMember(parent, data);
			}
		}
	}

	
	public String[] getMemberPartNames(Object factForeignKey, int levelIndex, IRuntimeContext ctx, boolean useOrder) throws Exception {
		LevelDatasCache levelDatas = getLevelDatas(hierarchy.getLevels().get(0), ctx);

		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeignKey);
		try{
			LevelDatas data = levelDatas.getLevelDatas().get(key);
			LevelDatas parentData = data;
			List<LevelDatas> lstDatas = new ArrayList<LevelDatas>();
			lstDatas.add(parentData);
			while((parentData.getData(LevelDataType.CLOSURE_PARENTKEY) != null && ((List)parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).size() >= 0)) {
				String parentKey = ((List) parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).get(0).toString();
				int keyIndex = levelDatas.getForeignKeyIndexMap().get(parentKey);
				parentData = levelDatas.getLevelDatas().get(keyIndex);
				lstDatas.add(parentData);
			}
			Collections.reverse(lstDatas);
			
			if(levelIndex >= lstDatas.size()) {
				return null;
			}
			
			String[] res = new String[lstDatas.size()];
			for(int i = 0; i < res.length; i++){
				if (useOrder){
					res[i] = lstDatas.get(i).getData(LevelDataType.MEMBER_ORDER).toString();
				}
				else{
					res[i] = lstDatas.get(i).getData(LevelDataType.MEMBER_NAME).toString();
				}
				
			}
			return res;
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}
	@Override
	public String getMemberName(Object factForeignKey, int levelIndex, IRuntimeContext ctx) throws Exception {
		LevelDatasCache levelDatas = getLevelDatas(hierarchy.getLevels().get(0), ctx);

		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeignKey);
		try{
			LevelDatas data = levelDatas.getLevelDatas().get(key);
			LevelDatas parentData = data;
			List<LevelDatas> lstDatas = new ArrayList<LevelDatas>();
			lstDatas.add(parentData);
			while((parentData.getData(LevelDataType.CLOSURE_PARENTKEY) != null && ((List)parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).size() >= 0)) {
				String parentKey = ((List) parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).get(0).toString();
				int keyIndex = levelDatas.getForeignKeyIndexMap().get(parentKey);
				parentData = levelDatas.getLevelDatas().get(keyIndex);
				lstDatas.add(parentData);
			}
			Collections.reverse(lstDatas);
			
			if(levelIndex >= lstDatas.size()) {
				return null;
			}
			
			return lstDatas.get(levelIndex).getData(LevelDataType.MEMBER_NAME).toString();
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}
	
	
	
	

	@Override
	public String getMemberOrderValue(Object factForeignKey, int levelIndex, IRuntimeContext ctx) throws Exception {
		LevelDatasCache levelDatas = getLevelDatas(hierarchy.getLevels().get(0), ctx);

		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeignKey);
		try{
			LevelDatas data = levelDatas.getLevelDatas().get(key);
			LevelDatas parentData = data;
			List<LevelDatas> lstDatas = new ArrayList<LevelDatas>();
			lstDatas.add(parentData);
			while((parentData.getData(LevelDataType.CLOSURE_PARENTKEY) != null && ((List)parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).size() >= 0)) {
				String parentKey = ((List) parentData.getData(LevelDataType.CLOSURE_PARENTKEY)).get(0).toString();
				int keyIndex = levelDatas.getForeignKeyIndexMap().get(parentKey);
				parentData = levelDatas.getLevelDatas().get(keyIndex);
				lstDatas.add(parentData);
			}
			Collections.reverse(lstDatas);
			
			if(levelIndex >= lstDatas.size()) {
				return null;
			}
			
			return lstDatas.get(levelIndex).getData(LevelDataType.MEMBER_ORDER).toString();
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
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
	
	
	private IQuery getOdaQuery(IRuntimeContext ctx) throws Exception {
		OdaInput input = OdaInputOverrider.override(this.input, ctx);
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

	@Override
	public void preloadLevelDatas(int currentLevel, int lastLevel, IRuntimeContext ctx) throws Exception {
		getLevelDatas(hierarchy.getLevels().get(0), ctx);
	}
	
	
}
