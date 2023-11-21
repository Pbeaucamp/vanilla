package bpm.vanillahub.web.client.dialogs;

import java.util.List;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.services.ResourcesService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DisplayReferenceDialog extends AbstractDialogBox {
	private static final int DEFAULT_CHAR_BEFORE = 75;
	private static final int DEFAULT_CHAR_AFTER = 100;

	private static DisplayReferenceDialogUiBinder uiBinder = GWT.create(DisplayReferenceDialogUiBinder.class);

	interface DisplayReferenceDialogUiBinder extends UiBinder<Widget, DisplayReferenceDialog> {
	}

	@UiField
	TextHolderBox txtReference, txtCharBefore, txtCharAfter;

	@UiField
	Button btnLoadReferences, btnPrevious, btnNext;

	@UiField
	TextArea txtStep;
	
	@UiField
	Label lblNbReferences;

	private String url;
	private List<String> references;

	private int displayIndex = 0;

	public DisplayReferenceDialog(String url) {
		super(Labels.lblCnst.References(), false, true);
		this.url = url;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsCommon.lblCnst.Close(), closeHandler);
		
		updateUi();
		txtCharBefore.setText(String.valueOf(DEFAULT_CHAR_BEFORE));
		txtCharAfter.setText(String.valueOf(DEFAULT_CHAR_AFTER));
	}

	@UiHandler("btnLoadReferences")
	public void onLoadReference(ClickEvent event) {
		final String reference = txtReference.getText();
		if (reference == null || reference.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Information(), Labels.lblCnst.SearchItemIsNeeded());
			return;
		}

		showWaitPart(true);
		
		int charBefore = DEFAULT_CHAR_BEFORE;
		try {
			charBefore = Integer.parseInt(txtCharBefore.getText());
		} catch(Exception e) {
			txtCharBefore.setText(String.valueOf(DEFAULT_CHAR_BEFORE));
		}
		
		int charAfter = DEFAULT_CHAR_AFTER;
		try {
			charAfter = Integer.parseInt(txtCharAfter.getText());
		} catch(Exception e) {
			txtCharAfter.setText(String.valueOf(DEFAULT_CHAR_AFTER));
		}

		ResourcesService.Connect.getInstance().getCrawlReference(url, reference, charBefore, charAfter, new GwtCallbackWrapper<List<String>>(this, true) {

			@Override
			public void onSuccess(List<String> result) {
				references = result;
				displayIndex = 0;
				if (result != null && !result.isEmpty()) {
					txtStep.setText(result.get(displayIndex));
				}
				else {
					txtStep.setText(Labels.lblCnst.NoReferenceFoundFor() + " '" + reference + "'");
				}
				updateUi();
			}
		}.getAsyncCallback());
	}

	private void updateUi() {
		if (references == null || references.isEmpty()) {
			btnNext.setEnabled(false);
			lblNbReferences.setText("0/0");
			btnPrevious.setEnabled(false);
		}
		else {
			btnNext.setEnabled(true);
			btnPrevious.setEnabled(true);
			if (displayIndex == 0) {
				btnPrevious.setEnabled(false);
			}

			if (displayIndex >= references.size() - 1) {
				btnNext.setEnabled(false);
			}
			lblNbReferences.setText((displayIndex+1) + "/" + references.size());
		}
	}
	
	@UiHandler("btnPrevious")
	public void onPreviousClick(ClickEvent event) {
		displayIndex--;
		txtStep.setText(references.get(displayIndex));
		updateUi();
	}
	
	@UiHandler("btnNext")
	public void onNextClick(ClickEvent event) {
		displayIndex++;
		txtStep.setText(references.get(displayIndex));
		updateUi();
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
