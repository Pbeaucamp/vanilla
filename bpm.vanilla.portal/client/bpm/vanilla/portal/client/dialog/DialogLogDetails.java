package bpm.vanilla.portal.client.dialog;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DialogLogDetails extends AbstractDialogBox {

	private static DialogLogDetailsUiBinder uiBinder = GWT.create(DialogLogDetailsUiBinder.class);

	interface DialogLogDetailsUiBinder extends UiBinder<Widget, DialogLogDetails> {
	}

	@UiField
	TextArea txtStep;

	public DialogLogDetails(String title, String logs) {
		super(title, false, true);
		setWidget(uiBinder.createAndBindUi(this));

		txtStep.setText(logs != null ? logs : ToolsGWT.lblCnst.NoLogs());

		createButton(ToolsGWT.lblCnst.Close(), closeHandler);
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
