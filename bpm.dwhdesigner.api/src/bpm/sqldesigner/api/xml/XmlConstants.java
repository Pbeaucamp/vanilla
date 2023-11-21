package bpm.sqldesigner.api.xml;

import java.text.SimpleDateFormat;

public class XmlConstants {

	public static final String ELEMENT_ROOT = "Cluster";
		public static final SimpleDateFormat DATE_STANDARD = new SimpleDateFormat(
	"dd/MM/yyyy - HH:mm:ss");
		public static final String ATTRIBUTE_ROOT_PRODUCTNAME = "productname";
		public static final String ATTRIBUTE_ROOT_DATE = "date";

	public static final String ELEMENT_CATALOG = "Catalog";
		public static final String ATTRIBUTE_CATALOG_NAME = "name";
	
	public static final String ELEMENT_NULLSCHEMA = "NullSchema";
	
	public static final String ELEMENT_SCHEMA = "Schema";
		public static final String ATTRIBUTE_SCHEMA_NAME = "name";

	public static final String ELEMENT_TABLE = "Table";
		public static final String ATTRIBUTE_TABLE_NAME = "name";
		public static final String ATTRIBUTE_TABLE_HEIGHT = "height";
		public static final String ATTRIBUTE_TABLE_WIDTH = "width";
		public static final String ATTRIBUTE_TABLE_Y = "y";
		public static final String ATTRIBUTE_TABLE_X = "x";
		
	public static final String ELEMENT_VIEW = "View";
		public static final String ATTRIBUTE_VIEW_NAME = "name";
		
	public static final String ELEMENT_PROCEDURE = "Procedure";
		public static final String ATTRIBUTE_PROCEDURE_NAME = "name";

	public static final String ELEMENT_TYPE = "Type";
		public static final String ATTRIBUTE_TYPE_NAME = "name";
		public static final String ATTRIBUTE_TYPE_ID = "id";

	public static final String ELEMENT_COLUMN = "Column";
		public static final String ATTRIBUTE_COLUMN_NAME = "name";
		public static final String ATTRIBUTE_COLUMN_TYPE = "type";
		public static final String ATTRIBUTE_COLUMN_SIZE = "size";
		public static final String ATTRIBUTE_COLUMN_DEFAULT = "def";
		public static final String ATTRIBUTE_COLUMN_UNSIGNED = "unsign";
		public static final String ATTRIBUTE_COLUMN_NULLABLE = "null";
		public static final String ATTRIBUTE_COLUMN_PRIMARYKEY = "pk";
		public static final String ATTRIBUTE_COLUMN_TARGETPK = "tpk";
		public static final String ATTRIBUTE_COLUMN_AUTOINC = "auto_inc";
		
		
		
		
		
		
		
		
		public static final String SCHEMA_VIEW_ELEMENT = "schemaView";
		public static final String SCHEMA_ELEMENT = "schema";
		public static final String SCHEMA_NAME = "name";
		public static final String SCHEMA_ZOOM = "scale";

		public static final String CATALOG_ELEMENT = "catalog";

		public static final String TABLE_ELEMENT = "table";
		public static final String TABLE_NAME = "name";
		public static final String TABLE_BOUNDS_X = "x";
		public static final String TABLE_BOUNDS_Y = "y";
		public static final String TABLE_BOUNDS_WIDTH = "w";
		public static final String TABLE_BOUNDS_HEIGHT = "h";

		public static final String SCHEMA_DBC_ELEMENT = "connection";
		public static final String SCHEMA_DBC_HOST = "host";
		public static final String SCHEMA_DBC_PORT = "port";
		public static final String SCHEMA_DBC_DBNAME = "dbname";
		public static final String SCHEMA_DBC_LOGIN = "login";
		public static final String SCHEMA_DBC_PASS = "pass";
		public static final String SCHEMA_DBC_DRIVER = "driver";

		public static final String MP_ELEMENT = "markup_point";
		public static final String MP_FILE = "file";
		public static final String MP_SCHEMA_NAME = "name";
		public static final String MP_CATALOG_NAME = "catalog";
		public static final String MP_SCALE = "scale";
		public static final String SCHEMA_VIEW_ELEMENT_NAME = "name";
		public static final String SNAPSHOT_ELEMENT = "schemaSnapshot";
		public static final String SNAPSHOT_ELEMENT_NAME = "name";
		public static final String ATTRIBUTE_ROOT_NAME = "name";
}
