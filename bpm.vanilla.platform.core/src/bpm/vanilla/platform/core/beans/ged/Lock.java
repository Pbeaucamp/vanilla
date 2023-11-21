package bpm.vanilla.platform.core.beans.ged;

import java.io.Serializable;
import java.util.Date;

public class Lock implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private int userId;
	private Date lockDate;
	private int docId;

	public Lock() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getDocId() {
		return docId;
	}

}
