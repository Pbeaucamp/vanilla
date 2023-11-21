package bpm.vanilla.platform.core.beans;

import java.util.Date;
/**
 * a simple Bean to store in the Vanilla DataBase the FMDT queries
 * perfomed by its ODA driver
 * @author ludo
 *
 */
public class FMDTQueryBean {
	private long id;
	private int repositoryId;
	private int directoryItemId;
	private int vanillaGroupId;
	private String fmdtQuery;
	private String effectiveQuery;
	private long duration;
	private Date date;
	private int weight;
	private String failureCause;
	
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
	 * @return the groupId
	 */
	public int getVanillaGroupId() {
		return vanillaGroupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setVanillaGroupId(int vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}
	/**
	 * @return the fmdtQuery
	 */
	public String getFmdtQuery() {
		return fmdtQuery;
	}
	/**
	 * @param fmdtQuery the fmdtQuery to set
	 */
	public void setFmdtQuery(String fmdtQuery) {
		this.fmdtQuery = fmdtQuery;
	}
	/**
	 * @return the effectiveQuery
	 */
	public String getEffectiveQuery() {
		return effectiveQuery;
	}
	/**
	 * @param effectiveQuery the effectiveQuery to set
	 */
	public void setEffectiveQuery(String effectiveQuery) {
		this.effectiveQuery = effectiveQuery;
	}
	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * @return the failureCause
	 */
	public String getFailureCause() {
		return failureCause;
	}
	/**
	 * @param failureCause the failureCause to set
	 */
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}
	
	
}
