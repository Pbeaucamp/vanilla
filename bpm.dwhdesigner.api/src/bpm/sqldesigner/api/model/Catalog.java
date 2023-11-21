package bpm.sqldesigner.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Catalog extends Node {

	protected DatabaseCluster databaseCluster;
	protected TreeMap<String, Schema> schemas = new TreeMap<String, Schema>();

	public Catalog() {
	}

	public Catalog(Catalog catalog) {
		name = catalog.name;
		databaseCluster = catalog.databaseCluster;
	}

	public TreeMap<String, Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(TreeMap<String, Schema> schemas) {
		this.schemas = schemas;
	}

	public DatabaseCluster getDatabaseCluster() {
		return databaseCluster;
	}

	public void setDatabaseCluster(DatabaseCluster databaseCluster) {
		this.databaseCluster = databaseCluster;
	}

	public void addSchema(Schema s) {
		schemas.put(s.getName(), s);
	}

	public Schema getSchema(String schemaName) {
		return schemas.get(schemaName);
	}

	@Override
	public Object[] getChildren() {
		List<Object> schemaValues = new ArrayList<Object>(schemas.values());

		SchemaNull schemaNull = (SchemaNull) schemas.get("null");
		if (schemaNull != null) {
			Object[] schemaNullChild = schemaNull.getChildren();
			schemaValues.remove(schemaNull);
			for (Object o : schemaNullChild)
				schemaValues.add(o);
		}

		return schemaValues.toArray();
	}

	@Override
	public Node getParent() {
		return databaseCluster;
	}

	@Override
	public String getClusterName() {
		return databaseCluster.getName();
	}

	public Object[] getChildrenWithSchemaNull() {
		List<Object> schemaValues = new ArrayList<Object>(schemas.values());
		return schemaValues.toArray();
	}

	@Override
	public DatabaseCluster getCluster() {
		return databaseCluster;
	}

	public void removeSchema(String name) {
		schemas.remove(name);
	}

	@Override
	public void updateName(Node node, String oldName) {
		if (node instanceof Schema) {
			schemas.remove(oldName);
			addSchema((Schema) node);
		}
	}

}
