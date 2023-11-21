package bpm.vanilla.server.commons.server.tasks;

import java.util.Calendar;
import java.util.Date;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;



public class ReportGenerationState implements ITaskState{

	private Date creationDate;
	private Date stopDate;
	private Date startDate;
	private ActivityState state = ActivityState.WAITING;
	private ActivityResult result = ActivityResult.UNDEFINED;
	private String failureCause;
	
	/**
	 * init creationDate with the current Date
	 */
	public ReportGenerationState(){
		this.creationDate = Calendar.getInstance().getTime();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	
	public Long getDuration() {
		if (getStoppedDate() == null || getStartedDate() == null){
			return null;
		}
		return getStoppedDate().getTime() - getStartedDate().getTime();
	}

	
	public Long getElapsedTime() {
		if (getStartedDate() == null){
			return null;
		}
		if (isStopped()){
			return getDuration();
		}
		return Calendar.getInstance().getTimeInMillis() - getStartedDate().getTime();
	}

	
	public String getFailingCause() {
		return failureCause;
	}

	
	public Date getStartedDate() {
		return startDate;
	}

	
	public Date getStoppedDate() {
		return stopDate;
	}

	
	public ActivityResult getTaskResult() {
		return result;
	}

	
	public ActivityState getTaskState() {
		return state;
	}

	
	public boolean hasFailed() {
		return getTaskResult() == ActivityResult.FAILED;
	}

	
	public boolean hasSucceed() {
		return getTaskResult() == ActivityResult.SUCCEED;
	}

	
	public boolean isStarted() {
		return getStartedDate() != null;
	}

	
	public boolean isStopped() {
		return getStoppedDate() != null;
	}
	
	/**
	 * set the Start Date with the current Date
	 */
	public void setStarted(){
		startDate = Calendar.getInstance().getTime();
		state = ActivityState.RUNNING;
	}
	
	/**
	 * set the Stop Date with the current Date
	 */
	public void setStopped(){
		stopDate = Calendar.getInstance().getTime();
		state = ActivityState.ENDED;
	}
	
	/**
	 * set the task has ITaskState.RESULT_SUCCEED
	 */
	public void setSucceed(){
		result = ActivityResult.SUCCEED;
	}
	
	/**
	 * set the state has failed
	 * @param failureCause : the failure message
	 */
	public void setFailed(String failureCause){
		this.failureCause = failureCause;
		result = ActivityResult.FAILED;
	}

}
