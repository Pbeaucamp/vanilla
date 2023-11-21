package bpm.vanillahub.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.text.RichTextToolbar;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.properties.MailPanel;

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
	
	private MailPanel dialogParent;

	public TextEmailDialog(MailPanel dialogParent, String htmlText) {
		super(Labels.lblCnst.TextEmail(), false, true);
		this.dialogParent = dialogParent;
		
		setWidget(uiBinder.createAndBindUi(this));

		RichTextToolbar toolbar = new RichTextToolbar(richTextArea);
		panelRichTextToolbar.setWidget(toolbar);

		richTextArea.setHTML(htmlText);

		createButtonBar(LabelsCommon.lblCnst.Confirmation(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);
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
