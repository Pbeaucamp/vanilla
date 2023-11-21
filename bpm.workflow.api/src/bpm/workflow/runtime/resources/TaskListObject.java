package bpm.workflow.runtime.resources;

import bpm.workflow.runtime.model.activities.TaskListType;

public class TaskListObject extends BiRepositoryObject{
	private TaskListType type;
	
	public TaskListObject(){
		super();
	}
	
	public TaskListObject(int id, TaskListType type) {
		super(id);	
		this.type = type;
	}
	@Override
	protected void setParameterNames() {
		
		
	}

	/**
	 * @return the type
	 */
	public TaskListType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TaskListType type) {
		this.type = type;
	}

	
}
