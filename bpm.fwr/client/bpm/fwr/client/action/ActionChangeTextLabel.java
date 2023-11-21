package bpm.fwr.client.action;

import bpm.fwr.client.widgets.LabelWidget;

public class ActionChangeTextLabel extends Action{

	private LabelWidget label;
	private String oldText;
	private String newText;
	
	public ActionChangeTextLabel(ActionType type, LabelWidget label, String oldText, String newText) {
		super(type);
		this.label = label;
		this.oldText = oldText;
		this.newText = newText;
	}

	@Override
	public void executeRedo() {
		label.setText(newText, oldText, ActionType.REDO);
	}

	@Override
	public void executeUndo() {
		label.setText(oldText, "", ActionType.UNDO);
	}
}
