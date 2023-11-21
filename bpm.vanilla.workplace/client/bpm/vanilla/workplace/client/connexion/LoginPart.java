package bpm.vanilla.workplace.client.connexion;

import bpm.vanilla.workplace.client.Bpm_vanilla_workplace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPart extends Composite {

	private static LoginPartUiBinder uiBinder = GWT.create(LoginPartUiBinder.class);

	interface StyleLogin extends CssResource {
		String imageButton();
	}
	
	@UiField
	Label lblTitle, lblLogin, lblPassword;
	
	@UiField
	Button btnOk, btnCancel;
	
	@UiField
	TextBox txtUsername;
	
	@UiField
	PasswordTextBox txtPassword;
	
	@UiField
	StyleLogin style;

	interface LoginPartUiBinder extends UiBinder<Widget, LoginPart> {
	}
	
//	private Bpm_vanilla_workplace mainCompParent;

	public LoginPart(Bpm_vanilla_workplace mainCompParent) {
		initWidget(uiBinder.createAndBindUi(this));
//		this.mainCompParent = mainCompParent;
		
		lblTitle.setText(Bpm_vanilla_workplace.LBL.Welcome());
		lblLogin.setText(Bpm_vanilla_workplace.LBL.Login());
		lblPassword.setText(Bpm_vanilla_workplace.LBL.Password());
		
//		imgOk.setResource(FMUserWebImage.INSTANCE.apply());
//		imgOk.addStyleName(style.imageButton());
	}
	
	@UiHandler("btnOk")
	public void onClick(ClickEvent event) {
//		final String username = txtUsername.getText();
//		final String password = txtPassword.getText();
//		
//		mainCompParent.setWaitPart(true);
//		
//		FMUserWeb.CONNECTION_SERVICES.authentifyUser(username, password, false, new AsyncCallback<UserGroupDTO>() {
//			@Override
//			public void onSuccess(UserGroupDTO result) {
//				LoginPart.this.removeFromParent();
//				mainCompParent.setWaitPart(false);
//				
//				if(result != null) {
//					RepositoryGroupSelectionPart grRepSelPart = new RepositoryGroupSelectionPart(mainCompParent, result);
//					RootPanel.get("main").add(grRepSelPart);
//				}
//				else {
//					ConnectionErrorDialog dial = new ConnectionErrorDialog();
//					dial.show();
//					dial.center();
//				}
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				mainCompParent.setWaitPart(false);
//				LoginPart.this.removeFromParent();
//				
//				ConnectionErrorDialog dial = new ConnectionErrorDialog();
//				dial.setGlassEnabled(true);
//				dial.center();
//			}
//		});
	}
}
