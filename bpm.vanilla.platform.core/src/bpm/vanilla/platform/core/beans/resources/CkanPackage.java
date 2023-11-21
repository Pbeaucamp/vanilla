package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CkanPackage implements Serializable {
	
	private String id;
	private String name;
	private String title;
	private String description;
	private String org;
	
	private Date metadataDate;
	private Map<String, String> extras = new HashMap<String, String>();
	
	private String licenseId;
	private boolean isPrivate;

	private List<String> keywords;
	private List<CkanResource> resources;
	
	private CkanResource selectedResource;
	
	public CkanPackage() { }

	public CkanPackage(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public CkanPackage(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title != null ? title : "";
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description != null ? description : "";
	}
	
	public void setOrg(String org) {
		this.org = org;
	}
	
	public String getOrg() {
		return org != null ? org : "";
	}
	
	public List<String> getKeywords() {
		return keywords != null ? keywords : new ArrayList<String>();
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public void putKeyword(String keyword) {
		if (keywords == null) {
			this.keywords = new ArrayList<String>();
		}
		this.keywords.add(keyword);
	}
	
	public String getLicenseId() {
		return licenseId != null ? licenseId : "";
	}
	
	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}
	
	public boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public void setResources(List<CkanResource> resources) {
		this.resources = resources;
	}
	
	public List<CkanResource> getResources() {
		return resources;
	}
	
	public void setSelectedResource(CkanResource selectedResource) {
		this.selectedResource = selectedResource;
	}
	
	public CkanResource getSelectedResource() {
		return selectedResource;
	}
	
	@Override
	public String toString() {
		return title != null && !title.isEmpty() ? title : name;
	}

	public Map<String, String> getExtras() {
		return extras;
	}

	public void setExtras(Map<String, String> extras) {
		this.extras = extras;
	}
	
	public void putExtra(String key, String value) {
		if (value.equalsIgnoreCase("true")) {
			value = "1";
		}
		else if (value.equalsIgnoreCase("false")) {
			value = "0";
		}
		this.extras.put(key, value);
	}

	public Date getMetadataDate() {
		return metadataDate;
	}
	
	public void setMetadataDate(Date metadataDate) {
		this.metadataDate = metadataDate;
	}
}
