package bpm.vanilla.workplace.shared.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PlaceWebPackage implements IsSerializable {

	private int id;
	private int projectId;
	private int creatorId;

	private int type;

	private String name;
	private String version;
	private String vanillaVersion;
	
	private Date creationDate;
	
	private String path;
	
	private boolean valid;
	private boolean certified;
	
	private String documentationUrl;
	private String siteWebUrl;
	private String prerequisUrl;

	public PlaceWebPackage() { }
	
	public PlaceWebPackage(String name, String version, String vanillaVersion, int projectId, 
			int creatorId, int type, Date creationDate, String path, boolean valid, boolean certified,
			String documentationUrl, String siteWebUrl, String prerequisUrl) {
		this.name = name;
		this.version = version;
		this.vanillaVersion = vanillaVersion;
		this.projectId = projectId;
		this.creatorId = creatorId;
		this.type = type;
		this.creationDate = creationDate;
		this.path = path;
		this.valid = valid;
		this.certified = certified;
		this.documentationUrl = documentationUrl;
		this.siteWebUrl = siteWebUrl;
		this.prerequisUrl = prerequisUrl;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVanillaVersion() {
		return vanillaVersion;
	}

	public void setVanillaVersion(String vanillaVersion) {
		this.vanillaVersion = vanillaVersion;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean getValid() {
		return valid;
	}

	public void setCertified(boolean certified) {
		this.certified = certified;
	}

	public boolean getCertified() {
		return certified;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setSiteWebUrl(String siteWebUrl) {
		this.siteWebUrl = siteWebUrl;
	}

	public String getSiteWebUrl() {
		return siteWebUrl;
	}

	public void setPrerequisUrl(String prerequisUrl) {
		this.prerequisUrl = prerequisUrl;
	}

	public String getPrerequisUrl() {
		return prerequisUrl;
	}
}
