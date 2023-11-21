package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;

public abstract class ChildDialogComposite extends CompositeWaitPanel {

	private DialogBox dialogParent;

	public void setParent(DialogBox dialogParent) {
		this.dialogParent = dialogParent;
	}

	public void closeParent() {
		if (dialogParent != null) {
			dialogParent.hide();
		}
	}
}
