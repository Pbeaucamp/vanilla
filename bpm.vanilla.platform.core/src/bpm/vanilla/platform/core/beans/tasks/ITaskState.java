package bpm.vanilla.platform.core.beans.tasks;

import java.io.Serializable;
import java.util.Date;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;

public interface ITaskState extends Serializable{
	
	/**
	 * 
	 * @return the Date the task has been created on the server
	 */
	public Date getCreationDate();
	
	/**
	 * 
	 * @return the Date the task has began in the server
	 * or null if the Task did not start yet
	 */
	public Date getStartedDate();
	
	/**
	 * 
	 * @return the Date the task has ended in the server
	 * or null is the Task did not stop yet
	 */
	public Date getStoppedDate();
	
	/**
	 * 
	 * @return the Time between the start and now
	 * or null if the task did not start yet
	 * or getDuration() if the task is stoped
	 */
	public Long getElapsedTime();
	
	/**
	 * 
	 * @return the Time between the start and the end date
	 * or null if the task did not start nor stop
	 */
	public Long getDuration();
	
	/**
	 * 
	 * @return a constant for Task State
	 */
	public ActivityState getTaskState();
	
	/**
	 * 
	 * @return a constant for the Task Result
	 */
	public ActivityResult getTaskResult();
	
	/**
	 * 
	 * @return a description of the failing cause
	 */
	public String getFailingCause();
	
	/**
	 * 
	 * @return true if the Task has started
	 */
	public boolean isStarted();
	/**
	 * 
	 * @return true if the Task has stoped
	 */
	public boolean isStopped();
	
	/**
	 * 
	 * @return true if the task failed
	 */
	public boolean hasFailed();
	
	/**
	 * 
	 * @return true if the task succeed
	 */
	public boolean hasSucceed();
}