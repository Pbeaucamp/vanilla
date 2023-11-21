package bpm.fa.api.connection;

public class XMLAConnection {
	private String url; //mandatory
	
	private String user;
	private String pass;
	
	//mandatory but fill later
	private String datasource;
	private String schema;
	
	private String cube;
	
	private String provider;
	
	public static final String MondrianProvider = "mondrian";
	public static final String MicrosoftProvider = "Mircrosoft Xmla";
	public static final String HyperionProvider = "Hyperion 9.3.1";
	public static final String QuartetFsProvider = "ActivePivot";
	public static final String IcCube = "icCube";
	
	public XMLAConnection() {
		
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * equivalent to getCatalog
	 * @return
	 */
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCube() {
		return cube;
	}

	public void setCube(String cube) {
		this.cube = cube;
	}
}
