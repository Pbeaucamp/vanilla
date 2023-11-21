package bpm.vanilla.platform.core.beans.ged;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GedDocument implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private Date creationDate = new Date();
	private int createdBy = 0;
	private Long accessCounter = 0l;
	private Integer categoryId;
	private Integer directoryId;

	private Lock lock;

	private boolean granted;
	private boolean mdmAttached;
	
	private List<DocumentVersion> documentVersions;

	public GedDocument() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = new Integer(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the id of the last user who accessed it
	 */
	public Long getAccessCounter() {
		return accessCounter;
	}

	public void setAccessCounter(Long accessCounter) {
		this.accessCounter = accessCounter;
	}

	public void setAccessCounter(String accessCounter) {
		try {
			this.accessCounter = Long.parseLong(accessCounter);
		} catch (Exception e) {
			this.accessCounter = -1l;
		}
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	public Integer getDirectoryId() {
		return directoryId;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setDocumentVersions(List<DocumentVersion> documentVersions) {
		this.documentVersions = documentVersions;
	}

	public List<DocumentVersion> getDocumentVersions() {
		if (documentVersions == null) {
			documentVersions = new ArrayList<DocumentVersion>();
		}
		return documentVersions;
	}

	public void addDocumentVersion(DocumentVersion version) {
		if (documentVersions == null) {
			documentVersions = new ArrayList<DocumentVersion>();
		}
		documentVersions.add(version);
		version.setParent(this);
	}

	public DocumentVersion getVersion(int version) {
		for (DocumentVersion v : documentVersions) {
			if (v.getVersion() == version) {
				return v;
			}
		}
		return null;
	}

	public DocumentVersion getCurrentVersion(Integer currentVersionId) {
		if (currentVersionId == null) {
			return getLastVersion();
		}
		
		if (documentVersions != null && !documentVersions.isEmpty()) {
			for (DocumentVersion v : documentVersions) {
				if (v.getId() == currentVersionId) {
					return v;
				}
			}
		}
		return null;
	}

	public DocumentVersion getLastVersion() {
		if (documentVersions != null && !documentVersions.isEmpty()) {
			int lastVersion = 0;
			for (DocumentVersion v : documentVersions) {
				if (v.getVersion() > lastVersion) {
					lastVersion = v.getVersion();
				}
			}

			return getVersion(lastVersion);
		}
		return null;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

	public Lock getLock() {
		return lock;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setMdmAttached(boolean mdmAttached) {
		this.mdmAttached = mdmAttached;
	}

	public boolean isMdmAttached() {
		return mdmAttached;
	}
}
