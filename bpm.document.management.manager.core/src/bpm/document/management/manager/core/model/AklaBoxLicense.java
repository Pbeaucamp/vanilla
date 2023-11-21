package bpm.document.management.manager.core.model;

import java.io.Serializable;
import java.util.Date;

public class AklaBoxLicense implements Serializable{

	private static final long serialVersionUID = 1L;

	private int licenseId;
	private String licenseInstance;
	private Date licenseCreation;
	private Date licenseExpiration;
	private String licenseEmail;
	private String licenseReference;
	private String licenseKey;
	private String licenseType;
	
	private int userLimit;
	private boolean finished;
	
	public int getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(int licenseId) {
		this.licenseId = licenseId;
	}
	public String getLicenseInstance() {
		return licenseInstance;
	}
	public void setLicenseInstance(String licenseInstance) {
		this.licenseInstance = licenseInstance;
	}
	public Date getLicenseCreation() {
		return licenseCreation;
	}
	public void setLicenseCreation(Date licenseCreation) {
		this.licenseCreation = licenseCreation;
	}
	public Date getLicenseExpiration() {
		return licenseExpiration;
	}
	public void setLicenseExpiration(Date licenseExpiration) {
		this.licenseExpiration = licenseExpiration;
	}
	public String getLicenseEmail() {
		return licenseEmail;
	}
	public void setLicenseEmail(String licenseEmail) {
		this.licenseEmail = licenseEmail;
	}
	public String getLicenseReference() {
		return licenseReference;
	}
	public void setLicenseReference(String licenseReference) {
		this.licenseReference = licenseReference;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public String getLicenseKey() {
		return licenseKey;
	}
	public void setLicenseKey(String licenseKey) {
		this.licenseKey = licenseKey;
	}
	public int getUserLimit() {
		return userLimit;
	}
	public void setUserLimit(int userLimit) {
		this.userLimit = userLimit;
	}
	public String getLicenseType() {
		return licenseType;
	}
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}
}
