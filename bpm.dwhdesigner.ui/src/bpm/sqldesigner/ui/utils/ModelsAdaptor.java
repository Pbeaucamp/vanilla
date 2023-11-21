package bpm.sqldesigner.ui.utils;

import java.util.HashMap;
import java.util.TreeMap;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class ModelsAdaptor {

	public static bpm.sqldesigner.query.model.Schema getSchema(Schema schema) {
		bpm.sqldesigner.query.model.Schema toSchema = new bpm.sqldesigner.query.model.Schema();
		toSchema.setName(schema.getName());

		TreeMap<String, Table> hmTables = schema.getTables();

		for (String tableName : hmTables.keySet()) {
			Table table = hmTables.get(tableName);

			bpm.sqldesigner.query.model.Table toTable = new bpm.sqldesigner.query.model.Table();
			toTable.setName(table.getName());
			toTable.setParent(toSchema);
			toSchema.addChild(toTable);

			HashMap<String, Column> hmColumns = table.getColumns();

			for (String columnName : hmColumns.keySet()) {
				Column column = hmColumns.get(columnName);

				bpm.sqldesigner.query.model.Column toColumn = new bpm.sqldesigner.query.model.Column();

				toColumn.setName(column.getName());
				toColumn.setParent(toTable);
				toColumn.setType(column.getType().getName());
				if (column.isPrimaryKey())
					toColumn.setKey(true);
				
				toTable.addChild(toColumn);

			}
		}
		
		return toSchema;
	}
}
