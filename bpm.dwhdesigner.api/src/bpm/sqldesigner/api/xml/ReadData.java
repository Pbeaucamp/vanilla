package bpm.sqldesigner.api.xml;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bpm.sqldesigner.api.database.DataBaseConnection;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.exception.MalformedXMLException;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class ReadData {

	public static DatabaseCluster readCatalogsList(String file)
			throws Exception {
		File staticXML = new File(file);
		SAXReader saxReader = new SAXReader();

		Document document;

		document = saxReader.read(staticXML);

		DatabaseCluster databaseCluster = new DatabaseCluster();
		Element root = document.getRootElement();
		databaseCluster.setProductName(root
				.attributeValue(XmlConstants.ATTRIBUTE_ROOT_PRODUCTNAME));

		String dateString = root
				.attributeValue(XmlConstants.ATTRIBUTE_ROOT_DATE);
		Date date;
		try {
			date = XmlConstants.DATE_STANDARD.parse(dateString);
		} catch (ParseException e) {
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. " + "Can't extract the date. "
					+ e.getMessage());
		}
		databaseCluster.setDate(date);
		databaseCluster.setNotFullLoaded(false);

		databaseCluster.setFileName(file);
		databaseCluster.setName(root.attributeValue(XmlConstants.ATTRIBUTE_ROOT_NAME, null));
		Iterator<?> it = root.elementIterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			/*
			 * parse connection
			 */
			if (element.getQualifiedName().equals(XmlConstants.SCHEMA_DBC_ELEMENT)) {
				
				DataBaseConnection dbc = databaseCluster.getDatabaseConnection() ;
				if ( dbc == null){
					dbc = new DataBaseConnection();
					databaseCluster.setDatabaseConnection(dbc);
				}
				
				dbc.setHost(element.attributeValue(XmlConstants.SCHEMA_DBC_HOST));
				dbc.setDataBaseName(element.attributeValue(XmlConstants.SCHEMA_DBC_DBNAME));
				dbc.setDriverName(element.attributeValue(XmlConstants.SCHEMA_DBC_DRIVER));
				dbc.setLogin(element.attributeValue(XmlConstants.SCHEMA_DBC_LOGIN));
				dbc.setPassword(element.attributeValue(XmlConstants.SCHEMA_DBC_PASS));
				dbc.setPort(element.attributeValue(XmlConstants.SCHEMA_DBC_PORT));
				
			}
			/*
			 * parse catalog
			 */
			if (element.getQualifiedName().equals(XmlConstants.ELEMENT_CATALOG)) {
				Catalog catalog = new Catalog();
				catalog.setNotFullLoaded(false);
				catalog.setName(element
						.attributeValue(XmlConstants.ATTRIBUTE_CATALOG_NAME));
				if (catalog.getName() == null)
					throw new MalformedXMLException("XML file :'" + file
							+ "' is malformed. " + "A catalog name is null.");

				databaseCluster.addCatalog(catalog);
				catalog.setDatabaseCluster(databaseCluster);

				HashMap<String, ListPair> waitedTables = new HashMap<String, ListPair>();

				Iterator<?> itSchema = element.elementIterator();
				while (itSchema.hasNext()) {
					Element schemaElement = (Element) itSchema.next();
					Schema schema = null;
					if (schemaElement.getQualifiedName().equals(
							XmlConstants.ELEMENT_NULLSCHEMA)) {
						schema = new SchemaNull();
					} else if (schemaElement.getQualifiedName().equals(
							XmlConstants.ELEMENT_SCHEMA)) {
						schema = new Schema();
						schema
								.setName(schemaElement
										.attributeValue(XmlConstants.ATTRIBUTE_SCHEMA_NAME));
						if (schema.getName() == null)
							throw new MalformedXMLException(
									"XML file :'"
											+ file
											+ "' is malformed. A schema name is null in catalog '"
											+ catalog.getName() + "'.");

					} else
						throw new MalformedXMLException("XML file :'" + file
								+ "' is malformed. Element "
								+ schemaElement.getQualifiedName()
								+ " found as a schema");

					schema.setNotFullLoaded(false);

					schema.setCatalog(catalog);
					catalog.addSchema(schema);

					Iterator<?> itTable = schemaElement.elementIterator();
					while (itTable.hasNext()) {
						Element tableElement = (Element) itTable.next();

						if (tableElement.getQualifiedName().equals(
								XmlConstants.ELEMENT_TABLE)) {
							Table table = new Table();
							table
									.setName(tableElement
											.attributeValue(XmlConstants.ATTRIBUTE_TABLE_NAME));
							if (table.getName() == null)
								throw new MalformedXMLException(
										"XML file :'"
												+ file
												+ "' is malformed.  A table name is null in schema '"
												+ catalog.getName() + "."
												+ schema.getName() + "'.");

							String xString = tableElement
									.attributeValue(XmlConstants.ATTRIBUTE_TABLE_X);

							if (xString != null) {
								int y = Integer
										.valueOf(tableElement
												.attributeValue(XmlConstants.ATTRIBUTE_TABLE_Y));
								int width = Integer
										.valueOf(tableElement
												.attributeValue(XmlConstants.ATTRIBUTE_TABLE_WIDTH));
								int height = Integer
										.valueOf(tableElement
												.attributeValue(XmlConstants.ATTRIBUTE_TABLE_HEIGHT));
								table.setLayout(Integer.valueOf(xString), y,
										width, height);
							}

							table.setNotFullLoaded(false);

							table.setSchema(schema);
							schema.addTable(table);

							Iterator<?> itColumn = tableElement
									.elementIterator();
							while (itColumn.hasNext()) {
								Element columnElement = (Element) itColumn
										.next();

								if (columnElement.getQualifiedName().equals(
										XmlConstants.ELEMENT_COLUMN))
									readColumn(columnElement, file, catalog,
											schema, table, waitedTables);
							}
						} else if (tableElement.getQualifiedName().equals(
								XmlConstants.ELEMENT_VIEW)) {
							SQLView view = new SQLView();
							view.setNotFullLoaded(false);

							view
									.setName(tableElement
											.attributeValue(XmlConstants.ATTRIBUTE_VIEW_NAME));
							view.setSchema(schema);
							schema.addView(view);

							if (!tableElement.getText().equals(""))
								view.setValue(tableElement.getText());

						} else if (tableElement.getQualifiedName().equals(
								XmlConstants.ELEMENT_PROCEDURE)) {
							SQLProcedure procedure = new SQLProcedure();

							procedure
									.setName(tableElement
											.attributeValue(XmlConstants.ATTRIBUTE_PROCEDURE_NAME));
							procedure.setSchema(schema);
							schema.addProcedure(procedure);

							if (!tableElement.getText().equals(""))
								procedure.setValue(tableElement.getText());
							procedure.setNotFullLoaded(false);

						}
					}
				}
			} else if (element.getQualifiedName().equals(
					XmlConstants.ELEMENT_TYPE)) {
				readType(element, file, databaseCluster);
			}

		}
		
		
		for(Element e : (List<Element>)root.elements()){
			if (e.getName().equals(XmlConstants.SCHEMA_VIEW_ELEMENT)){
				String catalogName =  e.attributeValue(XmlConstants.CATALOG_ELEMENT);
				String schemaName =  e.attributeValue(XmlConstants.SCHEMA_ELEMENT);
				
				Catalog c = databaseCluster.getCalalog(catalogName);
				Schema s = c.getSchema(schemaName);
				
				SchemaView view = null;
				if (s != null){
					view = new SchemaView(s);
				}
				else{
					view = new SchemaView(c);
				}
				
				
				view.setName(e.attributeValue(XmlConstants.SCHEMA_VIEW_ELEMENT_NAME));
				view.setScale(Float.parseFloat(e.attributeValue(XmlConstants.MP_SCALE)));
				for(Element t : (List<Element>)e.elements(XmlConstants.TABLE_ELEMENT)){
					Table table = view.getSchema().getTable(t.attributeValue(XmlConstants.TABLE_NAME));
					int[] oldLayout = table.getLayout();
					table.setLayout(Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_X)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_Y)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_WIDTH)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_HEIGHT)));
					table.setLayout(oldLayout[0], oldLayout[1], oldLayout[2], oldLayout[3]);
				}
				
				databaseCluster.addSchemaView(view);
			}
		}
		
		for(Element e : (List<Element>)root.elements()){
			if (e.getName().equals(XmlConstants.SNAPSHOT_ELEMENT)){
				String catalogName =  e.attributeValue(XmlConstants.CATALOG_ELEMENT);
				String schemaName =  e.attributeValue(XmlConstants.SCHEMA_ELEMENT);
				
				Catalog c = databaseCluster.getCalalog(catalogName);
				Schema s = c.getSchema(schemaName);
				
				DocumentSnapshot view = new DocumentSnapshot();
				
				if (s != null){
					view.setSchema(s);
				}
				else{
					view.setSchema(c.getSchema("null"));
				}
				
				view.setName(e.attributeValue(XmlConstants.SNAPSHOT_ELEMENT_NAME));
				view.setScale(Float.parseFloat(e.attributeValue(XmlConstants.MP_SCALE)));
				for(Element t : (List<Element>)e.elements(XmlConstants.TABLE_ELEMENT)){
					Table table = view.getSchema().getTable(t.attributeValue(XmlConstants.TABLE_NAME));
					int[] oldLayout = table.getLayout();
					table.setLayout(Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_X)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_Y)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_WIDTH)), 
									Integer.parseInt(t.attributeValue(XmlConstants.TABLE_BOUNDS_HEIGHT)));
					view.addTable(table);
					table.setLayout(oldLayout[0], oldLayout[1], oldLayout[2], oldLayout[3]);
				}
				view.setDatabaseCluster(databaseCluster);
				view.rebuildLinks();
				databaseCluster.addDocumentSnapshot(view);
			}
		}
		
		return databaseCluster;
	}

	private static void readColumn(Element columnElement, String file,
			Catalog catalog, Schema schema, Table table,
			HashMap<String, ListPair> waitedTables)
			throws MalformedXMLException {
		Column column = new Column();
		column.setNotFullLoaded(false);
		column.setName(columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_NAME));

		if (column.getName() == null)
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. A column name is null in table '"
					+ catalog.getName() + "." + schema.getName() + "."
					+ table.getName() + "'.");

		column.setTable(table);
		table.addColumn(column);

		String typeName = columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_TYPE);
		if (typeName == null)
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. Column type is null in column '"
					+ catalog.getName() + "." + schema.getName() + "."
					+ table.getName() + "." + column.getName() + "'.");

		if (waitedTables.keySet().contains(column.getName())) {
			Column cForeignKey = waitedTables.get(column.getName())
					.containsTable(table.getName());

			if (cForeignKey != null) {
				LinkForeignKey link = new LinkForeignKey(cForeignKey, column);

				column.addSourceForeignKey(link);
				cForeignKey.setTargetPrimaryKey(link);
			}

		}

		Type type = table.getSchema().getCatalog().getDatabaseCluster()
				.getTypesLists().getType(typeName);
		column.setType(type);
		if (type == null)
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. Type '" + typeName + "' in column '"
					+ catalog.getName() + "." + schema.getName() + "."
					+ table.getName() + "." + column.getName()
					+ "' can't be found.");

		String size = columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_SIZE);
		if (size != null)
			column.setSize(Integer.valueOf(size));

		if (columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_UNSIGNED) != null)
			column.setUnsigned(true);

		String defaultValue = columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_DEFAULT);
		if (defaultValue != null)
			column.setDefaultValue(defaultValue);

		if (columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_NULLABLE) != null)
			column.setNullable(false);

		if (columnElement.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_AUTOINC) != null)
			column.setAutoInc(true);

		if (columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_PRIMARYKEY) != null)
			column.setPrimaryKey(true);

		String targetPrimaryKey = columnElement
				.attributeValue(XmlConstants.ATTRIBUTE_COLUMN_TARGETPK);
		if (targetPrimaryKey != null) {
			String[] splittedArgs = targetPrimaryKey.split("\\.");
			if (splittedArgs.length == 2) {
				Table tTable = schema.getTable(splittedArgs[0]);

				if (tTable == null) {
					Pair tableNameColumn = new Pair(splittedArgs[0], column);
					if (waitedTables.get(splittedArgs[1]) == null) {
						waitedTables.put(splittedArgs[1], new ListPair());
					}
					waitedTables.get(splittedArgs[1]).add(tableNameColumn);
				}

				else {

					Column pkColumn = tTable.getColumn(splittedArgs[1]);

					if (pkColumn == null)
						throw new MalformedXMLException("XML file :'" + file
								+ "' is malformed. In primary key of '"
								+ catalog.getName() + "." + schema.getName()
								+ "." + table.getName() + "."
								+ column.getName() + "', targeted column '"
								+ tTable.getName() + '.' + splittedArgs[1]
								+ "' can't be found.");

					LinkForeignKey link = new LinkForeignKey(column, pkColumn);

					pkColumn.addSourceForeignKey(link);
					column.setTargetPrimaryKey(link);

				}
			} else
				throw new MalformedXMLException("XML file :'" + file
						+ "' is malformed. In primary key of '"
						+ catalog.getName() + "." + schema.getName() + "."
						+ table.getName() + "." + column.getName()
						+ "', malformed targeted primary key.");
		}
	}

	private static void readType(Element rootElement, String file,
			DatabaseCluster databaseCluster) throws MalformedXMLException {
		Type type = new Type();

		try {
			type.setId(Integer.valueOf(rootElement
					.attributeValue(XmlConstants.ATTRIBUTE_TYPE_ID)));

		} catch (NumberFormatException e) {
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. Type ID is missing'");
		}
		type.setName(rootElement.attributeValue("name"));

		if (type.getName() == null)
			throw new MalformedXMLException("XML file :'" + file
					+ "' is malformed. Type '" + type.getId()
					+ "' has null name.");

		databaseCluster.getTypesLists().addType(type);
	}

	private static class Pair {
		public String first;
		public Column second;

		public Pair(String first, Column second) {
			this.first = first;
			this.second = second;
		}
	}

	private static class ListPair {
		private List<Pair> pairs = new ArrayList<Pair>();

		public void add(Pair p) {
			pairs.add(p);
		}

		public Column containsTable(String tableName) {

			boolean found = false;
			Iterator<Pair> it = pairs.iterator();
			Pair p = null;
			while (it.hasNext() && !found) {
				p = it.next();
				found = p.first.equals(tableName);
			}

			if (found)
				return p.second;
			else
				return null;
		}
	}
}
