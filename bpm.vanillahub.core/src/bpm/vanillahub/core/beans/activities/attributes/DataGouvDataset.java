package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.List;

public class DataGouvDataset implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String createdAt;
	private String lastModified;
	private String description;
	
	private List<DataGouvResource> resources;
	
	public DataGouvDataset() { }
	
	public DataGouvDataset(String id, String title, String description, String createdAt, String lastModified, List<DataGouvResource> resources) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
		this.resources = resources;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getCreatedAt() {
		return createdAt;
	}
	
	public String getLastModified() {
		return lastModified;
	}
	
	public List<DataGouvResource> getResources() {
		return resources;
	}

	public String getHelp() {
		StringBuilder buf = new StringBuilder();
		buf.append(title + "\n\n");
		buf.append("    " + description);
		return buf.toString();
	}
}
