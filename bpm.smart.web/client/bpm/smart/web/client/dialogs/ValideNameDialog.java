package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.smart.web.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ValideNameDialog extends AbstractDialogBox {
	private static ValideNameUiBinder uiBinder = GWT
			.create(ValideNameUiBinder.class);

	interface ValideNameUiBinder extends
			UiBinder<Widget, ValideNameDialog> {
	}

	@UiField
	TextBox txtName;

	private boolean isConfirm;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ValideNameDialog() {
		super(lblCnst.NewScriptName(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Validate(), okHandler, lblCnst.Cancel(), cancelHandler);

	}
	
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			ValideNameDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = false;
			ValideNameDialog.this.hide();
		}
	};
	
	public String getName(){
		return txtName.getText();
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	public void setConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
	}
	
}
