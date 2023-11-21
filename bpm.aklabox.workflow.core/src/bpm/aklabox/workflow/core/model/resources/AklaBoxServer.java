package bpm.aklabox.workflow.core.model.resources;

public class AklaBoxServer extends Resource {

	private static final long serialVersionUID = 1L;

	private String description;

	public AklaBoxServer(String name, int userId, String userName, String password, String description, String address) {
		this.name = name;
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.address = address;
	}

	public AklaBoxServer() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
