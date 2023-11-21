package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class ODSPackage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String description;
	private String publisher;
	private String domain;
	private String parentDomain;
	private String theme;
	private String records;

	public ODSPackage() {
	}

	public ODSPackage(String id, String title, String description, String publisher, String domain, String parentDomain, String theme, String records) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.publisher = publisher;
		this.domain = domain;
		this.parentDomain = parentDomain;
		this.theme = theme;
		this.records = records;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getParentDomain() {
		return parentDomain;
	}

	public void setParentDomain(String parentDomain) {
		this.parentDomain = parentDomain;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public String getRecords() {
		return records;
	}
	
	public void setRecords(String records) {
		this.records = records;
	}
}
