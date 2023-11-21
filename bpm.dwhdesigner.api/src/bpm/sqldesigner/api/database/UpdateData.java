package bpm.sqldesigner.api.database;

import bpm.sqldesigner.api.exception.UpdateColumnException;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.view.SQLView;

public class UpdateData {

	public static RequestStatement updateTableName(Table table, String newName,
			String oldName) {

		if (newName != null)
			return new RequestStatement("ALTER TABLE " + table.getSimplePath()
					+ " RENAME TO " + newName);
		else {
			String oldPath;
			if (table.getSchema() instanceof SchemaNull)
				oldPath = table.getSchema().getCatalog().getName() + "."
						+ oldName;
			else
				oldPath = table.getSchema().getName() + "." + oldName;

			return new RequestStatement("ALTER TABLE " + oldPath
					+ " RENAME TO " + table.getName());
		}
	}

	public static RequestStatement updateViewValue(SQLView view, String newValue) {

		return new RequestStatement("ALTER VIEW " + view.getSimplePath()
				+ " AS " + newValue);
	}

	public static RequestStatement updateSchemaName(Schema schema,
			String newName, String oldName) {

		if (newName != null)
			return new RequestStatement("ALTER SCHEMA " + schema.getName()
					+ " RENAME TO " + newName);
		else
			return new RequestStatement("ALTER SCHEMA " + oldName
					+ " RENAME TO " + schema.getName());
	}

	public static RequestStatement updateColumnName(Column column,
			String newName, String oldName) throws UpdateColumnException {

		if (!column.isPrimaryKey()) {

			String query = "ALTER TABLE " + column.getTable().getSimplePath()
					+ " CHANGE ";

			if (newName != null)
				query += column.getName() + " " + newName;
			else
				query += oldName + " " + column.getName();

			query += " " + column.getTypeString();

			return new RequestStatement(query);
		} else {
			throw new UpdateColumnException("Can't change name of column "
					+ column.getName() + " to " + newName
					+ " because it's a primary key column.");
		}
	}

	public static RequestStatement updateColumnType(Column column,
			String newTypeString) {

		String query = "ALTER TABLE " + column.getTable().getSimplePath()
				+ " MODIFY ";
		query += column.getName();
		if (newTypeString != null)
			query += " " + newTypeString;
		else
			query += " " + column.getTypeString();

		return new RequestStatement(query);
	}

	public static RequestStatement truncateTable(Table table) {
		String query = "TRUNCATE TABLE " + table.getSimplePath();

		return new RequestStatement(query);
	}
}
