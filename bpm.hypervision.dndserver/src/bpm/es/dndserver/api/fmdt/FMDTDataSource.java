package bpm.es.dndserver.api.fmdt;

public class FMDTDataSource {
	
	private String dataSourceName;
	
	private String user;
	private String pass;
	private String url;
	private int dirItemId;
	private String businessModel;
	private String businessPackage;
	private String groupName;
	private String connectionName;
	
	public FMDTDataSource() {
	
	}

	public String getDataSourceName() {
		return dataSourceName;
	}
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDirItemId() {
		return dirItemId;
	}

	public void setDirItemId(int dirItemId) {
		this.dirItemId = dirItemId;
	}

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public String getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessPackage(String businessPackage) {
		this.businessPackage = businessPackage;
	}

//	public String getGroupName() {
//		return groupName;
//	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	
	
}
