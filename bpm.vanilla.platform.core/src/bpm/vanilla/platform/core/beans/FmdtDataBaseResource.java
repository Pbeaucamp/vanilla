package bpm.vanilla.platform.core.beans;

public class FmdtDataBaseResource {
	private String id;
	private String type;
	private String clientIp;
	private String additional;
	private String creation;
	private String user;
	/**
	 * @param id
	 * @param type
	 * @param clientIp
	 */
	public FmdtDataBaseResource(String id, String type, String clientIp, String additional, String creation, String user) {
		super();
		this.id = id;
		this.type = type;
		this.clientIp = clientIp;
		this.additional = additional;
		this.creation = creation;
		this.user = user;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @return the creation
	 */
	public String getCreation() {
		return creation;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the additional
	 */
	public String getAdditional() {
		return additional;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}
	
	
	
}
