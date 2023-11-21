package bpm.vanilla.workplace.client.admin.panel.user;

import bpm.vanilla.workplace.client.images.Images;
import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PanelUserInformation extends Composite {

	private static PanelUserInformationUiBinder uiBinder = GWT
			.create(PanelUserInformationUiBinder.class);

	interface PanelUserInformationUiBinder extends
			UiBinder<Widget, PanelUserInformation> {
	}	
	
	interface MyStyle extends CssResource {
		String username();
		String password();
		String mail();
		String creaDate();
		String btnEdit();
		String btnValid();
	}
	
	@UiField
	Label lblInfos, lblUsername, lblPassword, lblMail, lblCreaDate, creaDate;
	
	@UiField
	TextBox txtUsername, txtMail;
	
	@UiField
	PasswordTextBox txtPassword;
	
	@UiField
	Image btnEditUsername, btnEditPassword, btnEditMail, btnValidUsername, btnValidPassword, btnValidMail;
	
	@UiField
	MyStyle style;
	
	private PlaceWebUser user;

	public PanelUserInformation(PlaceWebUser user, boolean createUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.user = user;
		
		if(!createUser){
			lblUsername.setText(user.getName());
			lblPassword.setText("*************");
			lblMail.setText(user.getMail());
			lblCreaDate.setText(user.getCreationDate() + "");
			
			btnEditUsername.setResource(Images.INSTANCE.write());
			btnEditUsername.addStyleName(style.username());
			btnEditUsername.addStyleName(style.btnEdit());
			
			btnValidUsername.setResource(Images.INSTANCE.valid());
			btnValidUsername.addStyleName(style.username());
			btnValidUsername.addStyleName(style.btnValid());
			btnValidUsername.setVisible(false);
			
			btnEditPassword.setResource(Images.INSTANCE.write());
			btnEditPassword.addStyleName(style.password());
			btnEditPassword.addStyleName(style.btnEdit());
			
			btnValidPassword.setResource(Images.INSTANCE.valid());
			btnValidPassword.addStyleName(style.password());
			btnValidPassword.addStyleName(style.btnValid());
			btnValidPassword.setVisible(false);
			
			btnEditMail.setResource(Images.INSTANCE.write());
			btnEditMail.addStyleName(style.mail());
			btnEditMail.addStyleName(style.btnEdit());
			
			btnValidMail.setResource(Images.INSTANCE.valid());
			btnValidMail.addStyleName(style.mail());
			btnValidMail.addStyleName(style.btnValid());
			btnValidMail.setVisible(false);
			
			txtUsername.setVisible(false);
			txtMail.setVisible(false);
			txtPassword.setVisible(false);
		}
		else {
			lblInfos.setVisible(false);
			
			lblUsername.setVisible(false);
			lblPassword.setVisible(false);
			lblMail.setVisible(false);
			lblCreaDate.setVisible(false);
			
			creaDate.setVisible(false);
			
			btnEditUsername.setVisible(false);
			btnEditPassword.setVisible(false);
			btnEditMail.setVisible(false);
			
			btnValidMail.setVisible(false);
			btnValidPassword.setVisible(false);
			btnValidUsername.setVisible(false);
		}
	}
	
	public PlaceWebUser getUser(){
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		String mail = txtMail.getText();
		if(!username.isEmpty() && !password.isEmpty() && !mail.isEmpty()){
			PlaceWebUser user = new PlaceWebUser();
			user.setName(username);
			user.setPassword(password);
			user.setMail(mail);
			
			return user;
		}
		
		return null;
	}
	
	@UiHandler("btnEditUsername")
	public void onEditUsernameClick(ClickEvent event){
		lblUsername.setVisible(false);
		btnEditUsername.setVisible(false);
		
		txtUsername.setVisible(true);
		btnValidUsername.setVisible(true);
	}
	
	@UiHandler("btnEditPassword")
	public void onEditPasswordClick(ClickEvent event){
		lblPassword.setVisible(false);
		btnEditPassword.setVisible(false);
		
		txtPassword.setVisible(true);
		btnValidPassword.setVisible(true);
	}
	
	@UiHandler("btnEditMail")
	public void onEditMailClick(ClickEvent event){
		lblMail.setVisible(false);
		btnEditMail.setVisible(false);
		
		txtMail.setVisible(true);
		btnValidMail.setVisible(true);
	}
	
	@UiHandler("btnValidUsername")
	public void onValidUsernameClick(ClickEvent event){
		final String username = txtUsername.getText();
		if(!username.isEmpty()){			
			user.setName(username);
			
			AdminService.Connect.getInstance().updateUser(user, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					lblUsername.setVisible(true);
					btnEditUsername.setVisible(true);
					
					txtUsername.setVisible(false);
					btnValidUsername.setVisible(false);
					
					lblUsername.setText(username);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
	
	@UiHandler("btnValidPassword")
	public void onValidPasswordClick(ClickEvent event){
		final String password = txtPassword.getText();
		if(!password.isEmpty()){			
			user.setPassword(password);
			
			AdminService.Connect.getInstance().updateUser(user, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					lblPassword.setVisible(true);
					btnEditPassword.setVisible(true);
					
					txtPassword.setVisible(false);
					btnValidPassword.setVisible(false);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
	
	@UiHandler("btnValidMail")
	public void onValidMailClick(ClickEvent event){
		final String mail = txtMail.getText();
		if(!mail.isEmpty()){			
			user.setMail(mail);
			
			AdminService.Connect.getInstance().updateUser(user, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					lblMail.setVisible(true);
					btnEditMail.setVisible(true);
					
					txtMail.setVisible(false);
					btnValidMail.setVisible(false);
					
					lblMail.setText(mail);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	}
}
