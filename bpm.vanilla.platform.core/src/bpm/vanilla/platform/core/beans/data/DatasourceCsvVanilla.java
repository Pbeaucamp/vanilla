package bpm.vanilla.platform.core.beans.data;

public class DatasourceCsvVanilla implements IDatasourceObject {

	private static final long serialVersionUID = 4541113647173029720L;

	private int itemId;
	private int repositoryId;
	private String user;
	private String password;
	private int groupId;
	private String separator = ";";

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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getSeparator() {
		if(separator == null) {
			separator = ";";
		}
 		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public boolean equals(Object o) {
		DatasourceCsvVanilla ds = (DatasourceCsvVanilla) o;
		return groupId == ds.getGroupId() && itemId == ds.getItemId() && password.equals(ds.getPassword()) && user.equals(ds.getUser()) && repositoryId == ds.getRepositoryId() && separator.equals(ds.getSeparator());
	}

}
