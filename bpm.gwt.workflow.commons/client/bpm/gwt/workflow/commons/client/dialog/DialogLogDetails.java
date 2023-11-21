package bpm.gwt.workflow.commons.client.dialog;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class DialogLogDetails extends AbstractDialogBox {

	private static DialogLogDetailsUiBinder uiBinder = GWT.create(DialogLogDetailsUiBinder.class);

	interface DialogLogDetailsUiBinder extends UiBinder<Widget, DialogLogDetails> {
	}

	@UiField
	HTML txtStep;

	public DialogLogDetails(String title, String logs) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));

		txtStep.setHTML(logs != null ? logs.replaceAll("(\r\n|\n)", "<br />") : LabelsCommon.lblCnst.NoLogs());

		createButton(LabelsCommon.lblCnst.Close(), closeHandler);
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
