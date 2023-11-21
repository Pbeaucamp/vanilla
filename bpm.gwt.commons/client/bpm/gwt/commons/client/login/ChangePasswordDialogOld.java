package bpm.gwt.commons.client.login;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChangePasswordDialogOld extends AbstractDialogBox {

	private static ChangePasswordDialogOldUiBinder uiBinder = GWT.create(ChangePasswordDialogOldUiBinder.class);

	interface ChangePasswordDialogOldUiBinder extends UiBinder<Widget, ChangePasswordDialogOld> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	@UiField
	TextHolderBox txtPassword, txtNewPassword, txtNewPasswordConfirm;
	
	private String username;
	private boolean withoutConfirm;

	public ChangePasswordDialogOld(String username, boolean withoutConfirm) {
		super(LabelsConstants.lblCnst.PasswordChange(), false, true);
		this.username = username;
		this.withoutConfirm = withoutConfirm;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		if(withoutConfirm) {
			txtPassword.setVisible(false);
		}
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ChangePasswordDialogOld.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!txtNewPassword.getText().equalsIgnoreCase("") && (txtNewPassword.getText().equals(txtNewPasswordConfirm.getText()))) {

				LoginService.Connect.getInstance().changeUserPassword(username, txtPassword.getText(), txtNewPassword.getText(), withoutConfirm, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						MessageHelper.openMessageErrorWithRedirect(LabelsConstants.lblCnst.Information(), 
							LabelsConstants.lblCnst.PasswordChangeSuccess(), GWT.getHostPageBaseURL());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
					}
				});

				ChangePasswordDialogOld.this.hide();
			}
			else if (txtNewPassword.getText().equalsIgnoreCase("")) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.EmptyPassword());
			}
			else {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.PasswordNotMatch());
			}
		}
	};
}
