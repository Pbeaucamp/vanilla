package bpm.fwr.client.action;

import bpm.fwr.client.draggable.widgets.DraggableColumnHTML;

public class ActionChangeDraggableColumnHTMLLabel extends Action{

	private DraggableColumnHTML dragColumnHtml;
	private String oldText;
	private String newText;
	
	public ActionChangeDraggableColumnHTMLLabel(ActionType type, DraggableColumnHTML dragColumnHtml, String oldText, 
			String newText) {
		super(type);
		this.dragColumnHtml = dragColumnHtml;
		this.oldText = oldText;
		this.newText = newText;
	}

	@Override
	public void executeRedo() {
		dragColumnHtml.setText(newText, oldText, ActionType.REDO);
	}

	@Override
	public void executeUndo() {
		dragColumnHtml.setText(oldText, "", ActionType.UNDO);
	}
}
