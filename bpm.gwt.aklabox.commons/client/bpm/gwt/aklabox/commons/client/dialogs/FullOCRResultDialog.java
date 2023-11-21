package bpm.gwt.aklabox.commons.client.dialogs;

import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FullOCRResultDialog extends AbstractDialogBox {

	private static FullOCRResultDialogUiBinder uiBinder = GWT.create(FullOCRResultDialogUiBinder.class);

	interface FullOCRResultDialogUiBinder extends UiBinder<Widget, FullOCRResultDialog> {
	}

	@UiField
	TextArea txtOcrResult;

	public FullOCRResultDialog(FormCellResult result) {
		super(LabelsConstants.lblCnst.OcrResult(), false, true);

		setWidget(uiBinder.createAndBindUi(this));

		txtOcrResult.setText(result.getOcrResult());

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	@Override
	public int getThemeColor() {
		return 5;
	}
}