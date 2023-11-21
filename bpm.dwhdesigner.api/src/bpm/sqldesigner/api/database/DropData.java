package bpm.sqldesigner.api.database;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class DropData {

	public static RequestStatement dropCatalog(Catalog catalog) {

		return new RequestStatement("DROP DATABASE " + catalog.getName());
	}

	public static RequestStatement dropSchema(Schema schema) {
		if (!(schema instanceof SchemaNull)) {
			return new RequestStatement("DROP SCHEMA "
					+ schema.getCatalog().getName() + "." + schema.getName());
		}

		return null;
	}

	public static RequestStatement dropTable(Table table) {
		return new RequestStatement("DROP TABLE " + table.getPath());
	}

	public static RequestStatement dropColumn(Column column) {
		return new RequestStatement("ALTER TABLE "
				+ column.getTable().getPath() + " DROP COLUMN "
				+ column.getName());
	}

	public static RequestStatement dropProcedure(SQLProcedure procedure) {
		if (procedure.getDropStatement().equals(""))
			return null;
		return new RequestStatement(procedure.getDropStatement());
	}

	public static RequestStatement dropView(SQLView view) {
		return new RequestStatement("DROP VIEW " + view.getName());
	}
}
