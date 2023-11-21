package bpm.vanillahub.web.client.dialogs;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.services.ResourcesService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChangePasswordDialog extends AbstractDialogBox {

	private static ChangePasswordDialogUiBinder uiBinder = GWT.create(ChangePasswordDialogUiBinder.class);

	interface ChangePasswordDialogUiBinder extends UiBinder<Widget, ChangePasswordDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	TextHolderBox txtPassword, txtNewPassword, txtNewPasswordConfirm;

	private IManager<User> manager;
	private User user;
	private boolean withoutConfirm;

	public ChangePasswordDialog(IManager<User> manager, User user, boolean withoutConfirm) {
		super(Labels.lblCnst.ChangePassword(), false, true);
		this.user = user;
		this.withoutConfirm = withoutConfirm;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.Confirmation(), okHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		if (withoutConfirm) {
			txtPassword.setVisible(false);
		}
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ChangePasswordDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
//			String password = txtPassword.getText();
			String newPassword = txtNewPassword.getText();
			String confirmPassword = txtNewPasswordConfirm.getText();

			if (!newPassword.isEmpty() && (newPassword.equals(confirmPassword))) {

				showWaitPart(true);
				if (withoutConfirm) {
					user.setPassword(newPassword);

					updateUser(user);
				}
				else {
//					ResourcesService.Connect.getInstance().checkPassword(user, password, new GwtCallbackWrapper<Boolean>(ChangePasswordDialog.this, true) {
//
//						@Override
//						public void onSuccess(Boolean result) {
//							if (result) {
//								updateUser(user);
//							}
//							else {
//								MessageHelper.openMessageDialog(Labels.lblCnst.Information(), Labels.lblCnst.WrongPassword());
//							}
//						}
//					}.getAsyncCallback());
				}

				user.setPassword(newPassword);
			}
			else if (newPassword.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Error(), Labels.lblCnst.PasswordEmpty());
			}
			else {
				MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Error(), Labels.lblCnst.PasswordNotMatch());
			}
		}
	};

	private void updateUser(User user) {
		ResourcesService.Connect.getInstance().manageUser(user, true, new GwtCallbackWrapper<User>(ChangePasswordDialog.this, true) {

			@Override
			public void onSuccess(User result) {
				hide();

				manager.loadResources();
			}
		}.getAsyncCallback());
	}
}
