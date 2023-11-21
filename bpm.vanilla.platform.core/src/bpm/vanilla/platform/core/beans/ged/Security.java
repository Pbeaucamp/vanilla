package bpm.vanilla.platform.core.beans.ged;

public class Security {
	private int id;
	private int documentId;
	private int groupId = 0;
	
	// if null visible for all repositories
	private Integer repositoryId;
	
	private Integer userId = 0;
	
	/**
	 * this field is used to enable or disable an indexed document
	 * it has to be updated when an Group has a change on its grant for items Historic
	 * Then, when performing a Lookup in the indexedDocument,
	 * if this value <=0 the search will not send the document has a 
	 * result 
	 */
	private int isAvailable = 1;
	
	public Security() {
		
	}

	/**
	 * @return the isAvailable
	 */
	public int getIsAvailable() {
		return isAvailable;
	}

	/**
	 * @param isAvailable the isAvailable to set
	 */
	public void setIsAvailable(int isAvailable) {
		this.isAvailable = isAvailable;
	}

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

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public Integer getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}
		
	public Integer getUserId() {
		if (userId == null) {
			return 0;
		}
		return userId;
	}
	
	public void setUserId(Integer userId) {
		if (userId == null) {
			this.userId = 0;
		}
		else {
			this.userId = userId;
		}
	}
}
