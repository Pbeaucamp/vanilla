package bpm.aklabox.workflow.core.model.resources;

public class VanillaServer extends Resource {


	/**
	 * 
	 */
	private static final long serialVersionUID = 8471780105978263529L;
	
	private String description;
	private int groupId;
	private int repositoryId;

	public VanillaServer(String name, int userId, String userName, String password, String description, String address, int groupId, int repositoryId) {
		this.name = name;
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.address = address;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
	}

	public VanillaServer() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
}
