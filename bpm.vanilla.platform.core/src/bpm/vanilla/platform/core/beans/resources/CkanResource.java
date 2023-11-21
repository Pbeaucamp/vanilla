package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;

public class CkanResource implements Serializable {

	private String id;
	private String name;
	private String format;
	private String url;
	private String creationDate;
	private String lastModificationDate;
	private String description;
	private boolean datastoreActive;
	
	public CkanResource() { }
	
	public CkanResource(String id, String name, String format, String url) {
		this.id = id;
		this.name = name;
		this.format = format;
		this.url = url;
	}
	
	public CkanResource(String id, String name, String description, String format, String url) {
		this(id, name, format, url);
		this.description = description;
	}
	
	public CkanResource(String id, String name, String description, String format, String url, String creationDate, String lastModificationDate) {
		this(id, name, description, format, url);
		this.creationDate = creationDate;
		this.lastModificationDate = lastModificationDate;
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
	
	public String getDescription() {
		return description;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public String getLastModificationDate() {
		return lastModificationDate;
	}
	
	public boolean isDatastoreActive() {
		return datastoreActive;
	}
	
	public void setDatastoreActive(boolean datastoreActive) {
		this.datastoreActive = datastoreActive;
	}
	
	@Override
	public String toString() {
		return name + " (" + format + ")";
	}
}
