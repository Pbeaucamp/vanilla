package bpm.freemetrics.api.organisation.relations.appl_group;

import java.util.Date;

public class FmApplication_Groups {
	
	private int id;
	private Integer applicationId;
	private Integer groupId;
	private Date creationDate;
	private String comment;
	
	
	public FmApplication_Groups() {
		super();
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Integer getGroupId() {
		return groupId;
	}


	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * @return the applicationId
	 */
	public Integer getApplicationId() {
		return applicationId;
	}


	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}
	
	

}
