package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConnectionAklaboxDialog extends AbstractDialogBox {

	private static ConnectionAklaboxDialogUiBinder uiBinder = GWT.create(ConnectionAklaboxDialogUiBinder.class);

	interface ConnectionAklaboxDialogUiBinder extends UiBinder<Widget, ConnectionAklaboxDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	TextHolderBox txtLogin, txtPassword;
	
	private ILoginAklabox loginAklabox;
	
	public ConnectionAklaboxDialog(ILoginAklabox loginAklabox, String login, String password) {
		super(LabelsConstants.lblCnst.ConnectToAklabox(), false, true);
		this.loginAklabox = loginAklabox;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		if(login != null && !login.isEmpty()) {
			txtLogin.setText(login);
			txtPassword.setText(password);
		}
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
			String login = txtLogin.getText();
			String password = txtPassword.getText();
			
			loginAklabox.connect(login, password);
			
			hide();
		}
	};

}
