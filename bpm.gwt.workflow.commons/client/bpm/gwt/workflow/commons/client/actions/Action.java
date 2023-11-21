package bpm.gwt.workflow.commons.client.actions;

public abstract class Action {

	public enum TypeAction {
		ADD, REMOVE
	}
	
	protected String name;
	
	public Action(String name) {
		this.name = name;
	}

	public abstract void doAction();
	
	public abstract void undoAction();
}
