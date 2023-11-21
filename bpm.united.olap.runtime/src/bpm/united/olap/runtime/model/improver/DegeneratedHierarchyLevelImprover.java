package bpm.united.olap.runtime.model.improver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.tools.AlphanumComparator;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public abstract class DegeneratedHierarchyLevelImprover {
	
	private static AlphanumComparator comparator = new AlphanumComparator();

	public static class IndexDatas{
		public int mbNameIndex ;
		public int mbParentNameIndex;
		public int mbOrderIndex;
		public List<Integer> propertiesIndexes;
		public int foreignKeyIndex;
		public int labelIndex;
		
		public IndexDatas clone(){
			IndexDatas d = new IndexDatas();
			d.foreignKeyIndex = foreignKeyIndex;
			d.labelIndex = labelIndex;
			d.mbNameIndex = mbNameIndex;
			d.mbOrderIndex = mbOrderIndex;
			d.mbParentNameIndex = mbParentNameIndex;
			d.propertiesIndexes = new ArrayList<Integer>(propertiesIndexes);
			return d;
		}
		
		/**
		 * for all stored Index, if the index= Point.x, it is replaced by P.y
		 * 
		 * This method is complete bullshit
		 * I know it's not the best solutions but I add the boolean to avoid "double" replacement
		 * 
		 * @param usedIndex
		 */
		public void update(List<Point> usedIndex) {
			List<Integer> props = new ArrayList<Integer>();
			
			boolean nameChanged = false;
			boolean parentNameChanged = false;
			boolean foreignKeyChanged = false;
			boolean orderChanged = false;
			boolean labelChanged = false;
			for(Point p : usedIndex){
				if (p.x == mbNameIndex && !nameChanged){
					mbNameIndex = p.y;
					nameChanged = true;
				}
				
				if (p.x == mbParentNameIndex && !parentNameChanged){
					mbParentNameIndex= p.y;
					parentNameChanged = true;
				}
				
				
				if (p.x == foreignKeyIndex && !foreignKeyChanged){
					foreignKeyIndex = p.y;
					foreignKeyChanged = true;
				}
				
				if (p.x == mbOrderIndex && !orderChanged){
					mbOrderIndex = p.y;
					orderChanged = true;
				}
				
				for(Integer i : propertiesIndexes){
					if (i == p.x){
						props.add(p.y);
					}
				}
				
				if (p.x == labelIndex && !labelChanged){
					labelIndex = p.y;
					labelChanged = true;
				}
			}
			propertiesIndexes = props;
			
			
			
			
			
		}
	}
	
	private Dimension dimension;
	
	protected LinkedHashMap<Level, IndexDatas> levelIndex;
	protected LinkedHashMap<Level, IndexDatas> originalIndex;
	
	/**
	 * implementation have to close given query and its connections if a new IQuery is created
	 * @param query
	 * @param input
	 * @return
	 * @throws Exception
	 */
	abstract public IQuery improveQuery(IQuery query, OdaInput input) throws Exception;
	
	public IndexDatas getIndexDatas(Level l ){
		return levelIndex.get(l);
	}
	
	public void createIndexDatas(Hierarchy hiera){
		this.dimension = hiera.getParentDimension();
		levelIndex = new LinkedHashMap<Level, IndexDatas>();
		originalIndex = new LinkedHashMap<Level, IndexDatas>();
		
		for(Level lvl : hiera.getLevels()){
			
			IndexDatas d = new IndexDatas();
			d.mbNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getItem());
			d.mbParentNameIndex = -1;
			if(lvl.getParentLevel() != null) {
				d.mbParentNameIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getParentLevel().getItem());
			}
			d.mbOrderIndex = -1;
			if(lvl.getOrderItem() != null) {
				d.mbOrderIndex = lvl.getItem().getParent().getItems().indexOf(lvl.getOrderItem());
			}
			List<Integer> propertiesIndexes = new ArrayList<Integer>();
			if(lvl.getMemberProperties() != null && lvl.getMemberProperties().size() > 0) {
				for(MemberProperty prop : lvl.getMemberProperties()) {
					propertiesIndexes.add(lvl.getItem().getParent().getItems().indexOf(prop.getValueItem()));
				}
			}
			d.propertiesIndexes = propertiesIndexes;
			d.foreignKeyIndex = -1;
			for(int i = 0 ; i < lvl.getItem().getParent().getItems().size() ; i++) {
				DataObjectItem item = lvl.getItem().getParent().getItems().get(i);
				if(item.isIsKey()) {
					d.foreignKeyIndex = i;
					break;
				}
			}
			d.labelIndex = -1;
			if(lvl.getLabelItem() != null) {
				d.labelIndex= lvl.getItem().getParent().getItems().indexOf(lvl.getLabelItem());
			}
			
			
			levelIndex.put(lvl, d);
			originalIndex.put(lvl, d.clone());
		}
		
		
	}
	
	protected List<Point> getUsedIndex(){
		
		List<Point> res = new ArrayList<Point>();
		
		for(Level l : originalIndex.keySet()){
			
			IndexDatas d = originalIndex.get(l);
			
			if (d != null){
				
				addindex(res, d.mbNameIndex);
				addindex(res, d.foreignKeyIndex);
				addindex(res, d.mbOrderIndex);
				addindex(res, d.mbParentNameIndex);
				
				for(Integer i : d.propertiesIndexes){
					addindex(res, i);
				}
				
				addindex(res, d.labelIndex);
			}
		}
		
		
		return res;
	}
	
	/*
	 * simple method to add only new values in the list
	 */
	private void addindex(List<Point> res, int val) {
		if (val < 0){
			return;
		}
		for(Point i : res){
			if (i.x == val){
				return;
			}
		}
		
		res.add(new Point(val, -1));
		
	}

	public void getLevelData(IndexDatas index, IResultSet rs, LevelDatasCache levelDatasCache) throws OdaException {
		LevelDatas lvlData = new LevelDatas();
		
		lvlData.addData(LevelDataType.MEMBER_NAME, rs.getString(index.mbNameIndex + 1 ));//.replace(".", "_"));
		if(index.mbParentNameIndex > -1) {
			lvlData.addData(LevelDataType.PARENT_NAME, rs.getString(index.mbParentNameIndex + 1));//.replace(".", "_"));
		}
		else{
			lvlData.addData(LevelDataType.PARENT_NAME, "All " + dimension.getName());
		}
		if(index.mbOrderIndex > -1) {
			lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(index.mbOrderIndex + 1));
		}
		else{
			lvlData.addData(LevelDataType.MEMBER_ORDER, rs.getString(index.mbNameIndex + 1));
		}
		List<Object> properties = new ArrayList<Object>();
		for(int propInd : index.propertiesIndexes) {
			if (propInd > - 1){
				properties.add(rs.getString(propInd + 1));
			}
			
		}
		lvlData.addData(LevelDataType.MEMBER_PROPERTIES, properties);
		if (index.foreignKeyIndex > - 1){
			lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(index.foreignKeyIndex + 1));
		}
		else{
			lvlData.addData(LevelDataType.FOREIGN_KEY, rs.getString(index.mbNameIndex + 1));
		}
		
		if(index.labelIndex > -1) {
			lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(index.labelIndex + 1));
		}
		else {
			lvlData.addData(LevelDataType.MEMBER_LABEL, rs.getString(index.mbNameIndex + 1));
		}
		
		boolean exists = false;
		for(LevelDatas data : levelDatasCache.getLevelDatas()) {
			if(data.getData(LevelDataType.MEMBER_NAME).equals(lvlData.getData(LevelDataType.MEMBER_NAME))
				&& data.getData(LevelDataType.PARENT_NAME).equals(lvlData.getData(LevelDataType.PARENT_NAME))) {
				exists = true;
				
				//XXX look at the order element
				//oh yeah, fuck oracle
				if(lvlData.getData(LevelDataType.MEMBER_ORDER) != null) {
					Object prev = data.getData(LevelDataType.MEMBER_ORDER);
					Object neww = lvlData.getData(LevelDataType.MEMBER_ORDER);
					int res = comparator.compare(prev, neww);
					if(res < 0) {
						data.addData(LevelDataType.MEMBER_ORDER, neww);
					}
				}
				
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
	
	protected IQuery getOdaQuery(OdaInput input) throws Exception {
		
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
}
