package bpm.sqldesigner.api.model.procedure;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;

public class SQLProcedure extends Node {

	protected Schema schema;
	protected String value = null;
	protected int type;

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCreateStatement() {
		return "";
	}

	public int getProcedureValueColumn() {
		return -1;
	}

	public String getValue() {
		return value;
	}

	public String getPath() {
		if (schema instanceof SchemaNull) {
			return schema.getCatalog().getName() + "." + name;
		} else
			return schema.getCatalog().getName() + "." + schema.getName() + "."
					+ name;
	}

	public String getCatalogName() {
		return schema.getCatalog().getDatabaseCluster().getName();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDropStatement() {
		return "";
	}

	public Schema getSchema() {
		return schema;
	}

	public String getSimplePath() {
		if (schema instanceof SchemaNull)
			return schema.getCatalog().getName() + "." + name;
		else
			return schema.getName() + "." + name;
	}

	@Override
	public String getClusterName() {
		return schema.getCatalog().getDatabaseCluster().getName();
	}

	@Override
	public Node getParent() {
		return schema;
	}
	
	
}
