package bpm.aklabox.workflow.core.model.resources;

public class DatabaseServer extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String databaseName;
	private String driverType;
	private String host;
	private int port;

	public DatabaseServer(String name, int userId, String userName, String password, String description, String databaseName, String driverType, String host, int port) {
		this.name = name;
		this.userId = userId;
		this.address = "http://" + host + ":" + String.valueOf(port);
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.databaseName = databaseName;
		this.driverType = driverType;
		this.host = host;
		this.port = port;
	}

	public DatabaseServer() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDriverType() {
		return driverType;
	}

	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
