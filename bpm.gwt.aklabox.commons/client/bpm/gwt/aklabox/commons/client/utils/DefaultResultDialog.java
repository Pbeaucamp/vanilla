package bpm.gwt.aklabox.commons.client.utils;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomDialog;
import bpm.gwt.aklabox.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DefaultResultDialog extends CustomDialog {

	private static DefaultResultDialogUiBinder uiBinder = GWT.create(DefaultResultDialogUiBinder.class);

	interface DefaultResultDialogUiBinder extends UiBinder<Widget, DefaultResultDialog> {
	}
	
	public static enum TypeMessage {
	  success ("success"),
	  failure ("failure"),
	  warning ("warning"),
	  availability ("availability");
	   
	  private String name = "";
	   
	  //Constructeur
	  TypeMessage(String name){
	    this.name = name;
	  }
	   
	  public String toString(){
	    return name;
	  }
	}

	@UiField
	Button btnClose, btnOk;
	@UiField
	Label lblMessage, title;
	@UiField
	Image imgLogo;

	public DefaultResultDialog(String message, String type) {
		super("");
		setWidget(uiBinder.createAndBindUi(this));
		lblMessage.setText(message);
		if (type.equals("success")) {
			imgLogo.setResource(CommonImages.INSTANCE.validate());
			//imgLogo.setUrl("webapps/aklabox_files/images/logo_header_defaultResult_success.png");
			title.setText(LabelsConstants.lblCnst.Success());
			//lblMessage.setStyleName("successMessage");
			btnClose.setVisible(false);
		}
		else if (type.equals("failure")) {
			imgLogo.setResource(CommonImages.INSTANCE.logo_header_defaultResult_failure());
			//imgLogo.setUrl("webapps/aklabox_files/images/logo_header_defaultResult_failure.png");
			lblMessage.setStyleName("failureMessage");
			title.setVisible(false);
			btnOk.setVisible(false);
		}
		else if (type.equals("warning")) {
			imgLogo.setResource(CommonImages.INSTANCE.logo_header_defaultResult_warning());
			//imgLogo.setUrl("webapps/aklabox_files/images/logo_header_defaultResult_warning.png");
			lblMessage.setStyleName("warningMessage");
			title.setVisible(false);
			btnOk.setVisible(false);
		}
		else if (type.equals("availability")) {
			imgLogo.setResource(CommonImages.INSTANCE.logo_header_defaultResult_availability());
			//imgLogo.setUrl("webapps/aklabox_files/images/logo_header_defaultResult_availability.png");
			lblMessage.setStyleName("availabilityMessage");
			title.setVisible(false);
			btnOk.setVisible(false);
		}
	}

	@UiHandler("btnClose")
	public void onClose(ClickEvent e) {
		this.hide();
	}
	
	@UiHandler("btnOk")
	public void onOk(ClickEvent e) {
		this.hide();
	}
}
