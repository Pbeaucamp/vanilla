package bpm.sqldesigner.api.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.api.model.TypesList;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class ExtractData {

//	private static Logger logger = null;
//	
//	static{
//		logger = Logger.getLogger(ExtractData.class);
//		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
//		logger.setLevel(Level.DEBUG);
//	}
	
	private static final CharSequence UNSIGNED = " UNSIGNED";

	public static DatabaseCluster extractCatalogs( DatabaseCluster databaseCluster) throws SQLException {
//		logger.debug("-- extract catalog");
		
		
		ResultSet rs = null;
		DatabaseMetaData dbmd = null;
		SQLException error = null;
		try{
			dbmd = databaseCluster.getDatabaseConnection().getSocket().getMetaData();
			rs = dbmd.getCatalogs();
			databaseCluster.setProductName(dbmd.getDatabaseProductName());

			while (rs.next()) {
				Catalog c = new Catalog();
				c.setName(rs.getString(1));
				c.setDatabaseCluster(databaseCluster);

				if (databaseCluster.getProductName().equals("MySQL")) {
					SchemaNull schemaNull = new SchemaNull();
					schemaNull.setCatalog(c);
					c.addSchema(schemaNull);
				}
				databaseCluster.addCatalog(c);

				if (databaseCluster.getProductName().equals("Microsoft SQL Server"))
					if (!c.getName().equals(
							databaseCluster.getDatabaseConnection().getSocket()
									.getCatalog()))
						c.setNotFullLoaded(false);
			}
		}catch(SQLException ex){
			ex.printStackTrace();
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			
			if (error != null){
				throw error;
			}
		}
		

		return databaseCluster;
	}

	public static void extractSchemas(Catalog catalog) throws SQLException {
//		logger.debug("-- extract extract Schemas for " + catalog.dumpName());
		ResultSet rs = null;
		SQLException error = null;
		try{
			rs = catalog.getDatabaseCluster().getDatabaseConnection()
			.getSocket().getMetaData().getSchemas();
			if (catalog.getDatabaseCluster().getProductName().equals(
			"Microsoft SQL Server"))
		if (!catalog.getName().equals(
				catalog.getDatabaseCluster().getDatabaseConnection()
						.getSocket().getCatalog()))
			return;

			while (rs.next()) {
				Schema s = new Schema();
				s.setName(rs.getString(1));
		
				Catalog c;
				if (catalog.getDatabaseCluster().getProductName().equals(
						"PostgreSQL"))
					c = catalog;
				else if (catalog.getDatabaseCluster().getProductName().equals(
						"Microsoft SQL Server"))
					c = catalog;
				else if (catalog.getDatabaseCluster().getProductName().equals(
						"Oracle"))
					c = catalog;
				else
					c = catalog.getDatabaseCluster().getCalalog(rs.getString(2));
				s.setCatalog(c);
				c.addSchema(s);
			}
		}catch(SQLException ex){
			ex.printStackTrace();
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
				
			}
			
			if (error != null){
				throw error;
			}
		}
		
		
	}

	public static void extractProcedures(Schema schema) throws SQLException {
//		logger.debug("-- extract procedure for " + schema.dumpName());
		ResultSet rs = null;
		SQLException error = null;
		if (schema instanceof SchemaNull)
			rs = schema.getDatabaseConnection().getSocket().getMetaData()
					.getProcedures(schema.getCatalog().getName(), "%", "%");
		else
			rs = schema.getDatabaseConnection().getSocket().getMetaData()
					.getProcedures(schema.getCatalog().getName(),
							schema.getName(), "%");

		
		try{
			while (rs.next()) {
				SQLProcedure procedure = null;
				try {
					procedure = (SQLProcedure) Class.forName(
							"bpm.sqldesigner.api.model.procedure."
									+ schema.getCluster().getProductName()
									+ "Procedure").newInstance();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					procedure = new SQLProcedure();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

					procedure.setName(rs.getString(3));
					procedure.setType(Integer.valueOf(rs.getString(8)));

					procedure.setSchema(schema);
					schema.addProcedure(procedure);

					if (!procedure.getCreateStatement().equals("")) {
						Statement statement = null;
						ResultSet rsProcedure = null;
						
						try{
							statement = schema.getDatabaseConnection().getSocket().createStatement();
							rsProcedure = statement.executeQuery(procedure.getCreateStatement());

							rsProcedure.next();

							procedure.setValue(rsProcedure.getString(procedure
									.getProcedureValueColumn()));

						}finally{
							statement.close();
							rsProcedure.close();
						}
					}
				}
			}
		}catch(SQLException ex){
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			if(error != null){
				throw error;
			}
		}
		
		
	}

	public static void extractTablesAndViews(Schema schema) throws SQLException {
//		logger.debug("-- extract table and views for " + schema.dumpName());
		ResultSet rs = null;
		SQLException error = null;
		try{
			if (schema instanceof SchemaNull)
				rs = schema.getDatabaseConnection().getSocket().getMetaData()
						.getTables(schema.getCatalog().getName(), null, "%", null);
			else
				rs = schema.getDatabaseConnection().getSocket().getMetaData()
						.getTables(schema.getCatalog().getName(), schema.getName(),
								"%", null);

			while (rs.next()) {

				if (rs.getString(4).equals("TABLE")) {
					Table t = new Table();
					t.setName(rs.getString(3));
					t.setSchema(schema);
					schema.addTable(t);
				} else if (rs.getString(4).equals("VIEW")) {

					SQLView view = null;
					try {
						view = (SQLView) Class.forName(
								"bpm.sqldesigner.api.model.view."
										+ schema.getCatalog().getDatabaseCluster()
												.getProductName() + "View")
								.newInstance();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						view = new SQLView();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (view != null) {
						view.setName(rs.getString(3));
						view.setSchema(schema);

						if (!view.getStatementString().equals("")) {
							Statement statement = null;
							ResultSet rsView = null;
							
							try{
								statement = schema.getDatabaseConnection().getSocket().createStatement();
								rsView = statement.executeQuery(view.getStatementString());

								rsView.next();
								view.setValue(rsView.getString(view.getViewValueColumn()));

							}finally{
								if (rsView != null){
									rsView.close();
								}
								if (statement != null){
									statement.close();
								}
							}
						}

						schema.addView(view);
					}
				}
			}
		}catch(SQLException ex){
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			
			if (error != null){
				throw error;
			}
		}

		
	}

	public static void extractTypes(DatabaseCluster databaseCluster)
			throws SQLException {
//		logger.debug("-- extract types for " + databaseCluster.dumpName());
		ResultSet rs = null;
		SQLException error = null;
		try{
			rs = databaseCluster.getDatabaseConnection().getSocket().getMetaData().getTypeInfo();

			TypesList typesList = databaseCluster.getTypesLists();
			while (rs.next()) {
				Type t = new Type();
				t.setName(rs.getString(1).toUpperCase());
				t.setId(Integer.valueOf(rs.getString(2)));
				typesList.addType(t);
			}

		}catch(SQLException ex){
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			if (error != null){
				throw error;
			}
		}
		
	}

	public static void extractColumns(Table table) throws SQLException {
		
//		logger.debug("-- extract columns for " + table.dumpName());
		ResultSet rs = null;
		ResultSet keys = null;
		SQLException error = null;
		
		
		try{
			if (table.getSchema() instanceof SchemaNull) {
				rs = table.getDatabaseConnection().getSocket().getMetaData()
						.getColumns(table.getSchema().getCatalog().getName(), null,
								table.getName(), null);
				keys = table.getDatabaseConnection().getSocket().getMetaData()
						.getPrimaryKeys(table.getSchema().getCatalog().getName(),
								null, table.getName());

			} else {
				rs = table.getDatabaseConnection().getSocket().getMetaData()
						.getColumns(table.getSchema().getCatalog().getName(),
								table.getSchema().getName(), table.getName(), null);
				keys = table.getDatabaseConnection().getSocket().getMetaData()
						.getPrimaryKeys(table.getSchema().getCatalog().getName(),
								table.getSchema().getName(), table.getName());

			}

			List<String> listKeys = new ArrayList<String>();

			while (keys.next())
				listKeys.add(keys.getString(4));

			TypesList typesList = table.getSchema().getCatalog()
					.getDatabaseCluster().getTypesLists();

			while (rs.next()) {
				Column column = new Column();

				column.setName(rs.getString(4));
				String type = rs.getString(6).toUpperCase();

				if (type.contains(UNSIGNED)) {
					column.setUnsigned(true);
					type = type.substring(0, type.length() - UNSIGNED.length());
				}

				column.setType(typesList.getType(type));
				try {
					column.setSize(Integer.valueOf(rs.getString(7)));
				} catch (NumberFormatException e) {
					column.setSize(0);
				}

				try {
					if (rs.getString(23).equals("YES"))
						column.setAutoInc(true);
				} catch (SQLException e) {

				}

				String nullableString = rs.getString(11);
				if (nullableString.equals(0))
					column.setNullable(false);

				column.setDefaultValue(rs.getString(13));
				if (listKeys.contains(column.getName()))
					column.setPrimaryKey(true);

				column.setTable(table);
				table.addColumn(column);
			}
		}catch(SQLException ex){
			error = ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			if (keys != null){
				keys.close();
			}
			if (error != null){
				throw error;
			}
		}
		
		
	}

	public static void extractForeignKeys(Table t) throws SQLException {
		
//		logger.debug("-- extract foreignKey for " + t.dumpName());
		
		ResultSet rs = null;

		SQLException error = null;
		try{
			if (t.getSchema() instanceof SchemaNull)
				rs = t.getDatabaseConnection().getSocket().getMetaData()
						.getExportedKeys(t.getSchema().getCatalog().getName(),
								null, t.getName());
			else
				rs = t.getDatabaseConnection().getSocket().getMetaData()
						.getExportedKeys(t.getSchema().getCatalog().getName(),
								t.getSchema().getName(), t.getName());

			while (rs.next()) {

				Table pkTable = t.getSchema().getTable(rs.getString(3));
				Table fkTable = t.getSchema().getTable(rs.getString(7));

				Column pkColumn = pkTable.getColumn(rs.getString(4));
				Column fkColumn = fkTable.getColumn(rs.getString(8));

				LinkForeignKey link = new LinkForeignKey(fkColumn, pkColumn);

				fkColumn.setTargetPrimaryKey(link);
				pkColumn.addSourceForeignKey(link);
			}
		}catch(SQLException ex){
			error =ex;
		}finally{
			if (rs != null){
				rs.close();
			}
			
			if (error != null){
				throw error;
			}
		}
		
	
	}

	public static DatabaseCluster extractAll(DatabaseCluster databaseCluster)
			throws SQLException {
		extractCatalogs(databaseCluster).getCatalogs();
		extractTypes(databaseCluster);

		extractAllCatalogsChildren(databaseCluster);

		return databaseCluster;
	}

	public static DatabaseCluster extractAllCatalogsChildren(
			DatabaseCluster databaseCluster) throws SQLException {

		TreeMap<String, Catalog> hmCatalogs = databaseCluster.getCatalogs();

		for (String catalogName : hmCatalogs.keySet()) {
			Catalog catalog = hmCatalogs.get(catalogName);
			extractSchemas(catalog);

			TreeMap<String, Schema> hmSchemas = catalog.getSchemas();
			for (String schemaName : hmSchemas.keySet()) {
				Schema schema = hmSchemas.get(schemaName);
				extractTablesAndViews(schema);
				extractProcedures(schema);

				TreeMap<String, Table> hmTables = schema.getTables();

				for (String tableName : hmTables.keySet()) {
					final Table table = hmTables.get(tableName);
					extractColumns(table);
				}

				for (String tableName : hmTables.keySet()) {
					final Table table = hmTables.get(tableName);
					extractForeignKeys(table);
				}
			}
		}
		return databaseCluster;
	}

	public static Node extractWhenNotLoaded(Node node) throws SQLException {
		
//		logger.debug("-- extract not loaded for " + node.dumpName());
		Object[] listObjChild = node.getChildren();

		if (listObjChild != null) {
			if (node.isNotFullLoaded() && listObjChild.length == 0) {
				Catalog catalog = null;

				if (node instanceof Catalog) {
					catalog = (Catalog) node;
					extractSchemas(catalog);
				}
				if (node instanceof Schema) {
					Schema schema = (Schema) node;
					extractTablesAndViews(schema);
					extractProcedures(schema);
				}
				if (node instanceof Table) {
					Table table = (Table) node;
					extractColumns(table);
				}

				if (catalog != null) {
					listObjChild = catalog.getChildrenWithSchemaNull();
				} else
					listObjChild = node.getChildren();

			}

			for (Object objChild : listObjChild) {
				Node nodeChild = (Node) objChild;
				if (nodeChild.isNotFullLoaded()) {
					extractWhenNotLoaded(nodeChild);
					nodeChild.setNotFullLoaded(false);
				}
			}

			if (node instanceof Schema) {
				Schema schema = (Schema) node;
				TreeMap<String, Table> hmTables = schema.getTables();

				for (String tableName : hmTables.keySet()) {
					final Table table = hmTables.get(tableName);
					extractForeignKeys(table);
				}
			}

		}
		node.setNotFullLoaded(false);

		return node;
	}

	

}
