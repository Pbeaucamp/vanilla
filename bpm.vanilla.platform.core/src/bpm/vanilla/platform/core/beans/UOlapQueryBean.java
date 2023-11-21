package bpm.vanilla.platform.core.beans;

import java.util.Date;

public class UOlapQueryBean {
	private long id;
	private int repositoryId;
	private int directoryItemId;
	private int vanillaGroupId;
	private String mdxQuery;
	private Date executionDate;
	private long executionTime;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the repositoryId
	 */
	public int getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	/**
	 * @return the directoryItemId
	 */
	public int getDirectoryItemId() {
		return directoryItemId;
	}
	/**
	 * @param directoryItemId the directoryItemId to set
	 */
	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	/**
	 * @return the vanillaGroupId
	 */
	public int getVanillaGroupId() {
		return vanillaGroupId;
	}
	/**
	 * @param vanillaGroupId the vanillaGroupId to set
	 */
	public void setVanillaGroupId(int vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}
	/**
	 * @return the mdxQuery
	 */
	public String getMdxQuery() {
		return mdxQuery;
	}
	/**
	 * @param mdxQuery the mdxQuery to set
	 */
	public void setMdxQuery(String mdxQuery) {
		this.mdxQuery = mdxQuery;
	}
	/**
	 * @return the executionDate
	 */
	public Date getExecutionDate() {
		return executionDate;
	}
	/**
	 * @param executionDate the executionDate to set
	 */
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}
	/**
	 * @return the executionTime
	 */
	public long getExecutionTime() {
		return executionTime;
	}
	/**
	 * @param executionTime the executionTime to set
	 */
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	
}
