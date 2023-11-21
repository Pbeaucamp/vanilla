package bpm.fwr.api.beans.dataset;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DataSource implements Serializable {

	private String name;
	private String url;
	private String user;
	private String password;
	private String group;
	private boolean isEncrypted;
	private String connectionName;
	private int itemId;
	private int repositoryId;
	private String businessModel;
	private String businessPackage;

	private boolean isOnOlap = false;

	public DataSource() {

	}

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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

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

	public String getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public String getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessPackage(String businessPackage) {
		this.businessPackage = businessPackage;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEncrypted(String encr) {
		if (encr.equalsIgnoreCase("true")) {
			isEncrypted = true;
		}
		else {
			isEncrypted = false;
		}
	}

	public void setItemId(String ite) {
		itemId = Integer.parseInt(ite);
	}

	public void setOnOlap(boolean isOnOlap) {
		this.isOnOlap = isOnOlap;
	}

	public boolean isOnOlap() {
		return isOnOlap;
	}

	/**
	 * This method is used by the fwr oda
	 * 
	 * @return the xml of the datasource
	 */
	public String getXml() {
		StringBuffer buf = new StringBuffer();

		buf.append("<datasource>\n");

		buf.append("	<model>" + this.businessModel + "</model>\n");
		buf.append("	<package>" + this.businessPackage + "</package>\n");
		buf.append("	<connection>" + this.connectionName + "</connection>\n");
		buf.append("	<group>" + this.group + "</group>\n");
		buf.append("	<itemid>" + this.itemId + "</itemid>\n");
		buf.append("	<name>" + this.name + "</name>\n");
		buf.append("	<password>" + this.password + "</password>\n");
		buf.append("	<isencrypted>" + this.isEncrypted + "</isencrypted>\n");
		buf.append("	<url>" + this.url + "</url>\n");
		buf.append("	<user>" + this.user + "</user>\n");

		buf.append("</datasource>\n");

		return buf.toString();
	}
}
