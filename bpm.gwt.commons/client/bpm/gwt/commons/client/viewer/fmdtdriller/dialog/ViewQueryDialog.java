package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ViewQueryDialog extends AbstractDialogBox {

	private static ViewQueryDialogUiBinder uiBinder = GWT.create(ViewQueryDialogUiBinder.class);

	interface ViewQueryDialogUiBinder extends UiBinder<Widget, ViewQueryDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	TextArea requestField;

	public ViewQueryDialog(String text) {
		super(LabelsConstants.lblCnst.ViewQuery(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), okHandler);
		requestField.setText(text);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
