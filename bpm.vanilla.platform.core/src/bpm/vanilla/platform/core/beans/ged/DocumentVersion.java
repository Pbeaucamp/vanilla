package bpm.vanilla.platform.core.beans.ged;

import java.io.Serializable;
import java.util.Date;

import bpm.vanilla.platform.core.beans.User;

public class DocumentVersion implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum DocumentStatus {
		ACTIVE, OUTDATED, ARCHIVED
	}
	
	private int id;
	private int documentId;
	private String documentPath;
	private int version = 1;
	private Date modificationDate;
	private int modifiedBy;
	private User modificator;
	private int isValidated = 0;
	private int isIndexed = 0;
	private String summary;
	private Date peremptionDate;
	
	private GedDocument parent;
	
	private DocumentStatus status = DocumentStatus.ACTIVE;
	
	public DocumentVersion() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public int getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(int isValidated) {
		this.isValidated = isValidated;
	}

	public int getIsIndexed() {
		return isIndexed;
	}

	public void setIsIndexed(int isIndexed) {
		this.isIndexed = isIndexed;
	}

	public void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public int getModifiedBy() {
		return modifiedBy;
	}
	
	public void setModificator(User modificator) {
		this.modificator = modificator;
	}
	
	public User getModificator() {
		return modificator;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setParent(GedDocument parent) {
		this.parent = parent;
	}

	public GedDocument getParent() {
		return parent;
	}

	public boolean isIndexed() {
		return isIndexed > 0;
	}
	
	/**
	 * Find the format from the extension of the path
	 * 
	 * @return the file format
	 */
	public String getFormat() {
		try {
			int index = documentPath.lastIndexOf(".") + 1;
			return documentPath.substring(index);
		} catch(Exception e) {
			//The relative path hasn't been set or not contains the format
			return "any";
		}
	}

	public void setPeremptionDate(Date peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public Date getPeremptionDate() {
		return peremptionDate;
	}

	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}
}
