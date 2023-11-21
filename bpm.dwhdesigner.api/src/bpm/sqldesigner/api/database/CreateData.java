package bpm.sqldesigner.api.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.view.SQLView;

public class CreateData {

	public static List<RequestStatement> createCatalog(Catalog catalog) {
		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();

		RequestStatement request = createSimpleCatalog(catalog);
		listRequests.add(request);

		TreeMap<String, Schema> hmSchemas = catalog.getSchemas();
		for (String schemaName : hmSchemas.keySet()) {
			Schema schema = hmSchemas.get(schemaName);

			listRequests.addAll(createSchema(schema));

		}
		return listRequests;
	}

	public static RequestStatement createSimpleCatalog(Catalog catalog) {

		RequestStatement request = new RequestStatement("CREATE DATABASE "
				+ catalog.getName());

		return request;
	}

	public static List<RequestStatement> createSchema(Schema schema) {
		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();

		if (!(schema instanceof SchemaNull)) {
			RequestStatement request = createSimpleSchema(schema);

			listRequests.add(request);
		}

		TreeMap<String, Table> hmTables = schema.getTables();

		for (String tableName : hmTables.keySet()) {
			Table table = hmTables.get(tableName);
			listRequests.add(createTable(table));
		}
		return listRequests;
	}

	public static RequestStatement createSimpleSchema(Schema schema) {
		RequestStatement request = new RequestStatement("CREATE SCHEMA "
				+ schema.getName());

		return request;
	}

	public static RequestStatement createTable(Table table) {
		String query = "CREATE TABLE " + table.getSimplePath();
		if (table.getColumns().size() != 0)
			query += " (" + '\n';

		HashMap<String, Column> hmColumns = table.getColumns();

		boolean first = true;
		for (String columnName : hmColumns.keySet()) {
			if (!first)
				query += ",";
			else
				first = false;
			Column column = hmColumns.get(columnName);
			query += column.getName() + " " + column.getTypeString();
			if (column.isPrimaryKey())
				query += " PRIMARY KEY";

			query += '\n';
		}
		if (table.getColumns().size() != 0)
			query += ")";

		return new RequestStatement(query);
	}

	public static List<RequestStatement> createColumn(Column column) {
		List<RequestStatement> list = new ArrayList<RequestStatement>();

		Schema schema = column.getTable().getSchema();
		String root;
		if (schema instanceof SchemaNull)
			root = schema.getCatalog().getName();
		else
			root = schema.getName();

		String query = "ALTER TABLE " + root + "."
				+ column.getTable().getName() + " ADD COLUMN ";
		query += column.getName() + " " + column.getTypeString();

		list.add(new RequestStatement(query));

		if (column.isPrimaryKey()) {
			String queryPK = "ALTER TABLE " + root + "."
					+ column.getTable().getName() + " ADD PRIMARY KEY ("
					+ column.getName() + ")";
			list.add(new RequestStatement(queryPK));
		}
		
		return list;
	}

	public static RequestStatement createView(SQLView viewA) {
		RequestStatement req = new RequestStatement("CREATE VIEW " + viewA.getSimplePath()
				+ " AS " + viewA.getValue());
		req.setSchema(viewA.getSchema());
		
		return req;
	}

	public static RequestStatement createForeignKey(LinkForeignKey link) {
		Column pkColumn = link.getSource();
		Column fkColumn = link.getTarget();

		Table pkTable = pkColumn.getTable();
		Table fkTable = fkColumn.getTable();

		if (pkTable.getCluster().getProductName().equals("MySQL")) {
			return new RequestStatement("ALTER TABLE "
					+ fkTable.getSimplePath() + " ADD CONSTRAINT "
					+ link.getName() + " FOREIGN KEY " + link.getName() + "("
					+ fkColumn.getName() + ") REFERENCES " + pkTable.getName()
					+ "(" + pkColumn.getName() + ")");
		} else if (pkTable.getCluster().getProductName().equals("PostgreSQL")) {
			return new RequestStatement("ALTER TABLE "
					+ fkTable.getSimplePath() + " ADD CONSTRAINT "
					+ link.getName() + " FOREIGN KEY (" + fkColumn.getName()
					+ ") REFERENCES " + pkTable.getName() + "("
					+ pkColumn.getName() + ")");
		}

		return null;

	}
}
