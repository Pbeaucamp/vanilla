package bpm.vanilla.platform.core.beans.data;

public class DatasourceFmdt implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	private int itemId;
	private int repositoryId;
	private String user;
	private String password;
	private String businessPackage;
	private String businessModel;
	private int groupId;
	private String url;
	private boolean defaultUrl;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
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

	public String getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessPackage(String businessPackage) {
		this.businessPackage = businessPackage;
	}

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getUrl() {
		return url != null ? url : "";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(boolean defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	@Override
	public boolean equals(Object o) {
		DatasourceFmdt fmdt = (DatasourceFmdt) o;
		return defaultUrl == fmdt.isDefaultUrl() && businessModel.equals(fmdt.getBusinessModel()) && 
				businessPackage.equals(fmdt.getBusinessPackage()) && groupId == fmdt.getGroupId() && 
				itemId == fmdt.getItemId() && password.equals(fmdt.getPassword()) && user.equals(fmdt.getUser()) && 
				repositoryId == fmdt.getRepositoryId() && getUrl().equals(fmdt.getUrl());
	}

}
