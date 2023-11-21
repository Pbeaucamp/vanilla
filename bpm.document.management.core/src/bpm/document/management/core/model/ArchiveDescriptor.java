package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ArchiveDescriptor implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String DESCRIPTOR = "descriptor";

	private String archiveName;
	
	private Date creationDate;
	private String securityKey;
	
	private List<String> documents;
	
	public ArchiveDescriptor() { }
	
	public ArchiveDescriptor(String archiveName, Date creationDate, String securityKey, List<String> documents) {
		this.archiveName = archiveName;
		this.creationDate = creationDate;
		this.securityKey = securityKey;
		this.documents = documents;
	}
	
	public String getName() {
		return DESCRIPTOR;
	}
	
	public String getArchiveName() {
		return archiveName;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getSecurityKey() {
		return securityKey;
	}
	
	public List<String> getDocuments() {
		return documents;
	}
}
