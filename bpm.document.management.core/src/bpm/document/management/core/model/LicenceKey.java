package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class LicenceKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id=0;
	private String licenceKey="";
	private String email="";
	private Date creationDate=new Date();
	private Date endDate = new Date();
	private String licenseType="";
	private int userLimit=0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLicenceKey() {
		return licenceKey;
	}

	public void setLicenceKey(String licenceKey) {
		this.licenceKey = licenceKey;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setUserLimit(int userLimit) {
		this.userLimit = userLimit;
	}

	public int getUserLimit() {
		return 1000;
//		return userLimit;
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
