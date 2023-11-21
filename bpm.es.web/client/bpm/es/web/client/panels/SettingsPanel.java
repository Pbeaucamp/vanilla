package bpm.es.web.client.panels;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.services.AdminService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.Setting.SettingType;
import bpm.vanilla.platform.core.beans.Settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPanel extends CompositeWaitPanel {

	private static SettingsPanelUiBinder uiBinder = GWT.create(SettingsPanelUiBinder.class);

	interface SettingsPanelUiBinder extends UiBinder<Widget, SettingsPanel> {
	}
	
	@UiField
	LabelValueTextBox txtMaxInactivityDelay, txtNumberOfTryAllowed, txtInactivationDelay, txtURLFirstLoginValidity, txtURLChangePasswordValidity;
	
	@UiField
	CustomCheckbox checkSendEmailIfTooManyTry, checkNeedAdminValidationToChangePassword;
	
	@UiField
	LabelValueTextBox txtChangePasswordDelay, txtMinCharPassword, txtMinNumberInPassword;
	
	@UiField
	LabelTextArea txtFailedConnection;
	
	@UiField
	CustomCheckbox checkWebKeyboard, checkOTP;

	private Settings settings;
	
	public SettingsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		loadSettings();
	}

	private void loadSettings() {
		showWaitPart(true);
		
		AdminService.Connect.getInstance().getSettings(new GwtCallbackWrapper<Settings>(this, true) {

			@Override
			public void onSuccess(Settings result) {
				settings = result;
				
				loadSettings(settings);
			}
		}.getAsyncCallback());
	}

	private void loadSettings(Settings settings) {
		String maxInactivityDelay = settings.getSetting(SettingType.USER_MAX_INACTIVITY_DELAY);
		String numberOfTryAllowed = settings.getSetting(SettingType.AUTHENTICATION_MAX_TRY);
		String inactivationDelay = settings.getSetting(SettingType.USER_MAX_INACTIVITY_DELAY);
		String urlFirstLoginValidity = settings.getSetting(SettingType.MAIL_FIRST_LOGIN_VALIDITY_DELAY);
		String urlChangePasswordValidity = settings.getSetting(SettingType.MAIL_CHANGE_PASSWORD_VALIDITY_DELAY);
		
		boolean sendMailIfTooManyTry = settings.getSettingAsBoolean(SettingType.SEND_MAIL_IF_TOO_MANY_TRY);
		boolean needAdminToChangePassword = settings.getSettingAsBoolean(SettingType.NEED_ADMIN_TO_CHANGE_PASSWORD);
		String changePasswordDelay = settings.getSetting(SettingType.CHANGE_PASSWORD_DELAY);
		String minCharPerPassword = settings.getSetting(SettingType.MIN_CHAR_PER_PASSWORD);
		String minNumberPerPassword = settings.getSetting(SettingType.MIN_NUMBER_PER_PASSWORD);

		String messageFailedConnection = settings.getSetting(SettingType.MESSAGE_FAILED_CONNECTION);

		boolean useWebKeyboard = settings.getSettingAsBoolean(SettingType.USE_WEB_KEYBOARD);
		boolean useOTP = settings.getSettingAsBoolean(SettingType.USE_OTP);
		
		txtMaxInactivityDelay.setText(maxInactivityDelay);
		txtNumberOfTryAllowed.setText(numberOfTryAllowed);
		txtInactivationDelay.setText(inactivationDelay);
		txtURLFirstLoginValidity.setText(urlFirstLoginValidity);
		txtURLChangePasswordValidity.setText(urlChangePasswordValidity);
		
		checkSendEmailIfTooManyTry.setValue(sendMailIfTooManyTry);
		checkNeedAdminValidationToChangePassword.setValue(needAdminToChangePassword);
		
		txtChangePasswordDelay.setText(changePasswordDelay);
		txtMinCharPassword.setText(minCharPerPassword);
		txtMinNumberInPassword.setText(minNumberPerPassword);
		
		txtFailedConnection.setText(messageFailedConnection);
		
		checkWebKeyboard.setValue(useWebKeyboard);
		checkOTP.setValue(useOTP);
	}

	@UiHandler("btnCancel")
	public void onCancel(ClickEvent event) {
		loadSettings(settings);
	}

	@UiHandler("btnConfirm")
	public void onConfirm(ClickEvent event) {
		showWaitPart(true);
		
		int maxInactivityDelay = Integer.parseInt(txtMaxInactivityDelay.getText());
		int numberOfTryAllowed = Integer.parseInt(txtNumberOfTryAllowed.getText());
		int inactivationDelay = Integer.parseInt(txtInactivationDelay.getText());
		int urlFirstLoginValidity = Integer.parseInt(txtURLFirstLoginValidity.getText());
		int urlChangePasswordValidity = Integer.parseInt(txtURLChangePasswordValidity.getText());
		
		boolean sendMailIfTooManyTry = checkSendEmailIfTooManyTry.getValue();
		boolean needAdminToChangePassword = checkNeedAdminValidationToChangePassword.getValue();
		
		int changePasswordDelay = Integer.parseInt(txtChangePasswordDelay.getText());
		int minCharPerPassword = Integer.parseInt(txtMinCharPassword.getText());
		int minNumberPerPassword = Integer.parseInt(txtMinNumberInPassword.getText());
		
		String messageFailedConnection = txtFailedConnection.getText();
		
		boolean useWebKeyboard = checkWebKeyboard.getValue();
		boolean useOTP = checkOTP.getValue();

		settings.updateSetting(SettingType.USER_MAX_INACTIVITY_DELAY, maxInactivityDelay);
		settings.updateSetting(SettingType.AUTHENTICATION_MAX_TRY, numberOfTryAllowed);
		settings.updateSetting(SettingType.USER_MAX_INACTIVITY_DELAY, inactivationDelay);
		settings.updateSetting(SettingType.MAIL_FIRST_LOGIN_VALIDITY_DELAY, urlFirstLoginValidity);
		settings.updateSetting(SettingType.MAIL_CHANGE_PASSWORD_VALIDITY_DELAY, urlChangePasswordValidity);
		
		settings.updateSetting(SettingType.SEND_MAIL_IF_TOO_MANY_TRY, sendMailIfTooManyTry);
		settings.updateSetting(SettingType.NEED_ADMIN_TO_CHANGE_PASSWORD, needAdminToChangePassword);
		settings.updateSetting(SettingType.CHANGE_PASSWORD_DELAY, changePasswordDelay);
		settings.updateSetting(SettingType.MIN_CHAR_PER_PASSWORD, minCharPerPassword);
		settings.updateSetting(SettingType.MIN_NUMBER_PER_PASSWORD, minNumberPerPassword);

		settings.updateSetting(SettingType.MESSAGE_FAILED_CONNECTION, messageFailedConnection);

		settings.updateSetting(SettingType.USE_WEB_KEYBOARD, useWebKeyboard);
		settings.updateSetting(SettingType.USE_OTP, useOTP);
		
		AdminService.Connect.getInstance().updateSettings(settings, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.SettingsHasBeenUpdated());
			}
		}.getAsyncCallback());
	}
}
