package org.fasd.utils;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPRelation;

public class ActionGetPath {
	private DataObject[] datas;
	private FAModel model;
	Path path;

	public ActionGetPath(FAModel model, DataObject[] datas) {
		this.model = model;
		this.datas = datas;
	}

	public Path getPath() {
		return path;
	}

	public void run() {
		path = getShortestPathBetween(datas);
		// System.out.println(b.toString());
	}

	private Path getShortestPathBetween(DataObject[] tabs) {
		// WTF ???????????????????????????????????????

		// Let's try a different approach.
		// We have the business tables.
		// Let's try to see if they are somehow connected first.
		// If they are not, we add a table that's not being used so far and add
		// it to the equation.
		// We can continue like that until we connect all tables with joins.

		// This is a list of all the paths that we could find between all the
		// tables...
		List<Path> paths = new ArrayList<Path>();
		if (tabs.length < 2)
			return null;
		// Here are the tables we need to link it all together.
		List<DataObject> selectedTables = new ArrayList<DataObject>();
		for (int i = 0; i < tabs.length; i++)
			selectedTables.add(tabs[i]);

		boolean allUsed = tabs.length == 0;
		while (!allUsed) {
			// These are the tables that are not yet used
			List<DataObject> notSelectedTables = getNonSelectedTables(selectedTables);

			Path path = new Path();

			// System.out.println("nr of selected tables: "+selectedTables.size());

			// Generate all combinations of the selected tables...
			for (int i = 0; i < selectedTables.size(); i++) {
				for (int j = i + 1; j < selectedTables.size(); j++) {
					if (i != j) {
						DataObject one = (DataObject) selectedTables.get(i);
						DataObject two = (DataObject) selectedTables.get(j);

						// See if we have a relationship that goes from one to
						// two...
						OLAPRelation relationship = findRelationshipUsing(one, two);
						if (relationship != null && !path.contains(relationship)) {
							path.addRelationship(relationship);
							// System.out.println("Added ["+relationship+"], Path now is: "+path.toString());
						}
					}
				}

				// We need to have (n-1) relationships for n tables, otherwise
				// we will not connect everything.
				// 
				// if (path.size() == selectedTables.size() - 1) {
				if (isPathCorrect(path, tabs[0], tabs[1])) {
					// This is a valid path, the first we find here is probably
					// the shortest
					paths.add(path);
					// We can stop now.
					allUsed = true;
				}
			}

			if (!allUsed) {
				// Add one of the tables to the equation
				// Try one that has a relationship to one of the other tables.
				// Otherwise it doesn't make sense to add it.
				if (notSelectedTables.size() > 0) {
					DataObject businessTable = (DataObject) notSelectedTables.get(0);
					notSelectedTables.remove(0);
					selectedTables.add(businessTable);
				}
				else {
					allUsed = true; // we're done
				}
			}
		}
		if (paths.isEmpty())
			return null;

		Path minPath = paths.get(0);
		for (int i = 1; i < paths.size(); i++) {
			Path path1 = (Path) paths.get(i);
			if (path1.size() < minPath.size())
				minPath = path1;
		}

		return minPath;

	}

	private List<DataObject> getNonSelectedTables(List<DataObject> selectedTables) {
		List<DataObject> l = new ArrayList<DataObject>();

		for (DataSource ds : model.getDataSources()) {
			for (DataObject t : ds.getDataObjects()) {
				if (!selectedTables.contains(t))
					l.add(t);
			}
		}

		return l;
	}

	private OLAPRelation findRelationshipUsing(DataObject one, DataObject two) {
		for (int i = 0; i < model.getRelations().size(); i++) {
			OLAPRelation relationship = model.getRelations().get(i);
			if (relationship.isUsingTable(one) && relationship.isUsingTable(two)) {
				return model.getRelations().get(i);
			}
		}

		return null;
	}

	// private StringBuffer formatPath(Path path){
	// if (path == null)
	// return null;
	//		
	// StringBuffer buf = new StringBuffer();
	// for(int i=0; i<path.size(); i++){
	// OLAPRelation r = path.getRelationship(i);
	// if (i!= 0)
	// buf.append(" AND ");
	//			
	// buf.append(r.getLeftObject().getName() + "." +
	// r.getLeftObjectItem().getName());
	// buf.append("=");
	// buf.append(r.getRightObject().getName() + "." +
	// r.getRightObjectItem().getName());
	// }
	//		
	// return buf;
	// }

	private boolean isPathCorrect(Path path, DataObject one, DataObject two) {

		boolean findOne = false, findTwo = false;

		for (OLAPRelation r : path.getRelations()) {
			if (r.getRightObject() == one || r.getLeftObject() == one)
				findOne = true;

			if (r.getRightObject() == two || r.getLeftObject() == two)
				findTwo = true;
		}

		return findOne && findTwo;

	}
}
