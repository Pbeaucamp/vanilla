package bpm.vanillahub.web.client.properties.resources;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.ValueChangeHandlerWithError;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.UserDialog;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;

public class UserResourceProperties extends PropertiesPanel<User> implements NameChanger {

	private User user;

	private PropertiesText txtPassword, txtConfirmPassword, txtEmail, txtFonction;
	private CheckBox checkIsAdmin;

	private boolean edit;
	private boolean isNameValid = true;
	private boolean isEmailValid = false;

	public UserResourceProperties(UserDialog dialog, IResourceManager resourceManager, User myUser) {
		super(resourceManager, LabelsCommon.lblCnst.Login(), WidgetWidth.LARGE_PX, myUser != null ? myUser.getId() : 0, myUser != null ? myUser.getName() : "", false, true);
		this.edit = myUser != null;
		this.user = myUser != null ? myUser : new User();

		setNameChecker(dialog);
		setNameChanger(this);

		txtPassword = addText(LabelsCommon.lblCnst.Password(), user.getPassword(), WidgetWidth.LARGE_PX, true);
		txtConfirmPassword = addText(Labels.lblCnst.ConfirmPassword(), user.getPassword(), WidgetWidth.LARGE_PX, true);

		txtEmail = addText(Labels.lblCnst.Email(), user.getBusinessMail(), WidgetWidth.LARGE_PX, false);
		txtEmail.addValueChangeHandler(txtEmail, emailHandler);
		txtFonction = addText(Labels.lblCnst.Fonction(), user.getFunction(), WidgetWidth.LARGE_PX, false);

		checkIsAdmin = addCheckbox(Labels.lblCnst.Administrator(), user.isSuperUser(), null);

		checkName(getTxtName(), user.getName());
		checkEmail(txtEmail, user.getBusinessMail());

		if (edit) {
			txtPassword.setVisible(false);
			txtConfirmPassword.setVisible(false);
		}
	}

	private ValueChangeHandlerWithError emailHandler = new ValueChangeHandlerWithError() {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			checkEmail(getText(), event.getValue());
		}
	};
	
	private void checkEmail(PropertiesText text, String email) {
		if (!isValid(email)) {
			isEmailValid = false;
			text.setTxtError(Labels.lblCnst.EmailNotValid());
		}
		else {
			isEmailValid = true;
			text.setTxtError("");
		}
	}

	public boolean isValid(String value) {
		if (value == null)
			return true;

		String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
		return value.matches(emailPattern);
	}

	@Override
	public boolean isValid() {
		if(!edit) {
			String password = txtPassword.getText();
			String confirmPassword = txtConfirmPassword.getText();
			if (!password.equals(confirmPassword)) {
				txtPassword.setTxtError(Labels.lblCnst.PasswordNotMatch());
				return false;
			}
		}
		return isNameValid && isEmailValid;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		user.setName(value);
	}

	@Override
	public User buildItem() {
		String login = user.getName();
		String password = user.getPassword();
		String email = txtEmail.getText();
		String fonction = txtFonction.getText();
		boolean isAdmin = checkIsAdmin.getValue();
		
		if (edit) {
			user.setLogin(login);
			user.setPassword(password);
			user.setFunction(fonction);
			user.setSuperUser(isAdmin);
		}
		else {
			password = txtPassword.getText();
			user = new User();
			user.setLogin(login);
			user.setPassword(password);
			user.setBusinessMail(email);
			user.setFunction(fonction);
			user.setSuperUser(isAdmin);
		}
		
		return user;
	}
}
