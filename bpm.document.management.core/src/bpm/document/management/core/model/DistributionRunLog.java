package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class DistributionRunLog implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id = 0;
	
	private int distributionId = 0;
	
	private int numUser = 0;
	
	private int numDocuments = 0;
	
	private int numUsersWithDocuments = 0;
	
	private int numUsersWithoutDocuments = 0;
	
	private int numDocumentsDispatch = 0;
	
	private int numDocumentsNotDispatch = 0;
	
	private Date creationDate = new Date();

	public DistributionRunLog() {
		super();
	}

	public DistributionRunLog(int distributionId, int numUser,
			int numDocuments, int numUsersWithDocuments,
			int numUsersWithoutDocuments, int numDocumentsDispatch,
			int numDocumentsNotDispatch) {
		super();
		this.distributionId = distributionId;
		this.numUser = numUser;
		this.numDocuments = numDocuments;
		this.numUsersWithDocuments = numUsersWithDocuments;
		this.numUsersWithoutDocuments = numUsersWithoutDocuments;
		this.numDocumentsDispatch = numDocumentsDispatch;
		this.numDocumentsNotDispatch = numDocumentsNotDispatch;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDistributionId() {
		return distributionId;
	}

	public void setDistributionId(int distributionId) {
		this.distributionId = distributionId;
	}

	public int getNumUser() {
		return numUser;
	}

	public void setNumUser(int numUser) {
		this.numUser = numUser;
	}

	public int getNumDocuments() {
		return numDocuments;
	}

	public void setNumDocuments(int numDocuments) {
		this.numDocuments = numDocuments;
	}

	public int getNumUsersWithDocuments() {
		return numUsersWithDocuments;
	}

	public void setNumUsersWithDocuments(int numUsersWithDocuments) {
		this.numUsersWithDocuments = numUsersWithDocuments;
	}

	public int getNumUsersWithoutDocuments() {
		return numUsersWithoutDocuments;
	}

	public void setNumUsersWithoutDocuments(int numUsersWithoutDocuments) {
		this.numUsersWithoutDocuments = numUsersWithoutDocuments;
	}

	public int getNumDocumentsDispatch() {
		return numDocumentsDispatch;
	}

	public void setNumDocumentsDispatch(int numDocumentsDispatch) {
		this.numDocumentsDispatch = numDocumentsDispatch;
	}

	public int getNumDocumentsNotDispatch() {
		return numDocumentsNotDispatch;
	}

	public void setNumDocumentsNotDispatch(int numDocumentsNotDispatch) {
		this.numDocumentsNotDispatch = numDocumentsNotDispatch;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	

	
}
