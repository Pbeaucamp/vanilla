package bpm.vanilla.portal.client.dialog;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProfileDialog extends AbstractDialogBox {

	private static ProfileDialogUiBinder uiBinder = GWT.create(ProfileDialogUiBinder.class);

	interface ProfileDialogUiBinder extends UiBinder<Widget, ProfileDialog> {
	}
	
	interface MyStyle extends CssResource {
		String image();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Image imgUser;
	
	@UiField
	TextBox txtName, txtSurname, txtPrivateEmail, txtBusinessEmail, txtPhone, txtCellular;
	
	@UiField
	TextBox txtOldPassword, txtNewPassword, txtConfirmNewPassword;
	
	@UiField
	CustomCheckbox cbWidgets;
	
	@UiField
	FormPanel formPanel;
	
	@UiField
	FileUpload fileUpload;

	private User user;
	
	public ProfileDialog(User user) {
		super(ToolsGWT.lblCnst.Profil(), false, true);
		this.user = user;
		
		setWidget(uiBinder.createAndBindUi(this));
		increaseZIndex();
		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
		
		formPanel.setAction(GWT.getHostPageBaseURL() +  "VanillaPortail/ImageUserServlet?userId=" + user.getId());
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(submitCompleteHandler);
		
		fillInfos(user);
	}

	private void fillInfos(User user) {
		txtName.setText(user.getName());
		txtSurname.setText(user.getSurname());
		txtPrivateEmail.setText(user.getPrivateMail());
		txtBusinessEmail.setText(user.getBusinessMail());
		txtPhone.setText(user.getTelephone());
		txtCellular.setText(user.getCellular());
		cbWidgets.setValue(user.isWidget());

		LoginService.Connect.getInstance().getUserImg(user.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.image());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
				}
				else {
					imgUser.setUrl(PortalImage.INSTANCE.user_profile().getSafeUri());
				}
				imgUser.addStyleName(style.image());
			}
		});
	}

	private boolean checkPassword(String oldPassword, String newPassword, String confirmPassword) {
		if(!newPassword.isEmpty() && !confirmPassword.isEmpty()) {
			if(newPassword.equals(confirmPassword)) {
				return true;
			}
			else {
				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.PasswordNotMatch());
				return false;
			}
		}
		else {
			MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.PasswordEmpty());
			return false;
		}
	}
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		
		public void onSubmitComplete(SubmitCompleteEvent event) {
			biPortal.get().showWaitPart(false);
			
			hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			String name = txtName.getText();
			String surname = txtSurname.getText();
			String privateMail = txtPrivateEmail.getText();
			String businessMail = txtBusinessEmail.getText();
			String phone = txtPhone.getText();
			String cellular = txtCellular.getText();
			
			boolean changePassword = false;
			String oldPassword = txtOldPassword.getText();
			String newPassword = txtNewPassword.getText();
			String confirmPassword = txtConfirmNewPassword.getText();
			
			if(!oldPassword.isEmpty()) {
				if (checkPassword(oldPassword, newPassword, confirmPassword)) {
					changePassword = true;
				}
				else {
					return;
				}
			}
				
			user.setName(name);
			user.setSurname(surname);
			user.setPrivateMail(privateMail);
			user.setBusinessMail(businessMail);
			user.setTelephone(phone);
			user.setCellular(cellular);
			user.setWidget(cbWidgets.getValue());
			
			biPortal.get().showWaitPart(true);
			
			AdminService.Connect.getInstance().updateUser(user, changePassword, oldPassword, newPassword, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					if(fileUpload.getFilename() != null  && !fileUpload.getFilename().isEmpty()) {
						formPanel.submit();
					}
					else {
						biPortal.get().showWaitPart(false);

						hide();
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					biPortal.get().showWaitPart(false);
					
					hide();
					
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());
				}
			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
