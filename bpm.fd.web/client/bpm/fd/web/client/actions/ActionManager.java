package bpm.fd.web.client.actions;

import java.util.LinkedList;

public class ActionManager {

	private LinkedList<Action> actions = new LinkedList<Action>();
	private LinkedList<Action> actionsUndo = new LinkedList<Action>();

	public void launchAction(Action action, boolean launch) {
		actions.add(action);
		if (launch) {
			action.doAction();
		}
	}

	public void undoAction() {
		if (actions.isEmpty()) {
			return;
		}

		Action action = actions.removeLast();
		actionsUndo.addFirst(action);
		action.undoAction();
	}

	public void redoAction() {
		if (actionsUndo.isEmpty()) {
			return;
		}

		Action action = actionsUndo.removeFirst();
		launchAction(action, true);
	}
}
