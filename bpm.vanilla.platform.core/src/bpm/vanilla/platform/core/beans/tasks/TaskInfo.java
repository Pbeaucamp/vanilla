package bpm.vanilla.platform.core.beans.tasks;

import java.util.Date;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;

public class TaskInfo {

	private String id;
	private int itemId;
	private String itemName;
	private String className;
	private ActivityState state = ActivityState.WAITING;
	private ActivityResult result = ActivityResult.UNDEFINED;

	private Date creationDate;
	private Date startedDate;
	private Date stoppedDate;
	private Long elapsedTime;
	private Long durationTime;
	private String failureCause;

	private String priority;
	private String groupName;
	private int repositoryId;

	public TaskInfo(String id, String className, int itemId, String itemName) {
		this.id = id;
		this.className = className;
		this.itemId = itemId;
		this.itemName = itemName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public int getItemId() {
		return itemId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ActivityState getState() {
		return state;
	}

	public void setState(ActivityState state) {
		this.state = state;
	}

	public ActivityResult getResult() {
		return result;
	}

	public void setResult(ActivityResult result) {
		this.result = result;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	public Date getStoppedDate() {
		return stoppedDate;
	}

	public void setStoppedDate(Date stoppedDate) {
		this.stoppedDate = stoppedDate;
	}

	public Long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Long getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(Long durationTime) {
		this.durationTime = durationTime;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public int getRepositoryId() {
		return repositoryId;
	}
	
	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getItemName() {
		return itemName;
	}

}
