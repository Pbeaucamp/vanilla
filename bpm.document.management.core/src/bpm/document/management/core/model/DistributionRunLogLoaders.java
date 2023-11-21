package bpm.document.management.core.model;

import java.io.Serializable;

public class DistributionRunLogLoaders implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id = 0;
	
	private int logId = 0;
	
	private int userId = 0;
	
	private int docId = 0;

	public DistributionRunLogLoaders() {
		super();
	}

	public DistributionRunLogLoaders(int logId, int userId, int docId) {
		super();
		this.logId = logId;
		this.userId = userId;
		this.docId = docId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
