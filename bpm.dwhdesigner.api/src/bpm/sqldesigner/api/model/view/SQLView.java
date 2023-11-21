package bpm.sqldesigner.api.model.view;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;

public class SQLView extends Node {

	protected Schema schema;
	protected String value;

	public SQLView() {
	}

	public SQLView(SQLView view) {
		name = view.name;
		value = view.value;
		schema = view.schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatementString() {
		return "";
	}

	public int getViewValueColumn() {
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

	public Schema getSchema() {
		return schema;
	}

	@Override
	public String getClusterName() {
		return schema.getCatalog().getDatabaseCluster().getName();
	}

	public String getSimplePath() {
		if (schema instanceof SchemaNull)
			return schema.getCatalog().getName() + "." + name;
		else
			return schema.getName() + "." + name;
	}

	@Override
	public Node getParent() {
		return getSchema();
	}

	@Override
	public boolean isNotLoaded() {
		return false;
	}

}
