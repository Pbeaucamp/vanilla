package bpm.united.olap.runtime.model;

import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.model.IDataLocator;

public class ImprovedDataLocator implements IDataLocator {

	private HashMap<DataObjectItem, Integer> measureIndexes;
	private HashMap<DataObjectItem, Integer> levelIndexes;
	
	/*
	 * level Item ->> Level
	 */
	private HashMap<Level, DataObjectItem> levelMappingFromOtherDataSource;
//	private HashMap<Integer, Integer> oldNewIndexes;
	
	/**
	 * 
	 * @param measureIndexes
	 * @param levelIndexes
	 * @param oldNewIndexes
	 */
	public ImprovedDataLocator(HashMap<DataObjectItem, Integer> measureIndexes, HashMap<DataObjectItem, Integer> levelIndexes, HashMap<Level, DataObjectItem> levelMappingFromOtherDataSource) {
		this.measureIndexes = measureIndexes;
		this.levelIndexes = levelIndexes;
//		this.oldNewIndexes = oldNewIndexes;
		this.levelMappingFromOtherDataSource = levelMappingFromOtherDataSource;
	}
	
	@Override
	public Integer getResultSetIndex(Measure measure) {
//		return oldNewIndexes.get(measureIndexes.get(measure.getItem()));
		return measureIndexes.get(measure.getItem());
	}

	@Override
	public Integer getResultSetIndex(Member member) {
		if(member.getParentLevel() == null) {
			return null;
		}
//		return oldNewIndexes.get(levelIndexes.get(member.getParentLevel().getItem()));
		return  levelIndexes.get(member.getParentLevel().getItem());
		
	}

	@Override
	public Integer getResultSetIndex(Level level) {
//		return oldNewIndexes.get(levelIndexes.get(level.getItem()));
		if(levelMappingFromOtherDataSource.get(level) != null) {
			return levelIndexes.get(levelMappingFromOtherDataSource.get(level));
		}
		
//		for(DataObjectItem i : levelMappingFromOtherDataSource.keySet()){
//			if (levelMappingFromOtherDataSource.get(i) == level){
//				return levelIndexes.get(i);
//			}
//		}
		return  levelIndexes.get(level.getItem());
		
	}

	@Override
	public Integer getResultSetIndexInFactTable(DataObjectItem origin) {
//		return oldNewIndexes.get(measureIndexes.get(origin));
//		return oldNewIndexes.get(measureIndexes.get(origin);
		
		for(Level lvl : levelMappingFromOtherDataSource.keySet()) {
			if(levelMappingFromOtherDataSource.get(lvl) == origin) {
				levelIndexes.get(origin);
			}
		}
		
//		if (levelMappingFromOtherDataSource.get(origin) != null){
//			return levelIndexes.get(origin);
//		}
		
		if (levelIndexes.get(origin) != null){
			return levelIndexes.get(origin); 
		}
		
		else{
			return measureIndexes.get(origin);
		}
	}

	@Override
	public Integer getOrderResultSetIndex(Level level) {
		if(level.getOrderItem() == null) {
			return null;
		}
		
		return levelIndexes.get(level.getOrderItem()); 
	}

	@Override
	public DataObjectItem getRelatedDataObjectItem(DataObjectItem factForeignKey, Member member) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<Relation> getPath(DataObject parent, DataObject relatedObject) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public Integer getResultSetIndex(Member member, int levelIndex) throws Exception{
		if(member.getParentLevel() == null) {
			return null;
		}
		if(levelMappingFromOtherDataSource.get(member.getParentHierarchy().getLevels().get(levelIndex)) != null) {
			if(levelIndexes.get(levelMappingFromOtherDataSource.get(member.getParentHierarchy().getLevels().get(levelIndex))) != null) {
				return levelIndexes.get(levelMappingFromOtherDataSource.get(member.getParentHierarchy().getLevels().get(levelIndex)));
			}
		}
		
//		for(DataObjectItem i : levelMappingFromOtherDataSource.keySet()){
//			if (levelMappingFromOtherDataSource.get(i) == member.getParentHierarchy().getLevels().get(levelIndex)){
//				return levelIndexes.get(i);
//			}
//		}
		
		return levelIndexes.get(member.getParentHierarchy().getLevels().get(levelIndex).getItem());
		

	}

}
