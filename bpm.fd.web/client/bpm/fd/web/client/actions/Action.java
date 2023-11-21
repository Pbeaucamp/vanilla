package bpm.fd.web.client.actions;

public abstract class Action {

	public enum TypeAction {
		ADD, REMOVE, MOVE, MOVE_PANEL
	}

	public abstract void doAction();
	
	public abstract void undoAction();
}
