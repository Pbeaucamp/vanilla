package bpm.fwr.client.action;

public abstract class Action {

	protected ActionType type;
	
	public Action(ActionType type) {
		this.type = type;
	}
	
	public abstract void executeRedo();
	
	public abstract void executeUndo();
}
