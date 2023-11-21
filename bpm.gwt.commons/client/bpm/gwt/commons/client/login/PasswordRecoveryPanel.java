package bpm.gwt.commons.client.login;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomButton;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.shared.utils.PasswordState;
import bpm.vanilla.platform.core.beans.Setting.SettingType;
import bpm.vanilla.platform.core.beans.Settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PasswordRecoveryPanel extends Composite {

	private static PasswordRecoveryPanelUiBinder uiBinder = GWT.create(PasswordRecoveryPanelUiBinder.class);

	interface PasswordRecoveryPanelUiBinder extends UiBinder<Widget, PasswordRecoveryPanel> {
	}

	interface MyStyle extends CssResource {
		String success();
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox txtLogin, txtPassword, txtConfirmPassword;

	@UiField
	Label lblError;

	@UiField
	CustomButton btnConfirm;

	private IWait waitPanel;

	private Settings settings;
	private String hash;

	private boolean loginConfirmed = false;
	private boolean confirm = false;

	public PasswordRecoveryPanel(IWait waitPanel, String hash) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.hash = hash;

		checkHash();
	}

	private void checkHash() {
		waitPanel.showWaitPart(true);

		LoginService.Connect.getInstance().checkHashCode(hash, new GwtCallbackWrapper<Boolean>(waitPanel, true) {

			@Override
			public void onSuccess(Boolean result) {
				if (!result) {
					confirm = true;
					
					txtLogin.setVisible(false);
					lblError.setText(LabelsConstants.lblCnst.URLIsNotValidAnymoreAskAdmin());
					
					btnConfirm.setText(LabelsConstants.lblCnst.HomePage());
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnConfirm")
	public void onConfirm(ClickEvent event) {
		if (confirm) {
			//TODO redirect
		}
		else if (loginConfirmed) {
			final String newPassword = txtPassword.getText();
			String confirmPassword = txtConfirmPassword.getText();

			PasswordState state = checkPassword(newPassword, confirmPassword);
			if (state == PasswordState.VALID) {
				waitPanel.showWaitPart(true);
				
				LoginService.Connect.getInstance().checkPassword(hash, newPassword, new GwtCallbackWrapper<PasswordState>(waitPanel, false) {

					@Override
					public void onSuccess(PasswordState result) {
						if (result == PasswordState.VALID) {
							LoginService.Connect.getInstance().changePassword(hash, newPassword, new GwtCallbackWrapper<Void>(waitPanel, true) {
	
								@Override
								public void onSuccess(Void result) {
									confirm = true;
	
									lblError.setText(LabelsConstants.lblCnst.PasswordChangeSuccess());
									lblError.setStyleName(style.success());
	
									txtPassword.setEnabled(false);
									txtConfirmPassword.setEnabled(false);
	
									btnConfirm.setText(LabelsConstants.lblCnst.HomePage());
								}
							}.getAsyncCallback());
						}
						else {
							waitPanel.showWaitPart(false);
							
							updateUi(result);
						}
					}
				}.getAsyncCallback());
			}
			else {
				updateUi(state);
			}
		}
		else {
			String login = txtLogin.getText();
			if (login.isEmpty()) {
				lblError.setText(LabelsConstants.lblCnst.LoginCannotBeEmpty());
				return;
			}

			LoginService.Connect.getInstance().checkLoginWithHash(login, hash, new GwtCallbackWrapper<Settings>(waitPanel, true) {

				@Override
				public void onSuccess(Settings result) {
					if (result != null) {
						loginConfirmed = true;
						settings = result;

						txtLogin.setVisible(false);
						txtPassword.setVisible(true);
						txtConfirmPassword.setVisible(true);
						
						lblError.setText("");
					}
					else {
						lblError.setText(LabelsConstants.lblCnst.LoginIsNotCorrect());
					}
				}
			}.getAsyncCallback());
		}
	}

	private PasswordState checkPassword(String newPassword, String confirmPassword) {
		int minChar = settings.getSettingAsInteger(SettingType.MIN_CHAR_PER_PASSWORD);
		int minNumber = settings.getSettingAsInteger(SettingType.MIN_NUMBER_PER_PASSWORD);

		if (newPassword.isEmpty()) {
			return PasswordState.EMPTY;
		}
		else if (!newPassword.equals(confirmPassword)) {
			return PasswordState.DIFFERENT;
		}
		else if (minChar > 0 && newPassword.length() < minChar) {
			return PasswordState.NOT_ENOUGH_CHAR;
		}
		else if (minNumber > 0) {
			String onlyDigit = newPassword.replaceAll("\\D+", "");
			if (onlyDigit.length() < minNumber) {
				return PasswordState.NOT_ENOUGH_NUMBER;
			}
		}
		return PasswordState.VALID;
	}
	
	private void updateUi(PasswordState state) {
		int minChar = settings.getSettingAsInteger(SettingType.MIN_CHAR_PER_PASSWORD);
		int minNumber = settings.getSettingAsInteger(SettingType.MIN_NUMBER_PER_PASSWORD);
		
		switch (state) {
		case EMPTY:
			lblError.setText(LabelsConstants.lblCnst.PasswordCannotBeEmpty());
			break;
		case DIFFERENT:
			lblError.setText(LabelsConstants.lblCnst.PasswordsDoesNotMatch());
			break;
		case NOT_ENOUGH_CHAR:
			lblError.setText(LabelsConstants.lblCnst.PasswordMustBeAtLeast() + " " + minChar + " " + LabelsConstants.lblCnst.CharactersLong());
			break;
		case NOT_ENOUGH_NUMBER:
			lblError.setText(LabelsConstants.lblCnst.PasswordMustContainsAtLeast() + " " + minNumber + " " + LabelsConstants.lblCnst.Digits());
			break;
		case SAME_AS_BEFORE:
			lblError.setText(LabelsConstants.lblCnst.PasswordCannotBeTheSameAsBefore());
			break;
		case NOT_ALLOWED:
			lblError.setText(LabelsConstants.lblCnst.ThisWordIsNotAllowedToUseAsAPassword());
			break;
		case VALID:
			lblError.setText("");
			break;

		default:
			lblError.setText("");
			break;
		}
	}
}
