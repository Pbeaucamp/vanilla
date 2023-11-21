package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import bpm.metadata.layer.logical.DijkstraAlgorithm;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.DijkstraAlgorithm.Edge;
import bpm.metadata.layer.logical.DijkstraAlgorithm.Graph;
import bpm.metadata.layer.logical.DijkstraAlgorithm.Vertex;
import bpm.metadata.pathfinder.Path;

public class PathFinder {
	private List<IDataStream> tables = new ArrayList<IDataStream>();
	private List<Relation> availables = new ArrayList<Relation>();
	private List<RelationStrategy> relationStrategies;

	public PathFinder(List<Relation> relations, List<IDataStream> tables, List<RelationStrategy> relationStrategies) {
		this.tables = tables;
		this.relationStrategies = relationStrategies;
		availables.addAll(relations);
	}

	public Path getPath() {
		if (tables.size() <= 1) {
			return null;
		}

		List<Relation> result = new ArrayList<Relation>();
		
		Collections.sort(tables, new Comparator<IDataStream>() {
			@Override
			public int compare(IDataStream o1, IDataStream o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		List<List<Relation>> possiblePaths = findAllPossiblePaths();

		// Find the shortest path
		int minSize = Integer.MAX_VALUE;
		for (List<Relation> relations : possiblePaths) {
			if (relations.size() < minSize) {
				minSize = relations.size();
				result = relations;
			}
		}

		Path p = new Path();

		for (Relation r : result) {
			p.addRelationship(r);
		}

		return p;
	}

	private List<Relation> useDijkstra(IDataStream origin, IDataStream destination, List<Relation> relations) {

		List<Vertex> lookedTables = new ArrayList<Vertex>();
		HashMap<IDataStream, Vertex> map = new HashMap<IDataStream, Vertex>();
		List<Edge> lookedRel = new ArrayList<Edge>();

		
		Vertex vorigin = new DijkstraAlgorithm().new Vertex(origin.getName(), origin.getName(), origin);
		map.put(origin, vorigin);
		lookedTables.add(vorigin);
		Vertex vdest = new DijkstraAlgorithm().new Vertex(destination.getName(), destination.getName(), destination);
		map.put(destination, vdest);
		lookedTables.add(vdest);
		
		for(Relation rel : relations) {
			
			IDataStream left = rel.getLeftTable();
			if(map.get(left) == null) {
				Vertex v = new DijkstraAlgorithm().new Vertex(left.getName(), left.getName(), left);
				map.put(left, v);
			}
			
			IDataStream right = rel.getRightTable();
			if(map.get(right) == null) {
				Vertex v = new DijkstraAlgorithm().new Vertex(right.getName(), right.getName(), right);
				map.put(right, v);
			}
			
			Edge e = new DijkstraAlgorithm().new Edge(rel.getName() + "1", map.get(rel.getLeftTable()), map.get(rel.getRightTable()), 1, rel);
			lookedRel.add(e);
			Edge ee = new DijkstraAlgorithm().new Edge(rel.getName() + "2", map.get(rel.getRightTable()), map.get(rel.getLeftTable()), 1, rel);
			lookedRel.add(ee);
			
		}
		
		Graph g = new DijkstraAlgorithm().new Graph(lookedTables, lookedRel);
		
		DijkstraAlgorithm al = new DijkstraAlgorithm(g);
		
		al.execute(lookedTables.get(0));
		LinkedList<Vertex> vs = al.getPath(lookedTables.get(1));
		
		if(vs == null) {
			return new ArrayList<Relation>();
		}
		
		//find the relations back
		List<Relation> rels = findBackRelations(vs, relations);
		
		return rels;
	}

	private List<Relation> findBackRelations(LinkedList<Vertex> vs, List<Relation> relations) {

		List<Relation> result = new ArrayList<Relation>();
		
		for(int i = 0 ; i < vs.size() - 1 ; i++) {
			Vertex source = vs.get(i);
			Vertex dest = vs.get(i+1);
			
			for(Relation rel : relations) {
				if(rel.getLeftTable().equals(source.getTable()) && rel.getRightTable().equals(dest.getTable())) {
					result.add(rel);
					break;
				}
				if(rel.getLeftTable().equals(dest.getTable()) && rel.getRightTable().equals(source.getTable())) {
					result.add(rel);
					break;
				}
			}
		}
		
		return result;
	}

	private List<List<Relation>> findAllPossiblePaths() {
		List<List<Relation>> result = new ArrayList<List<Relation>>();

		List<String> lookedTableNames = new ArrayList<String>();
		for (IDataStream ds : tables) {
			lookedTableNames.add(ds.getName());
		}

		List<RelationStrategy> toUse = new ArrayList<RelationStrategy>();

		List<String> toRemove = new ArrayList<String>();

		for (RelationStrategy strat : relationStrategies) {
			boolean useIt = true;
			for (String t : lookedTableNames) {
				if (!strat.getTableNames().contains(t)) {
					useIt = false;
					break;
				}
			}
			if (useIt) {
				toUse.add(strat);
				toRemove.addAll(strat.getTableNames());
			}
		}

		lookedTableNames.removeAll(toRemove);

		List<Relation> baseResult = new ArrayList<Relation>();
		for (RelationStrategy s : toUse) {
			for (String key : s.getRelationKeys()) {
				Relation r = findRelation(key);
				baseResult.add(r);
			}
		}

		if (!lookedTableNames.isEmpty()) {
			if (toUse.isEmpty()) {
				// just ignore strategies and find paths
				result = findPaths(tables, availables);
				// dumpThePaths(result);
				result = removeNonWorkingPaths(result);

			} else {
				// that's the bad part
				// we need to find the relations between the remaining tables
				// but also with the ones used in the strategies...

				List<Relation> usedRelations = new ArrayList<Relation>();
				List<IDataStream> usedTables = new ArrayList<IDataStream>();

				for (RelationStrategy strat : toUse) {
					for (String key : strat.getRelationKeys()) {
						Relation rel = findRelation(key);
						usedRelations.add(rel);
					}
					for (String table : strat.getTableNames()) {
						usedTables.add(findTable(table));
					}
				}

				result = findPaths((List<IDataStream>) CollectionUtils.subtract(tables, usedTables), (List<Relation>) CollectionUtils.subtract(availables, usedRelations));
				// dumpThePaths(result);
				result = removeNonWorkingPaths(result);
			}
		} else {
			result.add(baseResult);
		}

		return result;
	}

	private IDataStream findTable(String table) {
		for (IDataStream ds : tables) {
			if (ds.getName().equals(table)) {
				return ds;
			}
		}
		return null;
	}

	/**
	 * If there's not all the table in the path, we can't keep it
	 * 
	 * @param result
	 */
	private List<List<Relation>> removeNonWorkingPaths(List<List<Relation>> result) {
		List<List<Relation>> res = new ArrayList<List<Relation>>();
		for (List<Relation> path : result) {
			int commonTables = 0;
			for (IDataStream ds : tables) {
				for (Relation rel : path) {
					if (rel.getLeftTable().getName().equals(ds.getName()) || rel.getRightTable().getName().equals(ds.getName())) {
						commonTables++;
						break;
					}
				}
			}

			if (commonTables == tables.size()) {
				res.add(path);
			}

		}
		return res;
	}

	private void dumpThePaths(List<List<Relation>> result) {

		int i = 1;
		for (List<Relation> res : result) {

			StringBuilder buf = new StringBuilder();
			buf.append("path" + i++);

			for (Relation r : res) {
				buf.append("\n" + r.getLeftTableName() + " - " + r.getRightTableName());
			}
			System.out.println(buf.toString());
		}
	}

	private List<List<Relation>> findPaths(List<IDataStream> allTables, List<Relation> allRelations) {

		List<IDataStream> usedTables = new ArrayList<IDataStream>();
		List<Relation> usedRelations = new ArrayList<Relation>();
		List<List<Relation>> result = new ArrayList<List<Relation>>();
		// find the direct relationship first
		for (IDataStream table : allTables) {

			List<Relation> relations = getPossibleRelations(table, allRelations);
			for (Relation relation : relations) {

				if (usedRelations.contains(relation)) {
					continue;
				}

				if (relation.getLeftTable().equals(table)) {
					if (allTables.contains(relation.getRightTable())) {
						usedRelations.add(relation);
						if (!usedTables.contains(relation.getRightTable())) {
							usedTables.add(relation.getRightTable());
						}
						if (!usedTables.contains(relation.getLeftTable())) {
							usedTables.add(relation.getLeftTable());
						}
						//break;
					}
				} else {
					if (allTables.contains(relation.getLeftTable())) {
						usedRelations.add(relation);
						if (!usedTables.contains(relation.getLeftTable())) {
							usedTables.add(relation.getLeftTable());
						}
						if (!usedTables.contains(relation.getRightTable())) {
							usedTables.add(relation.getRightTable());
						}
						//break;
					}
				}

			}

		}

		if (usedTables.size() == allTables.size()) {

			// TODO Look for loops maybe ????

			result.add(usedRelations);
			return result;
		}

		// if not all the tables are linked, we need to find the path
		else {
			//XXX Restart the treatment
			usedTables.clear();
			usedRelations.clear();
			List<IDataStream> tablesToFind = (List<IDataStream>) CollectionUtils.subtract(allTables, usedTables);
			// For each of those table we want to link them with the others

			for (IDataStream ds : tablesToFind) {
				if(usedTables.isEmpty()) {
					usedTables.add(tablesToFind.get(1));
				}
				
				if(usedTables.contains(ds)) {
					continue;
				}
				
				List<Relation> path = findShortestPath(ds, usedTables, usedRelations);
				if(path == null) {
					return result;
				}
				usedRelations.addAll(path);
				
				for(Relation rel : path) {
					if(!usedTables.contains(rel.getLeftTable())) {
						usedTables.add(rel.getLeftTable());
					}
					if(!usedTables.contains(rel.getRightTable())) {
						usedTables.add(rel.getRightTable());
					}
				}
				
				//XXX we need to check all the paths
//				if(isFinished(usedTables, tablesToFind)) {
//					break;
//				}
 				
				if(!usedTables.contains(ds)) {
					usedTables.add(ds);
				}
			}

			result.add(usedRelations);
			return result;
		}

		// usedTables.add(tables.get(0));
		// return findNextRelations(tables.get(0), usedRelations, usedTables);
	}

	private boolean isFinished(List<IDataStream> usedTables, List<IDataStream> tablesToFind) {
		boolean finished = usedTables.containsAll(tablesToFind);
		return finished;
	}

	/**
	 * This should find the best path from origin to one of the used tables
	 * 
	 * We will find the path between origin and all the used tables and keep the
	 * shortest
	 * 
	 * @param origin
	 * @param usedTables
	 * @param usedRelations
	 * @return
	 */
	private List<Relation> findShortestPath(IDataStream origin, List<IDataStream> usedTables, List<Relation> usedRelations) {
		List<Relation> relations = null;
		for (IDataStream ds : usedTables) {
			if(ds.equals(origin)) {
				continue;
			}
			List<Relation> path = findPathBetweenTables(origin, ds, usedRelations);
			if (relations == null && !path.isEmpty() || (relations != null && (relations.size() > path.size()))) {
				relations = path;
			}
			
		}
		return relations;
	}

	private List<Relation> findPathBetweenTables(IDataStream origin, IDataStream destination, List<Relation> usedRelations) {

		List<Relation> relations = useDijkstra(origin, destination, availables);//(List<Relation>) CollectionUtils.subtract(availables, usedRelations));
		
		return relations;
	}

	private List<Relation> getPossibleRelations(IDataStream actualTable, List<Relation> ava) {
		List<Relation> result = new ArrayList<Relation>();
		for (Relation r : ava) {
			if (r.isUsingTable(actualTable)) {
				result.add(r);
			}
		}
		return result;
	}

	private Relation findRelation(String key) {
		for (Relation r : availables) {
			if (r.getRelationKey().equals(key)) {
				return r;
			}
		}
		return null;
	}


}
