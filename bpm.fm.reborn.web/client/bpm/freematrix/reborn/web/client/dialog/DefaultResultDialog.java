package bpm.freematrix.reborn.web.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DefaultResultDialog extends DialogBox {

	private static DefaultResultDialogUiBinder uiBinder = GWT
			.create(DefaultResultDialogUiBinder.class);

	interface DefaultResultDialogUiBinder extends
			UiBinder<Widget, DefaultResultDialog> {
	}

	@UiField
	Button btnClose;
	@UiField
	Label lblMessage;
	@UiField
	Image imgLogo;

	public DefaultResultDialog(String message, String type) {
		setWidget(uiBinder.createAndBindUi(this));
		lblMessage.setText(message);
		if(type.equals("success")) {
			imgLogo.setUrl("webapps/freematrix_files/images/logo_header_defaultResult_success.png");
			lblMessage.setStyleName("successMessage");
		}else if(type.equals("failure")) {
			imgLogo.setUrl("webapps/freematrix_files/images/logo_header_defaultResult_failure.png");
			lblMessage.setStyleName("failureMessage");
		}else if(type.equals("warning")){
			imgLogo.setUrl("webapps/freematrix_files/images/logo_header_defaultResult_warning.png");
			lblMessage.setStyleName("warningMessage");
		}else if(type.equals("availability")) {
			imgLogo.setUrl("webapps/freematrix_files/images/logo_header_defaultResult_availability.png");
			lblMessage.setStyleName("availabilityMessage");
		}
	}
	

	@UiHandler("btnClose")
	public void onClose(ClickEvent e) {
		this.hide();
	}
}
