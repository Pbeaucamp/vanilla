package bpm.united.olap.runtime.model.improver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.united.olap.api.model.Level;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.united.olap.runtime.query.OdaQueryRunner;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public abstract class LevelImprover {
	
	private long cacheDataTimeout;
	public LevelImprover(long cacheDataTimeout){
		this.cacheDataTimeout = cacheDataTimeout;
	}
	
	protected IQuery getOdaQuery(OdaInput input) throws Exception {
		
		if(input.getDatasourcePublicProperties().get("odaURL") != null && ((String)input.getDatasourcePublicProperties().get("odaURL")).contains("postgresql")) {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "true");
		}
		else {
			input.getDatasourcePublicProperties().put("odaAutoCommit", "false");
		}
		
		IQuery queryOda = bpm.dataprovider.odainput.consumer.QueryHelper.buildquery(input);
			
		if(input.getOdaExtensionDataSourceId().equals("org.eclipse.birt.report.data.oda.jdbc")) {
//			if(isInStreamMode) {
//				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
//					queryOda.setProperty("rowFetchSize", Integer.MIN_VALUE + "");
//				}
//				else {
//					queryOda.setProperty("rowFetchSize", 1 + "");
//				}
//			}
//			else {
				if(((String)input.getDatasourcePublicProperties().get("odaURL")).contains("mysql")) {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
				else {
					queryOda.setProperty("rowFetchSize", 10000 + "");
				}
//			}

		}
			
		return queryOda;
	}

	public LevelDatasCache createLevelDatas(Level lvl, OdaInput odaInput, IQuery queryOda,  int mbNameIndex,  int mbParentNameIndex,  int mbOrderIndex, List<Integer> propertiesIndexes, int foreignKeyIndex, int labelIndex) throws Exception{
		
		
		HashMap<Object, Integer> foreignKeyIndexMap = new HashMap<Object, Integer>();
		List<LevelDatas> levelDatas = new ArrayList<LevelDatas>();
		
		
		List backup = new ArrayList();
		
		try{
			Logger.getLogger(getClass()).info("Trying to improve OdaQuery to load Level " + lvl.getParentHierarchy().getParentDimension().getUname() + "." + lvl.getUname());
			List<Object> l = improve(lvl, queryOda, odaInput, mbNameIndex, mbParentNameIndex, mbOrderIndex, propertiesIndexes, foreignKeyIndex, labelIndex);
			if (l != null){
				Logger.getLogger(getClass()).info("OdaQuery improved to load Level " + lvl.getParentHierarchy().getParentDimension().getUname() + "." + lvl.getUname());
				/*
				 * release the old IQuery
				 */
				
				queryOda.close();
				bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
				bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
				
				/*
				 * backup the object before change occurs
				 */
				backup.add(queryOda);
				backup.add(mbNameIndex);
				backup.add(mbParentNameIndex);
				backup.add(mbOrderIndex);
				backup.add(propertiesIndexes);
				backup.add(foreignKeyIndex);
				backup.add(labelIndex);
				
				queryOda = (IQuery) l.get(0);
				mbNameIndex = (Integer) l.get(1);
				mbParentNameIndex = (Integer)l.get(2); 
				mbOrderIndex = (Integer)l.get(3); 
				propertiesIndexes = (List<Integer>)l.get(4); 
				foreignKeyIndex = (Integer)l.get(5); 
				labelIndex = (Integer)l.get(6); 
				
//				backup.clear();
//				backup = null;
			}
			else{
				Logger.getLogger(getClass()).info("Could not improve OdaQuery for Level " + lvl.getParentHierarchy().getParentDimension().getUname() + "." + lvl.getUname());
			}
		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Error when improving OdaQuery for Level " + lvl.getParentHierarchy().getParentDimension().getUname() + "." + lvl.getUname() + " - " + ex.getMessage(), ex);
			
			/*
			 * restore the backup values
			 */
			queryOda = (IQuery) backup.get(0);
			mbNameIndex = (Integer) backup.get(1);
			mbParentNameIndex = (Integer)backup.get(2); 
			mbOrderIndex = (Integer)backup.get(3); 
			propertiesIndexes = (List<Integer>)backup.get(4); 
			foreignKeyIndex = (Integer)backup.get(5); 
			labelIndex = (Integer)backup.get(6); 
		}
		
	
		
		
		LevelDatasCache lvlDatasCache = new LevelDatasCache(cacheDataTimeout, lvl.getUname(), lvl.getParentHierarchy().getParentDimension().getParentSchema().getId(), queryOda.getEffectiveQueryText());
		
		
		IResultSet rs = null;
		try {
			Logger.getLogger(getClass()).debug("Running ODA query " + queryOda.getEffectiveQueryText());
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
		} catch (Exception e) {
			Logger.getLogger(getClass()).error("Unable to execute query " + queryOda.getEffectiveQueryText(),e);
			
			Logger.getLogger(getClass()).warn("Error when improving OdaQuery for Level " + lvl.getParentHierarchy().getParentDimension().getUname() + "." + lvl.getUname() + " - " + e.getMessage(), e);
			
			/*
			 * restore the backup values
			 */
			queryOda = (IQuery) backup.get(0);
			mbNameIndex = (Integer) backup.get(1);
			mbParentNameIndex = (Integer)backup.get(2); 
			mbOrderIndex = (Integer)backup.get(3); 
			propertiesIndexes = (List<Integer>)backup.get(4); 
			foreignKeyIndex = (Integer)backup.get(5); 
			labelIndex = (Integer)backup.get(6); 
			
			Logger.getLogger(getClass()).debug("Running ODA query " + queryOda.getEffectiveQueryText());
			rs = OdaQueryRunner.runQuery(queryOda);
			Logger.getLogger(getClass()).info("Run ODA Query for " + lvl.getUname() + " in Dimension " + lvl.getParentHierarchy().getParentDimension().getUname() );
			
//			throw e;
		}
		
		try {
			while(rs.next()) {
				
				LevelDatas lvlData = new LevelDatas();
				
				lvlData.addData(LevelDataType.MEMBER_NAME, rs.getString(mbNameIndex + 1 ));//.replace(".", "_"));
				if(mbParentNameIndex > -1) {
					lvlData.addData(LevelDataType.PARENT_NAME, rs.getString(mbParentNameIndex + 1));//.replace(".", "_"));
				}
				else{
					lvlData.addData(LevelDataType.PARENT_NAME, "All " + lvl.getParentHierarchy().getParentDimension().getName());
				}
				if(mbOrderIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(mbOrderIndex + 1));
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
				
				if(labelIndex > -1) {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(labelIndex + 1));
				}
				else {
					lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(mbNameIndex + 1));
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
			Logger.getLogger(getClass()).error("Error while executing the query : " + queryOda.getEffectiveQueryText(),e);
			throw e;
		}
		
		rs.close();
		queryOda.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(queryOda);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(queryOda);
		
		lvlDatasCache.setLevelDatas(levelDatas);
		lvlDatasCache.setForeignKeyIndexMap(foreignKeyIndexMap);
		
		return lvlDatasCache;
	}
	
	abstract protected List<Object> improve(Level lvl, IQuery query, OdaInput odaInput,  int mbNameIndex,  int mbParentNameIndex,  int mbOrderIndex, List<Integer> propertiesIndexes, int foreignKeyIndex, int labelIndex) throws Exception;
}
