package bpm.gwt.commons.shared.repository;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentVersionDTO implements IsSerializable {
	private int id;
	private DocumentDefinitionDTO documentParent;
	
	private String name;
	private String key;
	private String version;
	private String format;
	private String summary;
	
	private Date creationDate;
	
	private boolean locked;
	
	public DocumentVersionDTO() { }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public void setDocumentParent(DocumentDefinitionDTO documentParent) {
		this.documentParent = documentParent;
	}

	public DocumentDefinitionDTO getDocumentParent() {
		return documentParent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
}
