package bpm.gwt.commons.client.login;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class PasswordChangeDemandDialog extends AbstractDialogBox {

	private static PasswordChangeDemandDialogUiBinder uiBinder = GWT.create(PasswordChangeDemandDialogUiBinder.class);

	interface PasswordChangeDemandDialogUiBinder extends UiBinder<Widget, PasswordChangeDemandDialog> {
	}
	
	@UiField
	LabelTextBox txtLogin;
	
	private IWait waitPanel;
	
	public PasswordChangeDemandDialog(IWait waitPanel) {
		super(LabelsConstants.lblCnst.ForgotPassword(), false, true);
		this.waitPanel = waitPanel;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String login = txtLogin.getText();
			if (login.isEmpty()) {
				return;
			}
			
			waitPanel.showWaitPart(true);
			
			String webappUrl = GWT.getHostPageBaseURL();
			
			LoginService.Connect.getInstance().forgotPassword(webappUrl, login, false, new GwtCallbackWrapper<Boolean>(waitPanel, true) {

				@Override
				public void onSuccess(Boolean result) {
					hide();
					if (result) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.EmailSendToResetPassword());
					}
					else {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedAdminConfirmationToResetPassword());
					}
				}
			}.getAsyncCallback());
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			PasswordChangeDemandDialog.this.hide();
		}
	};
}
