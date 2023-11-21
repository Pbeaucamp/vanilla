package bpm.fwr.client.draggable.widgets;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.client.dialogs.AddColumnToDatasetDialogBox;

import com.google.gwt.user.client.ui.Label;

public class DraggableColumnLabel extends Label{
	
	private AddColumnToDatasetDialogBox dialogParent;
	private Column column;

	public DraggableColumnLabel(String text, AddColumnToDatasetDialogBox dialogParent, Column column) {
		super(text);
		
		this.setDialogParent(dialogParent);
		this.column = column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Column getColumn() {
		return column;
	}

	public void setDialogParent(AddColumnToDatasetDialogBox dialogParent) {
		this.dialogParent = dialogParent;
	}

	public AddColumnToDatasetDialogBox getDialogParent() {
		return dialogParent;
	}
}
