package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Absence implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id = 0;
	private int userId;
	private Date beginDate;
	private Date endDate;
	
	private int delegationId;

	public Absence() {
	}

	public Absence(int userId, Date beginDate, Date endDate) {
		this.setUserId(userId);
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getDelegationId() {
		return delegationId;
	}
	
	public void setDelegationId(int delegationId) {
		this.delegationId = delegationId;
	}

}
