package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Delegation implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id = 0;
	private String emailDelegator;
	private String emailDelegate;
	private Date beginDate;
	private Date endDate;
	private boolean isActive;
	
	private User delegator;

	public Delegation() {
	}

	public Delegation(String delegator, String delegate, Date beginDate, Date endDate, boolean isActive) {
		this.emailDelegate = delegate;
		this.emailDelegator = delegator;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.isActive = isActive;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmailDelegator() {
		return emailDelegator;
	}

	public void setEmailDelegator(String emailDelegator) {
		this.emailDelegator = emailDelegator;
	}

	public String getEmailDelegate() {
		return emailDelegate;
	}

	public void setEmailDelegate(String emailDelegate) {
		this.emailDelegate = emailDelegate;
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

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public User getDelegator() {
		return delegator;
	}

	public void setDelegator(User delegator) {
		this.delegator = delegator;
	}

}
