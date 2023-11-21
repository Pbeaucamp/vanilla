package bpm.fwr.client.utils;

public final class DatasourceType {
	
	public static final String JDBC = "JDBC Data Source";
	public static final String XLS = "Excel Data Source";
	public static final String GOOGLE_SPREADSHEET = "Google Spread Sheet Data Source";
	public static final String FMDT = "FreeMetaData Data Source";
	
	public static final String[] getDatasourceTypes() {
		return new String[]{JDBC, XLS, GOOGLE_SPREADSHEET, FMDT};
	}
}
