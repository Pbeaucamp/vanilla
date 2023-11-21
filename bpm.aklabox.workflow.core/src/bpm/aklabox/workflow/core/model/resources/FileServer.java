package bpm.aklabox.workflow.core.model.resources;


public class FileServer extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;
	private String serverType;
	private int port;

	public FileServer(String name, int userId, String userName, String password, String description, String serverType, String address, int port) {
		this.name = name;
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.serverType = serverType;
		this.address = address;
		this.port = port;
	}

	public FileServer() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
