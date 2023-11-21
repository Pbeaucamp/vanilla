package bpm.sqldesigner.api.model;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.constants.types.StandardTypes;

public class Column extends Node {

	protected Table table;
	protected Type type;
	protected String defaultValue = null;
	protected int size = -1;
	protected boolean isNullable = true;
	protected boolean isPrimaryKey = false;
	protected boolean isUnsigned = false;
	protected boolean isAutoInc = false;
	protected boolean needsSize = false;
	

	protected LinkForeignKey targetPrimaryKey = null;

	protected List<LinkForeignKey> sourceForeignKeys = new ArrayList<LinkForeignKey>();

	public Column() {

	}

	public Column(Column column) {
		name = column.name;
		table = column.table;
		type = column.type;
		defaultValue = column.defaultValue;
		size = column.size;
		isNullable = column.isNullable;
		isPrimaryKey = column.isPrimaryKey;
		isUnsigned = column.isUnsigned;
		isAutoInc = column.isAutoInc;
		needsSize = column.needsSize;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		if (type.getName().contains("VARCHAR"))
			needsSize = true;
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public Column getTargetColumnPrimaryKey() {
		return targetPrimaryKey.getTarget();
	}

	public void setTargetPrimaryKey(LinkForeignKey targetPrimaryKey) {
		this.targetPrimaryKey = targetPrimaryKey;
		getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null,
				targetPrimaryKey);
	}

	public List<LinkForeignKey> getSourceForeignKeys() {
		return sourceForeignKeys;
	}

	public void addSourceForeignKey(LinkForeignKey sourceForeignKey) {
		sourceForeignKeys.add(sourceForeignKey);
		getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
				sourceForeignKey);
	}

	public boolean isUnsigned() {
		return isUnsigned;
	}

	public void setUnsigned(boolean isUnsigned) {
		this.isUnsigned = isUnsigned;
	}

	public boolean isForeignKey() {
		return targetPrimaryKey != null;
	}

	public boolean gotDefault() {
		return defaultValue != null;
	}

	public String getPath() {
		Schema schema = table.getSchema();
		Catalog catalog = schema.getCatalog();

		if (schema instanceof SchemaNull)
			return catalog.getName() + "." + table.getName() + "." + name;

		else
			return catalog.getName() + "." + schema.getName() + "."
					+ table.getName() + "." + name;
	}

	@Override
	public String getClusterName() {
		DatabaseCluster databaseCluster = table.getSchema().getCatalog()
				.getDatabaseCluster();

		return databaseCluster.getName();
	}

	public String getTypeString() {
		String out = "";
		out = type.getName();

		if (needsSize) {
			out += "(" + size + ")";
		}

		if (isUnsigned) {
			out += " UNSIGNED";
		}

		if (!isNullable) {
			out += " NOT NULL";
		}

		if (gotDefault()) {
			if (type.equals(StandardTypes.NUMERIC)
					|| type.equals(StandardTypes.INTEGER)
					|| defaultValue.equals("NULL")
					|| defaultValue.equals("CURRENT_TIMESTAMP"))
				out += " DEFAULT " + defaultValue;
			else {
				out += " DEFAULT '" + defaultValue + "'";
			}
		}

		if (isAutoInc)
			out += " AUTO_INCREMENT";

		return out;
	}

	public boolean isAutoInc() {
		return isAutoInc;
	}

	public void setAutoInc(boolean isAutoInc) {
		this.isAutoInc = isAutoInc;
	}

	@Override
	public Node getParent() {
		return table;
	}

	@Override
	public boolean isNotFullLoaded() {
		return false;
	}

	@Override
	public boolean isNotLoaded() {
		return false;
	}

	public void removeForeignKey(LinkForeignKey foreignKey) {
		sourceForeignKeys.remove(foreignKey);
		
		getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
				foreignKey);
	}

	public LinkForeignKey getTargetPrimaryKey() {
		return targetPrimaryKey;
	}

	public void setNeedsSize(boolean b) {
		needsSize = b;
	}

	public boolean needsSize() {
		return needsSize;
	}

}
