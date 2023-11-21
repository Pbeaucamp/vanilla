package bpm.sqldesigner.api.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.sqldesigner.api.database.DataBaseConnection;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.api.model.TypesList;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class SaveData {

	public static byte SAVE_VIEWS = 1;
	public static byte SAVE_PROCEDURES = 2;
	public static byte SAVE_LAYOUT = 4;

	public static boolean checkFile(String fileNameIn) {
		return new File(fileNameIn).exists();
	}

	public static void saveDatabaseCluster(DatabaseCluster databaseCluster,
			String file, int config) throws IOException {

		boolean saveViews = SAVE_VIEWS == (config & SAVE_VIEWS);
		boolean saveProcedures = SAVE_PROCEDURES == (config & SAVE_PROCEDURES);
		boolean saveLayout = SAVE_LAYOUT == (config & SAVE_LAYOUT);

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(XmlConstants.ELEMENT_ROOT);
		
		
		root.addAttribute(XmlConstants.ATTRIBUTE_ROOT_NAME,
				databaseCluster.getName());
		root.addAttribute(XmlConstants.ATTRIBUTE_ROOT_PRODUCTNAME,
				databaseCluster.getProductName());
		root.addAttribute(XmlConstants.ATTRIBUTE_ROOT_DATE,
				XmlConstants.DATE_STANDARD.format(databaseCluster.getDate()));
		
		
		
		if (databaseCluster.getDatabaseConnection() != null){
			DataBaseConnection dbc = databaseCluster.getDatabaseConnection();
			Element dbcElement = root.addElement(XmlConstants.SCHEMA_DBC_ELEMENT);
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_HOST, dbc.getHost());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PORT, dbc.getPort());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DBNAME, dbc.getDataBaseName());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_LOGIN, dbc.getLogin());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PASS, dbc.getPassword());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DRIVER, dbc.getDriverName());
		}
		
		
		saveTypes(root, databaseCluster);

		TreeMap<String, Catalog> hmCatalogs = databaseCluster.getCatalogs();

		for (String catalogName : hmCatalogs.keySet()) {
			Catalog catalog = hmCatalogs.get(catalogName);
			Element catalogElement = root.addElement(
					XmlConstants.ELEMENT_CATALOG).addAttribute(
					XmlConstants.ATTRIBUTE_CATALOG_NAME, catalog.getName());

			TreeMap<String, Schema> hmSchemas = catalog.getSchemas();

			for (String schemaName : hmSchemas.keySet()) {

				Schema schema = hmSchemas.get(schemaName);
				Element schemaElement;
				if (schema instanceof SchemaNull)
					schemaElement = catalogElement
							.addElement(XmlConstants.ELEMENT_NULLSCHEMA);
				else
					schemaElement = catalogElement.addElement(
							XmlConstants.ELEMENT_SCHEMA).addAttribute(
							XmlConstants.ATTRIBUTE_SCHEMA_NAME,
							schema.getName());

				if (saveViews)
					saveViews(schema, schemaElement);
				if (saveProcedures)
					saveProcedures(schema, schemaElement);
				saveTables(schema, schemaElement, saveLayout);
			}

		}
		/*
		 * save SchemaViews
		 */
		for(SchemaView v : databaseCluster.getSchemaViews()){

			Element viewE = root.addElement(XmlConstants.SCHEMA_VIEW_ELEMENT).addAttribute(XmlConstants.MP_SCALE, v.getScale() + "").addAttribute(XmlConstants.CATALOG_ELEMENT, v.getSchema().getCatalog().getName())
			.addAttribute(XmlConstants.SCHEMA_ELEMENT, v.getSchema().getName())
			.addAttribute(XmlConstants.SCHEMA_VIEW_ELEMENT_NAME, v.getName());
			
			
			if (databaseCluster.getDatabaseConnection() != null){
				DataBaseConnection dbc = databaseCluster.getDatabaseConnection();
				Element dbcElement = viewE.addElement(XmlConstants.SCHEMA_DBC_ELEMENT);
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_HOST, dbc.getHost());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PORT, dbc.getPort());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DBNAME, v.getSchema().getCatalog().getName());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_LOGIN, dbc.getLogin());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PASS, dbc.getPassword());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DRIVER, dbc.getDriverName());
			}
			
			for(Table t : v.getTables()){
				viewE.addElement(XmlConstants.TABLE_ELEMENT).addAttribute(XmlConstants.TABLE_NAME, t.getName())
				.addAttribute(XmlConstants.TABLE_BOUNDS_X, "" + t.getLayout()[0])
				.addAttribute(XmlConstants.TABLE_BOUNDS_Y, "" + t.getLayout()[1])
				.addAttribute(XmlConstants.TABLE_BOUNDS_WIDTH, "" + t.getLayout()[2])
				.addAttribute(XmlConstants.TABLE_BOUNDS_HEIGHT, "" + t.getLayout()[3]);
			}
			
		}
		
		/*
		 * save snapshot
		 */
		for(DocumentSnapshot v : databaseCluster.getDocumentSnapshots()){

			Element viewE = root.addElement(XmlConstants.SNAPSHOT_ELEMENT).addAttribute(XmlConstants.MP_SCALE, v.getScale() + "").addAttribute(XmlConstants.CATALOG_ELEMENT, v.getSchema().getCatalog().getName())
			.addAttribute(XmlConstants.SCHEMA_ELEMENT, v.getSchema().getName())
			.addAttribute(XmlConstants.SNAPSHOT_ELEMENT_NAME, v.getName());
			
			
			if (databaseCluster.getDatabaseConnection() != null){
				DataBaseConnection dbc = databaseCluster.getDatabaseConnection();
				Element dbcElement = viewE.addElement(XmlConstants.SCHEMA_DBC_ELEMENT);
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_HOST, dbc.getHost());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PORT, dbc.getPort());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DBNAME, v.getSchema().getCatalog().getName());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_LOGIN, dbc.getLogin());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PASS, dbc.getPassword());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DRIVER, dbc.getDriverName());
			}
			
			for(Table t : v.getTables()){
				viewE.addElement(XmlConstants.TABLE_ELEMENT).addAttribute(XmlConstants.TABLE_NAME, t.getName())
				.addAttribute(XmlConstants.TABLE_BOUNDS_X, "" + t.getLayout()[0])
				.addAttribute(XmlConstants.TABLE_BOUNDS_Y, "" + t.getLayout()[1])
				.addAttribute(XmlConstants.TABLE_BOUNDS_WIDTH, "" + t.getLayout()[2])
				.addAttribute(XmlConstants.TABLE_BOUNDS_HEIGHT, "" + t.getLayout()[3]);
			}
			
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);

		writer.write(document);

		writer.close();
		databaseCluster.setFileName(file);
	}

	private static void saveTables(Schema schema, Element schemaElement,
			boolean saveLayout) {
		TreeMap<String, Table> hmTables = schema.getTables();
		for (String tableName : hmTables.keySet()) {
			Table table = hmTables.get(tableName);
			Element tableElement = schemaElement.addElement(
					XmlConstants.ELEMENT_TABLE).addAttribute(
					XmlConstants.ATTRIBUTE_TABLE_NAME, table.getName());

			if (saveLayout) {
				if (!table.layoutIsNull()) {
					int[] layout = table.getLayout();
					tableElement.addAttribute(XmlConstants.ATTRIBUTE_TABLE_X,
							String.valueOf(layout[0]));
					tableElement.addAttribute(XmlConstants.ATTRIBUTE_TABLE_Y,
							String.valueOf(layout[1]));
					tableElement.addAttribute(
							XmlConstants.ATTRIBUTE_TABLE_WIDTH, String
									.valueOf(layout[2]));
					tableElement.addAttribute(
							XmlConstants.ATTRIBUTE_TABLE_HEIGHT, String
									.valueOf(layout[3]));
				}
			}

			HashMap<String, Column> hmColumns = table.getColumns();
			for (String columnName : hmColumns.keySet()) {
				Column column = hmColumns.get(columnName);
				saveColumn(tableElement, column);
			}
		}
	}

	private static void saveProcedures(Schema schema, Element schemaElement) {
		HashMap<String, SQLProcedure> hmProcedures = schema.getProcedures();
		Iterator<String> it = hmProcedures.keySet().iterator();
		while (it.hasNext()) {
			String procedureName = it.next();
			SQLProcedure procedure = hmProcedures.get(procedureName);
			Element procedureElement = schemaElement.addElement(
					XmlConstants.ELEMENT_PROCEDURE).addAttribute(
					XmlConstants.ATTRIBUTE_PROCEDURE_NAME, procedure.getName());
			if (procedure.getValue() != null)
				procedureElement.addText(procedure.getValue());
		}
	}

	private static void saveViews(Schema schema, Element schemaElement) {
		HashMap<String, SQLView> hmViews = schema.getViews();
		for (String viewName : hmViews.keySet()) {
			SQLView view = hmViews.get(viewName);
			Element viewElement = schemaElement.addElement(
					XmlConstants.ELEMENT_VIEW).addAttribute(
					XmlConstants.ATTRIBUTE_VIEW_NAME, view.getName());
			if (view.getValue() != null)
				viewElement.addText(view.getValue());
		}
	}

	private static void saveTypes(Element rootElement,
			DatabaseCluster databaseCluster) {
		TypesList typesList = databaseCluster.getTypesLists();

		HashMap<String, Type> hmTypes = typesList.getTypes();

		for (String typeName : hmTypes.keySet()) {
			Type type = hmTypes.get(typeName);
			rootElement.addElement(XmlConstants.ELEMENT_TYPE).addAttribute(
					XmlConstants.ATTRIBUTE_TYPE_ID,
					String.valueOf(type.getId())).addAttribute(
					XmlConstants.ATTRIBUTE_TYPE_NAME, type.getName());
		}

	}

	private static void saveColumn(Element tableElement, Column column) {
		Element columnElement = tableElement
				.addElement(XmlConstants.ELEMENT_COLUMN);
		columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_NAME, column
				.getName());
		columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_TYPE, column
				.getType().getName());
		if (column.getSize() != -1)
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_SIZE,
					String.valueOf(column.getSize()));

		if (column.gotDefault())
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_DEFAULT,
					column.getDefaultValue());
		if (column.isUnsigned())
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_UNSIGNED,
					"1");
		if (!column.isNullable())
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_NULLABLE,
					"0");
		if (column.isAutoInc())
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_AUTOINC,
					"1");
		if (column.isPrimaryKey())
			columnElement.addAttribute(
					XmlConstants.ATTRIBUTE_COLUMN_PRIMARYKEY, "1");
		if (column.isForeignKey()) {
			Column tpk = column.getTargetColumnPrimaryKey();
			columnElement.addAttribute(XmlConstants.ATTRIBUTE_COLUMN_TARGETPK,
					tpk.getTable().getName() + "." + tpk.getName());
		}
	}




	public static String saveSnapshotAsXml(String encoding, DocumentSnapshot snapshot, int config) throws IOException {

		boolean saveViews = SAVE_VIEWS == (config & SAVE_VIEWS);
		boolean saveProcedures = SAVE_PROCEDURES == (config & SAVE_PROCEDURES);
		boolean saveLayout = SAVE_LAYOUT == (config & SAVE_LAYOUT);

		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(XmlConstants.ELEMENT_ROOT);
		root.addAttribute(XmlConstants.ATTRIBUTE_ROOT_PRODUCTNAME,
				snapshot.getDatabaseCluster().getProductName());
		root.addAttribute(XmlConstants.ATTRIBUTE_ROOT_DATE,
				XmlConstants.DATE_STANDARD.format(snapshot.getDatabaseCluster().getDate()));
		
		
		if (snapshot.getDatabaseCluster().getDatabaseConnection() != null){
			
			DataBaseConnection dbc = snapshot.getDatabaseCluster().getDatabaseConnection();
			Element dbcElement = root.addElement(XmlConstants.SCHEMA_DBC_ELEMENT);
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_HOST, dbc.getHost());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PORT, dbc.getPort());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DBNAME, snapshot.getSchema().getCatalog().getName());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_LOGIN, dbc.getLogin());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PASS, dbc.getPassword());
			dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DRIVER, dbc.getDriverName());
		}
		
		
		saveTypes(root, snapshot.getDatabaseCluster());

		

		
			Catalog catalog = snapshot.getSchema().getCatalog();
			Element catalogElement = root.addElement(
					XmlConstants.ELEMENT_CATALOG).addAttribute(
					XmlConstants.ATTRIBUTE_CATALOG_NAME, catalog.getName());

			TreeMap<String, Schema> hmSchemas = catalog.getSchemas();

			for (String schemaName : hmSchemas.keySet()) {

				Schema schema = hmSchemas.get(schemaName);
				Element schemaElement;
				if (schema instanceof SchemaNull)
					schemaElement = catalogElement
							.addElement(XmlConstants.ELEMENT_NULLSCHEMA);
				else
					schemaElement = catalogElement.addElement(
							XmlConstants.ELEMENT_SCHEMA).addAttribute(
							XmlConstants.ATTRIBUTE_SCHEMA_NAME,
							schema.getName());

				if (saveViews)
					saveViews(schema, schemaElement);
				if (saveProcedures)
					saveProcedures(schema, schemaElement);
				saveTables(schema, schemaElement, saveLayout);
			}

				
		/*
		 * save snapshot
		 */
		DocumentSnapshot v = snapshot;

			Element viewE = root.addElement(XmlConstants.SNAPSHOT_ELEMENT).addAttribute(XmlConstants.MP_SCALE, v.getScale() + "").addAttribute(XmlConstants.CATALOG_ELEMENT, v.getSchema().getCatalog().getName())
			.addAttribute(XmlConstants.SCHEMA_ELEMENT, v.getSchema().getName())
			.addAttribute(XmlConstants.SNAPSHOT_ELEMENT_NAME, v.getName());
			
			
			if (v.getDatabaseCluster().getDatabaseConnection() != null){
				DataBaseConnection dbc = v.getDatabaseCluster().getDatabaseConnection();
				Element dbcElement = viewE.addElement(XmlConstants.SCHEMA_DBC_ELEMENT);
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_HOST, dbc.getHost());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PORT, dbc.getPort());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DBNAME, dbc.getDataBaseName());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_LOGIN, dbc.getLogin());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_PASS, dbc.getPassword());
				dbcElement.addAttribute(XmlConstants.SCHEMA_DBC_DRIVER, dbc.getDriverName());
			}
			
			for(Table t : v.getTables()){
				viewE.addElement(XmlConstants.TABLE_ELEMENT).addAttribute(XmlConstants.TABLE_NAME, t.getName())
				.addAttribute(XmlConstants.TABLE_BOUNDS_X, "" + t.getLayout()[0])
				.addAttribute(XmlConstants.TABLE_BOUNDS_Y, "" + t.getLayout()[1])
				.addAttribute(XmlConstants.TABLE_BOUNDS_WIDTH, "" + t.getLayout()[2])
				.addAttribute(XmlConstants.TABLE_BOUNDS_HEIGHT, "" + t.getLayout()[3]);
			}
			
		

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setTrimText(false);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		XMLWriter writer = new XMLWriter(bos, format);

		writer.write(document);

		writer.close();
		
		return bos.toString(encoding);
		
	}
}
