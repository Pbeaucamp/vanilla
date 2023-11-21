package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.text.RichTextToolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TextEmailDialog extends AbstractDialogBox {

	private static TextEmailDialogUiBinder uiBinder = GWT.create(TextEmailDialogUiBinder.class);

	interface TextEmailDialogUiBinder extends UiBinder<Widget, TextEmailDialog> {
	}
	
	@UiField
	SimplePanel panelRichTextToolbar;

	@UiField
	RichTextArea richTextArea;
	
	private MailShareDialog dialogParent;

	public TextEmailDialog(MailShareDialog dialogParent, String htmlText) {
		super(LabelsConstants.lblCnst.TextEmail(), false, true);
		this.dialogParent = dialogParent;
		
		setWidget(uiBinder.createAndBindUi(this));

		RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		panelRichTextToolbar.setWidget(toolbar);

		richTextArea.setHTML(htmlText);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			dialogParent.setEmailText(richTextArea.getHTML());
			hide();
		}
	};

}
