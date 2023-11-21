package bpm.document.management.manager.core.model;

import java.io.Serializable;
import java.util.Date;

public class AklaBoxInstance implements Serializable{

	private static final long serialVersionUID = 1L;

	private int instanceId;
	private String instanceUrl;
	private String instanceName;
	private String licenseReference;
	private String email;
	private String password;
	private Date creationDate;
	private String licenseType;
	
	public int getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}
	public String getInstanceUrl() {
		return instanceUrl;
	}
	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getLicenseReference() {
		return licenseReference;
	}
	public void setLicenseReference(String licenseReference) {
		this.licenseReference = licenseReference;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
	
	
	
}
