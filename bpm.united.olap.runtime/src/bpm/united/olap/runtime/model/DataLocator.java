package bpm.united.olap.runtime.model;

import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.model.IDataLocator;

public class DataLocator implements IDataLocator{
	private Cube cube;
	private DataObject factTable;
	private HashMap<Hierarchy, Relation> hierachiesRelations;
	private PathFinder pathFinder;
	
	public DataLocator(Cube cube, DataObject factTable, HashMap<Hierarchy, Relation> hierachiesRelations, PathFinder pathFinder){
		this.cube = cube;
		this.factTable = factTable;
		this.hierachiesRelations = hierachiesRelations;
		this.pathFinder = pathFinder;
	}

	@Override
	public Integer getResultSetIndex(Measure measure) {
		for(Measure m : cube.getMeasures()){
			
			if (m == measure || m.getUname().equals(measure.getUname())){
				return m.getItem().getParent().getItems().indexOf(m.getItem());
				
			}
			
		}
		return null;
	}

	@Override
	public Integer getResultSetIndex(Member member) {
		
		//Level within Dimension
		if(member.getParentLevel() == null){
			return null;
		}else if (factTable == member.getParentLevel().getItem().getParent()){
			return factTable.getItems().indexOf(member.getParentLevel().getItem());
		}
		for(Hierarchy hiera : hierachiesRelations.keySet()) {
			if(hiera.getUname().equals(member.getParentHierarchy().getUname())) {
				Relation rel = hierachiesRelations.get(hiera);
				if(rel.getLeftItem().getParent() == factTable) {
					return factTable.getItems().indexOf(rel.getLeftItem());
				}
				else if(rel.getRightItem().getParent() == factTable) {
					return factTable.getItems().indexOf(rel.getRightItem());
				}
			}
		}
		/*
		 * need to find a Path between factTable and Member's Level
		 */
		
		List<Relation> path = pathFinder.findPath(factTable, member.getParentLevel().getItem().getParent());
		if(path.get(0).getLeftItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getLeftItem());
		}
		else if(path.get(0).getRightItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getRightItem());
		}
		
		throw new RuntimeException("Not supported");
		
//		return null;
	}

	@Override
	public Integer getResultSetIndexInFactTable(DataObjectItem origin) {
		return factTable.getItems().indexOf(origin);
	}

	@Override
	public Integer getResultSetIndex(Level level) {
		if(level == null){
			return null;
		}else if (factTable == level.getItem().getParent()){
			return factTable.getItems().indexOf(level.getItem());
		}
		else if(hierachiesRelations.containsKey(level.getParentHierarchy())) {
			Relation rel = hierachiesRelations.get(level.getParentHierarchy());
			if(rel.getLeftItem().getParent() == factTable) {
				return factTable.getItems().indexOf(rel.getLeftItem());
			}
			else if(rel.getRightItem().getParent() == factTable) {
				return factTable.getItems().indexOf(rel.getRightItem());
			}
		}
		
		List<Relation> path = pathFinder.findPath(factTable, level.getItem().getParent());
		if(path.get(0).getLeftItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getLeftItem());
		}
		else if(path.get(0).getRightItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getRightItem());
		}
		throw new RuntimeException("Not supported");
	}

	@Override
	public Integer getOrderResultSetIndex(Level level) {
		if(level == null || level.getOrderItem() == null){
			return null;
		}else if (factTable == level.getOrderItem().getParent()){
			return factTable.getItems().indexOf(level.getOrderItem());
		}
		else if(hierachiesRelations.containsKey(level.getParentHierarchy())) {
			Relation rel = hierachiesRelations.get(level.getParentHierarchy());
			if(rel.getLeftItem().getParent() == factTable) {
				return factTable.getItems().indexOf(rel.getLeftItem());
			}
			else if(rel.getRightItem().getParent() == factTable) {
				return factTable.getItems().indexOf(rel.getRightItem());
			}
		}
		List<Relation> path = pathFinder.findPath(factTable, level.getItem().getParent());
		if(path.get(0).getLeftItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getLeftItem());
		}
		else if(path.get(0).getRightItem().getParent() == factTable) {
			return factTable.getItems().indexOf(path.get(0).getRightItem());
		}
		throw new RuntimeException("Not supported");
	}

	@Override
	public DataObjectItem getRelatedDataObjectItem(DataObjectItem factForeignKey, Member member) throws Exception {
		if (factTable == member.getParentHierarchy().getParentDimension().getDataObject()){
			return factForeignKey;
		}
		for(Hierarchy hiera : hierachiesRelations.keySet()) {
			if(hiera.getUname().equals(member.getParentHierarchy().getUname())) {
				Relation rel = hierachiesRelations.get(hiera);
				if(rel.getLeftItem().getParent() == factTable) {
					return rel.getRightItem();
				}
				else if(rel.getRightItem().getParent() == factTable) {
					return rel.getLeftItem();
				}
			}
		}
		
		return null;
		/*
		 * need to find a Path between factTable and Member's Level
		 */
		
//		List<Relation> path = pathFinder.findPath(factTable, factForeignKey.getParent());
//		if(path.get(0).getLeftItem().getParent() == factTable) {
//			return path.get(0).getLeftItem();
//		}
//		else if(path.get(0).getRightItem().getParent() == factTable) {
//			return path.get(0).getRightItem();
//		}
		
//		throw new RuntimeException("Not supported");
	}

	@Override
	public List<Relation> getPath(DataObject parent, DataObject relatedObject) throws Exception {
		
		return pathFinder.findPath(parent, relatedObject);
	}
	@Override
	public Integer getResultSetIndex(Member member, int levelIndex) throws Exception{
		//throw new Exception("Not implemented");
		return getResultSetIndex(member.getParentHierarchy().getLevels().get(levelIndex));
	}
	
}
