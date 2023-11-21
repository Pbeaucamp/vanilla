package bpm.vanilla.workplace.client.admin.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DialogInformation extends DialogBox {

	private static DialogInformationUiBinder uiBinder = GWT
			.create(DialogInformationUiBinder.class);

	interface DialogInformationUiBinder extends
			UiBinder<Widget, DialogInformation> {
	}
	
	@UiField
	Label lblInformations;
	
	@UiField
	Button button;

	public DialogInformation(String informations) {
		this.setWidget(uiBinder.createAndBindUi(this));
		
		this.setText(informations);
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		this.hide();
	}
}
