package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Relation;

public class SnowFlakesPathFinder implements PathFinder {

	private List<Relation> existingRelations;
	
	private HashMap<String, List<Relation>> findedPaths = new HashMap<String, List<Relation>>();
	
	public SnowFlakesPathFinder(List<Relation> existingRelations) {
		this.existingRelations = existingRelations;
	}
	
	@Override
	public List<Relation> findPath(DataObject table1, DataObject table2) {
		
		List<Relation> res = findedPaths.get(table1.getQueryText()+table2.getQueryText());
		if(res != null) {
			return res;
		}
		
		List<List<Relation>> result = new ArrayList<List<Relation>>();
		
		for(Relation rel : existingRelations) {
			if(rel.getLeftItem().getParent() == table1 || rel.getRightItem().getParent() == table1) {
				List<Relation> usedRelations = new ArrayList<Relation>();
				List<Relation> relations = new ArrayList<Relation>();
				if(findPath(table1, table2, rel, usedRelations, relations)) {
					if(relations.size() > 0) {
						result.add(relations);
					}
				}
			}
		}
		
		List<Relation> shortestPath = null;
		int minSize = Integer.MAX_VALUE;
		for(List<Relation> rels : result) {
			if(rels.size() < minSize) {
				shortestPath = rels;
				minSize = rels.size();
			}
		}
		
		findedPaths.put(table1.getQueryText()+table2.getQueryText(), shortestPath);
		
		return shortestPath;
	}

	/**
	 * Find the path between two tables
	 * @param table1 the origin table
	 * @param table2 the destination table
	 * @param startingRelation the relation to start with
	 * @param usedRelations the already used relations (to avoid circlings)
	 * @param result the result path
	 */
	private boolean findPath(DataObject table1, DataObject table2, Relation startingRelation, List<Relation> usedRelations, List<Relation> result) {
		usedRelations.add(startingRelation);
		if(startingRelation.getLeftItem().getParent() == table1) {
			result.add(startingRelation);
			if(startingRelation.getRightItem().getParent() == table2) {
				return true;
			}
			else {
				List<Relation> possibleRelations = findNextPossibleRelations(startingRelation.getRightItem().getParent(), usedRelations);
				List<List<Relation>> possiblePath = new ArrayList<List<Relation>>();
				for(Relation rel : possibleRelations) {
					List<Relation> relations = new ArrayList<Relation>();
					if(findPath(startingRelation.getRightItem().getParent(), table2, rel, usedRelations, relations)) {
						possiblePath.add(relations);
					}
				}
				List<Relation> shortestPath = null;
				int minSize = Integer.MAX_VALUE;
				for(List<Relation> rels : possiblePath) {
					if(rels.size() < minSize) {
						shortestPath = rels;
						minSize = rels.size();
					}
				}
				if(shortestPath == null || shortestPath.size() <= 0) {
					return false;
				}
				result.addAll(shortestPath);
				return true;
			}
		}
		else if(startingRelation.getRightItem().getParent() == table1) {
			result.add(startingRelation);
			if(startingRelation.getLeftItem().getParent() == table2) {
				return true;
			}
			else {
				List<Relation> possibleRelations = findNextPossibleRelations(startingRelation.getLeftItem().getParent(), usedRelations);
				List<List<Relation>> possiblePath = new ArrayList<List<Relation>>();
				for(Relation rel : possibleRelations) {
					List<Relation> relations = new ArrayList<Relation>();
					if(findPath(startingRelation.getLeftItem().getParent(), table2, rel, usedRelations, relations)) {
						possiblePath.add(relations);
					}
				}
				List<Relation> shortestPath = null;
				int minSize = Integer.MAX_VALUE;
				for(List<Relation> rels : possiblePath) {
					if(rels.size() < minSize) {
						shortestPath = rels;
						minSize = rels.size();
					}
				}
				if(shortestPath == null || shortestPath.size() <= 0) {
					return false;
				}
				result.addAll(shortestPath);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Find all possible relations
	 * @param parent
	 * @param usedRelations
	 * @return
	 */
	private List<Relation> findNextPossibleRelations(DataObject parent, List<Relation> usedRelations) {
		
		List<Relation> possibleRelations = new ArrayList<Relation>();
		for(Relation rel : existingRelations) {
			if(!usedRelations.contains(rel)) {
				if(rel.getLeftItem().getParent() == parent || rel.getRightItem().getParent() == parent) {
					possibleRelations.add(rel);
				}
			}
		}
		
		return possibleRelations;
	}

	@Override
	public List<DataObjectItem> findUsedColumns(DataObject parent) {
		List<DataObjectItem> usedColumns = new ArrayList<DataObjectItem>();
		for(Relation rel : existingRelations) {
			if(rel.getLeftItem().getParent() == parent) {
				if(!usedColumns.contains(rel.getLeftItem())) {
					usedColumns.add(rel.getLeftItem());
				}
			}
			else if(rel.getRightItem().getParent() == parent) {
				if(!usedColumns.contains(rel.getRightItem())) {
					usedColumns.add(rel.getRightItem());
				}
			}
		}
		
		return usedColumns;
	}
	
	
}
