package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;

public class ItemNameDialog extends AbstractDialogBox {

	private static ItemNameDialogUiBinder uiBinder = GWT.create(ItemNameDialogUiBinder.class);

	interface ItemNameDialogUiBinder extends UiBinder<Widget, ItemNameDialog> {
	}
	
	@UiField
	LabelTextBox txtName;
	
	@UiField
	LabelTextArea txtDescription;
	
	private boolean confirm = false;
	
	public ItemNameDialog(String title, boolean showDescription) {
		super(title, false, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		txtDescription.setVisible(showDescription);
	}
	
	public String getName() {
		return txtName.getText();
	}
	
	public String getDescription() {
		return txtDescription.getText();
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			ItemNameDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			ItemNameDialog.this.hide();
		}
	};
}
