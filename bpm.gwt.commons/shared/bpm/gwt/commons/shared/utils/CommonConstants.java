package bpm.gwt.commons.shared.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CommonConstants {
	public static final String SID = "sid";
	public static final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.

	public static final String PREVIEW_REPORT_SERVLET_ANALYSIS = "FreeAnalysisWeb/PreviewReport";
	public static final String PREVIEW_REPORT_SERVLET = "PreviewReport";
	public static final String PREVIEW_STREAM_SERVLET = "DownloadStreamServlet";
	public static final String VIEW_STREAM_SERVLET = "ViewStreamServlet";
	public static final String PREVIEW_DOCUMENT_SERVLET = "PreviewDocumentServlet";
	public static final String DATASOURCE_ARCHITECT_SERVLET = "DatasourceArchitectUploadServlet.gupld";
	public static final String ARCHITECT_UPLOAD_SERVLET = "ArchitectUploadServlet.gupld";
	public static final String COMMON_UPLOAD_SERVLET = "CommonUploadServlet.gupld";
	public static final String HUB_UPLOAD_SERVLET = "HubUploadServlet.gupld";
	public static final String SCHEMA_UPLOAD_SERVLET = "SchemaUploadServlet.gupld";

	public static final String STREAM_HASHMAP_NAME = "streamHashName";
	public static final String STREAM_HASHMAP_FORMAT = "streamHashFormat";
	public static final String REPORT_HASHMAP_NAME = "reportHashName";
	public static final String REPORT_OUTPUT = "output";
	public static final String REPORT_BIG_FILE = "bigFile";
	public static final String IS_IN_SESSION = "isInSession";
	
	public static final String CHECKOUT = "checkout";
	public static final String CHECKIN = "checkin";
	public static final String DOCUMENT_ID = "documentId";
	public static final String DOCUMENT_TYPE = "documentType";
	public static final String USER_ID = "userId";
	public static final String INDEX = "index";
	
	public static final double BIG_FILE = 50000000;
	
	public static final int DOCUMENT_TYPE_BACKGROUND = 0;

	public static final String FORMAT_XLS_NAME = "XLS";
	public static final String FORMAT_XLS = "xls";
	
	public static final String FORMAT_CSV_NAME = "CSV";
	public static final String FORMAT_CSV = "csv";
	
	public static final String FORMAT_WEKA_NAME = "WEKA";
	public static final String FORMAT_WEKA = "arff";

	public static final String FORMAT_PDF_NAME = "PDF";
	public static final String FORMAT_PDF = "pdf";

	public static final String FORMAT_DOCX_NAME = "DOCX";
	public static final String FORMAT_DOCX = "docx";

	public static final String FORMAT_HTML_NAME = "HTML";
	public static final String FORMAT_HTML = "html";
	
	public static final String FORMAT_R_NAME = "R";
	public static final String FORMAT_R = "R";
	
	public static final String FORMAT_RMD_NAME = "R Markdown";
	public static final String FORMAT_RMD = "Rmd";
	
	public static final String FORMAT_AUTO = "Auto";
	
	public static final String FORMAT_TXT_NAME = "TXT";
	public static final String FORMAT_TXT = "txt";
	
	public static final String FORMAT_JPEG_NAME = "JPEG";
	public static final String FORMAT_JPEG = "jpg";
	public static final String FORMAT_SVG_NAME = "SVG";
	public static final String FORMAT_SVG = "svg";
	public static final String FORMAT_PNG_NAME = "PNG";
	public static final String FORMAT_PNG = "png";
	
	public static final String FORMAT_ZIP_NAME = "ZIP";
	public static final String FORMAT_ZIP = "zip";
	
	public static final String FORMAT_LOG_NAME = "LOG";
	public static final String FORMAT_LOG = "log";
	public static final String FORMAT_RPT_DESIGN = "rptdesign";
	
	public static final String[] FORMAT_DISPLAY = {FORMAT_HTML_NAME, FORMAT_PDF_NAME, FORMAT_XLS_NAME, "XLSX", "CSV", "ODS", "DOC", FORMAT_DOCX_NAME, "ODT", "PPT < 2010", "PPT > 2010", "PPTX", "POSTSCRIPT"};
	public static final String[] FORMAT_VALUE = {FORMAT_HTML, FORMAT_PDF, FORMAT_XLS, "xlsx", "csv", "ods", "doc", FORMAT_DOCX, "odt", "ppt", "pht", "pptx", "postscript"};

	public static final String[] FORMAT_DISPLAY_FD = {FORMAT_PDF_NAME, FORMAT_DOCX_NAME};
	public static final String[] FORMAT_VALUE_FD = {FORMAT_PDF, FORMAT_DOCX};
	
	public static final String[] FORMAT_DISPLAY_CUBE = {FORMAT_PDF_NAME, FORMAT_HTML_NAME, FORMAT_XLS_NAME};
	public static final String[] FORMAT_VALUE_CUBE = {FORMAT_PDF, FORMAT_HTML, FORMAT_XLS};
	
	public static final String[] FORMAT_DISPLAY_DRILLTHROUGH = {FORMAT_XLS_NAME, FORMAT_CSV_NAME, FORMAT_WEKA_NAME};
	public static final String[] FORMAT_VALUE_DRILLTHROUGH = {FORMAT_XLS, FORMAT_CSV, FORMAT_WEKA};

	public static HashMap<String, String> jdbcLabels;
	static {
		jdbcLabels = new LinkedHashMap<String, String>();
		
		jdbcLabels.put("org.apache.derby.jdbc.ClientDriver", "Derby");
		jdbcLabels.put("org.h2.Driver", "H2");
		jdbcLabels.put("org.hsqldb.jdbcDriver", "Hypersonic");
		jdbcLabels.put("com.ingres.jdbc.IngresDriver", "Ingres");
		jdbcLabels.put("com.mysql.jdbc.Driver", "MySQL");
		jdbcLabels.put("oracle.jdbc.driver.OracleDriver", "Oracle");
		jdbcLabels.put("org.postgresql.Driver", "PostgreSql");
		jdbcLabels.put("net.sourceforge.jtds.jdbc.Driver", "SqlServer");
		jdbcLabels.put("com.teradata.jdbc.TeraDriver", "Teradata");
		jdbcLabels.put("com.vertica.Driver", "Vertica");
		jdbcLabels.put("org.mariadb.jdbc.Driver", "MariaDb");
		jdbcLabels.put("org.apache.hive.jdbc.HiveDriver", "Hive");
		
	}
}
