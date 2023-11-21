package bpm.sqldesigner.api.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Table extends Node {

	protected Schema schema;
	protected LinkedHashMap<String, Column> columns = new LinkedHashMap<String, Column>();

	public Table(Table table) {
		name = table.name;
		schema = table.schema;

		HashMap<String, Column> hmColumns = table.getColumns();
		for (String columnName : hmColumns.keySet()) {
			Column column = hmColumns.get(columnName);
			Column newColumn = new Column(column);
			newColumn.setTable(this);
			addColumn(newColumn);
		}
		setLayout(table.getLayout()[0], table.getLayout()[1], table.getLayout()[2], table.getLayout()[3]);
	}

	public Table() {
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public HashMap<String, Column> getColumns() {
		return columns;
	}

	public void addColumn(Column column) {
		columns.put(column.getName(), column);
		getListeners().firePropertyChange(PROPERTY_ADD, null, column);
	}

	public Column getColumn(String cName) {
		return columns.get(cName);
	}

	@Override
	public String getClusterName() {
		return schema.getCatalog().getDatabaseCluster().getName();
	}

	public String getPath() {
		if (schema instanceof SchemaNull)
			return schema.getCatalog().getName() + "." + name;
		else
			return schema.getCatalog().getName() + "." + schema.getName() + "."
					+ name;
	}

	public String getSimplePath() {
		if (schema instanceof SchemaNull)
			return schema.getCatalog().getName() + "." + name;
		else
			return schema.getName() + "." + name;
	}

	@Override
	public Object[] getChildren() {
		return columns.values().toArray();
	}

	@Override
	public Node getParent() {
		return schema;
	}

	public void removeColumn(String name) {
		columns.remove(name);
	}

	@Override
	public void updateName(Node node, String oldName) {
		if (node instanceof Column) {
			columns.remove(oldName);
			addColumn((Column) node);
		}
	}

	public void setFocus() {
		getListeners().firePropertyChange(FOCUS, null, this);
	}
}
