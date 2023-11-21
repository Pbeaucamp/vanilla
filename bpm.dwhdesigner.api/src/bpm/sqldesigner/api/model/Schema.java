package bpm.sqldesigner.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class Schema extends Node {

	protected TreeMap<String, Table> tables = new TreeMap<String, Table>();
	protected Catalog catalog;
	protected HashMap<String, SQLView> views = new HashMap<String, SQLView>();
	protected HashMap<String, SQLProcedure> procedures = new HashMap<String, SQLProcedure>();

	public Schema() {
	}

	public Schema(Schema schema) {
		name = schema.name;
		catalog = schema.catalog;

		// for (String tableName : schema.tables.keySet())
		// addTable(schema.tables.get(tableName));
		//		
		// for (String viewsName : schema.views.keySet())
		// addView(schema.views.get(viewsName));

	}

	public TreeMap<String, Table> getTables() {
		return tables;
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public void addTable(Table table) {
		tables.put(table.getName(), table);
		getListeners().firePropertyChange(PROPERTY_ADD, null, table);
	}

	public Table getTable(String tName) {
		return tables.get(tName);
	}

	public void addView(SQLView view) {
		views.put(view.getName(), view);
	}

	public HashMap<String, SQLView> getViews() {
		return views;
	}

	public void addProcedure(SQLProcedure procedure) {
		procedures.put(procedure.getName(), procedure);
	}

	public HashMap<String, SQLProcedure> getProcedures() {
		return procedures;
	}

	public SQLProcedure getProcedure(String procedureName) {
		return procedures.get(procedureName);
	}

	@Override
	public Object[] getChildren() {

		List<Node> children = new ArrayList<Node>();
		children.addAll(tables.values());
		children.addAll(procedures.values());
		children.addAll(views.values());

		return children.toArray();
	}

	public List<Node> getTablesList() {
		List<Node> children = new ArrayList<Node>();
		children.addAll(tables.values());
		return children;
	}

	@Override
	public Node getParent() {
		return catalog;
	}

	@Override
	public String getClusterName() {
		return getCatalog().getDatabaseCluster().getName();
	}

	public void removeTable(String name) {
		tables.remove(name);
		getListeners().firePropertyChange(PROPERTY_REMOVE, null, name);
	}

	public Object getView(String text) {
		return views.get(text);
	}

	@Override
	public void updateName(Node node, String oldName) {
		if (node instanceof Table) {
			tables.remove(oldName);
			addTable((Table) node);
		}
	}

	public void removeView(String name) {
		views.remove(name);
	}

	public void removeProcedure(String name) {
		procedures.remove(name);
	}

	public List<Table> getTablesWithoutLayout() {
		List<Table> list = new ArrayList<Table>();

		for (String tableName : tables.keySet()) {
			Table table = tables.get(tableName);
			if (table.layoutIsNull())
				list.add(table);
		}
		return list;
	}

	public void clearChildren() {
		tables.clear();
		views.clear();
		procedures.clear();
	}
}
