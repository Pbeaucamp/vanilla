package bpm.metadata.ui.birtreport.tools;

import bpm.vanilla.platform.core.IRepositoryApi;



public class ConnectionHelper {
	private static ConnectionHelper instance;
	

	private String repositoryUrl;
	private String vanillaRuntimeUrl;
	private String groupName;
	private String login;
	private String password;
	
	private Boolean isEncrypted;
	
	private int repositoryId;
	private int itemId;
	
	private IRepositoryApi sock;
	
	public ConnectionHelper(){
		
	}
	
	public static ConnectionHelper getInstance() {
		if(instance == null) {
			instance = new ConnectionHelper();
		}
		return instance;
	}
	
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsEncrypted() {
		return isEncrypted;
	}

	public void setIsEncrypted(Boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl) {
		this.vanillaRuntimeUrl = vanillaRuntimeUrl;
	}

	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}

	public void setSock(IRepositoryApi sock) {
		this.sock = sock;
	}

	public IRepositoryApi getSock() {
		return sock;
	}

}
