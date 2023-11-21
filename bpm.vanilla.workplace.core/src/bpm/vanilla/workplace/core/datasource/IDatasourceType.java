package bpm.vanilla.workplace.core.datasource;

public interface IDatasourceType {	
	
	//Type DATASOURCE_REPOSITORY
	public static final String FWR_ODA = "bpm.fwr.oda.runtime";
	
	//Type DATASOURCE_REPOSITORY
	public static final String LIST_DATA = "bpm.vanilla.listdata.oda.driver";
	
	//Type DATASOURCE_REPOSITORY
	public static final String METADATA = "bpm.metadata.birt.oda.runtime";
	
	//Type DATASOURCE_REPOSITORY
	public static final String METADATA_OLAP = "bpm.metadata.birt.oda.runtime.olap";
	
	//Type DATASOURCE_REPOSITORY
	public static final String CSV_ODA = "bpm.csv.oda.runtime";
	
	//Type DATASOURCE_REPOSITORY
	public static final String EXCEL_ODA = "bpm.excel.oda.runtime";
	
	//Type DATASOURCE_REPOSITORY
	public static final String FUSION_MAP = "bpm.vanilla.map.oda.runtime";
	
	
	
	//Type DATASOURCE_JDBC
	public static final String JDBC = "org.eclipse.birt.report.data.oda.jdbc";
	
	//Type DATASOURCE_JDBC
	public static final String FM = "bpm.fm.oda.driver";
	
	
	public static final String[] DATASOURCE_REPOSITORY = {FWR_ODA, LIST_DATA, METADATA, CSV_ODA, EXCEL_ODA, METADATA_OLAP};
	
	public static final String[] DATASOURCE_JDBC = {JDBC, FM};
	
	
	/**
	 * 
	 * This is a list of properties we can find for Birt Datasource
	 *
	 */
	
	//JDBC
	public static final String ODA_DRIVER = "odaDriverClass";
	public static final String ODA_URL = "odaURL";
	public static final String ODA_USER = "odaUser";
	public static final String ODA_PASSWORD = "odaPassword";
	
	public static final String DRIVER = "driverName";
	public static final String HOST = "host";
	public static final String PORT = "portNumber";
	public static final String DB_NAME = "dataBaseName";
	public static final String USE_FULL_URL = "useFullUrl";
	public static final String FULL_URL = "fullUrl";
	public static final String USER = "username";
	public static final String PASSWORD = "password";
	
	//BIRT_METADATA
	public static final String METADATA_USER = "USER";
	public static final String METADATA_PASSWORD = "PASSWORD";
	public static final String METADATA_URL = "URL";
	public static final String METADATA_DIRECTORY_ITEM_ID = "DIRECTORY_ITEM_ID";
	public static final String METADATA_VANILLA_URL = "VANILLA_URL";
	public static final String METADATA_REPOSITORY_ID = "REPOSITORY_ID";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String CONNECTION_NAME = "CONNECTION_NAME";
	public static final String BUSINESS_MODEL = "BUSINESS_MODEL";
	public static final String BUSINESS_PACKAGE = "BUSINESS_PACKAGE";
	
	//BIRT_FWR
	public static final String FWR_REPORT_ID = "FWREPORT_ID";
	public static final String FWR_USER = "USER";
	public static final String FWR_PASSWORD = "PASSWORD";
	public static final String FWR_URL = "URL";
	public static final String FWR_REPOSITORY_URL = "REPOSITORY_URL";
	public static final String FWR_GROUP_ID = "GROUP_ID";
	public static final String FWR_GROUP_NAME = "GROUP_NAME";
	
	//BIRT_CSV_AND_EXCEL
	public static final String CSV_REPOSITORY_ID = "repository.id";
	public static final String CSV_REPOSITORY_ITEM_ID = "repository.item.id";
	public static final String CSV_GROUP_ID = "vanilla.group.id";
	public static final String CSV_USER = "repository.user";
	public static final String CSV_PASSWORD = "repository.password";
	
	//OLAP
	public static final String OLAP_FASD_ID = "fasdid";
	public static final String OLAP_REPOSITORY_ID = "repositoryid";
	public static final String OLAP_USER = "user";
	public static final String OLAP_PASSWORD = "password";
	public static final String OLAP_GROUP_NAME = "groupname";
	public static final String OLAP_GROUP_ID = "groupid";
	
	//LIST DATA
	public static final String LIST_DATA_USER = "vanillaLogin";
	public static final String LIST_DATA_PASSWORD = "vanillaPassword";
	public static final String LIST_DATA_RUNTIME_URL = "vanillaUrl";
	public static final String LIST_DATA_REPOSITORY_ID = "vanillaRepositoryId";
	
	//FDDICO
	public static final String NAME_FDDICO_DEPENDANCIES = "fdDicoDependanciesName";
	
	//FD
	public static final String NAME_FD_PROJECT_DESCRIPTOR = "fdProjectDescriptorName";
	public static final String NAME_FD_DEPENDANCIES = "fdDependanciesName";
	
	//FM
	public static final String FM_USER = "fmLogin";
	public static final String FM_PASSWORD = "freemetricsPassword";
	
	//VANILLA MAP
	public static final String MAP_RUNTIME_URL = "bpm.vanilla.map.oda.runtime.vanillaRuntimeServerUrl";
	
	public enum DatasourceType {
		DATASOURCE_OTHER(0),
		DATASOURCE_JDBC(1),
		DATASOURCE_PATH(2),
		DATASOURCE_REPOSITORY(3);
		
		private int type;
		
		private DatasourceType(int type) {
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	}
}
