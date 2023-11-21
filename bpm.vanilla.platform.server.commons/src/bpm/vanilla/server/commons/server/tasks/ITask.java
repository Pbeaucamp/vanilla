package bpm.vanilla.server.commons.server.tasks;

import java.io.Serializable;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;
import bpm.vanilla.platform.core.beans.tasks.TaskPriority;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;


public interface ITask extends Comparable, Serializable{
	
	/**
	 * 
	 * @return the Id of the Task on the Server
	 */
	public long getId();
	
	/**
	 * 
	 * @return the current state of the task
	 */
	public ITaskState getTaskState();
	
	/**
	 * 
	 * @return true if the task is running
	 */
	public boolean isRunning();
	
	/**
	 * 
	 * @return tru if the task is Stopped
	 */
	public boolean isStopped();
	
	/**
	 * stop the execution of the task
	 * @throws Exception
	 */
	public void stopTask() throws Exception;
	
	/**
	 * start the execution of the Task
	 */
	public void startTask();
	
	/**
	 * 
	 * @return the TaskPriority
	 */
	public TaskPriority getTaskPriority();
	
	public void join() throws Exception;
	
	public IObjectIdentifier getObjectIdentifier();
	
	public int getGroupId();
	
	public IVanillaComponentIdentifier getComponentIdentifier();
	
	public String getSessionId();
}
