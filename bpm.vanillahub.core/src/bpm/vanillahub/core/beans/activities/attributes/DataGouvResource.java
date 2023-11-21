package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class DataGouvResource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String description;
	private String createdAt;
	private String lastModified;
	private String format;
	private String url;
	
	public DataGouvResource() { }

	public DataGouvResource(String id, String title, String description, String createdAt, String lastModified, String format, String url) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdAt = createdAt;
		this.lastModified = lastModified;
		this.format = format;
		this.url = url;
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
	
	public String getFormat() {
		return format;
	}
	
	public String getUrl() {
		return url;
	}

	public String getHelp() {
		StringBuilder buf = new StringBuilder();
		buf.append(title + "\n\n");
		buf.append("    " + description);
		return buf.toString();
	}
	
	@Override
	public String toString() {
		return title + " - (" + format + ")";
	}
}
