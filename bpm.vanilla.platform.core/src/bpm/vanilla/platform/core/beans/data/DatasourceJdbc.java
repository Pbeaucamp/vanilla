package bpm.vanilla.platform.core.beans.data;

public class DatasourceJdbc implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	private String url;
	private String host;
	private String port;
	private String databaseName;
	private String user;
	private String password;
	private String driver;
	private boolean fullUrl;
	
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public boolean getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(boolean fullUrl) {
		this.fullUrl = fullUrl;
	}

	@Override
	public boolean equals(Object o) {
		DatasourceJdbc jdbc = (DatasourceJdbc) o;
		if(fullUrl == true && jdbc.getFullUrl()) {
			return driver.equals(jdbc.getDriver()) && url.equals(jdbc.getUrl()) && user.equals(jdbc.getUser()) && password.equals(jdbc.getPassword());
		}
		else if(fullUrl == false && !jdbc.getFullUrl()) {
			return driver.equals(jdbc.getDriver()) && databaseName.equals(jdbc.getDatabaseName()) && port.equals(jdbc.getPort()) && host.equals(jdbc.getHost()) && user.equals(jdbc.getUser()) && password.equals(jdbc.getPassword());
		}
		return false;
	}

}
