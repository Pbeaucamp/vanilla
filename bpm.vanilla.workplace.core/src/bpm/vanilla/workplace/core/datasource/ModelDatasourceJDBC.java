package bpm.vanilla.workplace.core.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table (name = "rpy_relational_datasources_jdbc")
@PrimaryKeyJoinColumn(name="dbId")
public class ModelDatasourceJDBC extends AbstractDatasource {
	
	@Column(name = "driver")
	private String driver;

	@Column(name = "driverClass")
	private String driverClass;
	
	@Column(name = "fullUrl")
	private String fullUrl;
	
	@Column(name = "`user`")
	private String user;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "`host`")
	private String host;
	
	@Column(name = "port")
	private String port;
	
	@Column(name = "dbName")
	private String dbName;
	
	@Column(name = "fmUser")
	private String fmUser;
	
	@Column(name = "fmPassword")
	private String fmPassword;
	
	@Column(name = "useFullUrl")
	private boolean useFullUrl = false;
	
	@Column(name = "isPasswordEncrypted")
	private boolean isPasswordEncrypted;
	
	public ModelDatasourceJDBC() { }

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDriver() {
		return driver;
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	public String getDriverClass() {
		return driverClass;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}

	public void setUseFullUrl(boolean useFullUrl) {
		this.useFullUrl = useFullUrl;
	}

	public boolean isUseFullUrl() {
		return useFullUrl;
	}

	public void setIsPasswordEncrypted(boolean isPasswordEncrypted) {
		this.isPasswordEncrypted = isPasswordEncrypted;
	}
	
	public boolean isIsPasswordEncrypted() {
		return isPasswordEncrypted;
	}

	public void setFmUser(String fmUser) {
		this.fmUser = fmUser;
	}

	public String getFmUser() {
		return fmUser;
	}

	public void setFmPassword(String fmPassword) {
		this.fmPassword = fmPassword;
	}

	public String getFmPassword() {
		return fmPassword;
	}
}
