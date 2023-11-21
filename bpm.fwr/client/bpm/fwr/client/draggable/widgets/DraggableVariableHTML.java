package bpm.fwr.client.draggable.widgets;

import bpm.fwr.client.dialogs.SaveOptionsDialogBoxPanel;
import bpm.fwr.client.utils.VariableTypes;

import com.google.gwt.user.client.ui.HTML;

public class DraggableVariableHTML extends HTML{
	
	private VariableTypes type;
	private SaveOptionsDialogBoxPanel dialParent;

	public DraggableVariableHTML(String html, SaveOptionsDialogBoxPanel dialParent, VariableTypes type){
		super(html);
		this.setType(type);
		this.setDialParent(dialParent);
	}

	public void setType(VariableTypes type) {
		this.type = type;
	}

	public VariableTypes getType() {
		return type;
	}

	public void setDialParent(SaveOptionsDialogBoxPanel dialParent) {
		this.dialParent = dialParent;
	}

	public SaveOptionsDialogBoxPanel getDialParent() {
		return dialParent;
	}
}
