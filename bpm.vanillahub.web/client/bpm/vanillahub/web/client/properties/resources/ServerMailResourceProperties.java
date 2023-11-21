package bpm.vanillahub.web.client.properties.resources;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.web.client.I18N.Labels;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class ServerMailResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private ServerMail cible;

	private VariablePropertiesText txtUrl, txtPort, txtFromMail;
	private PropertiesText txtLogin, txtPassword;
	private CheckBox checkSecured, checkTls;

	private boolean isNameValid = true;

	public ServerMailResourceProperties(NameChecker dialog, IResourceManager resourceManager, ServerMail myServerMail) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, myServerMail != null ? myServerMail.getId() : 0, myServerMail != null ? myServerMail.getName() : Labels.lblCnst.ServerMail(), true, true);
		this.cible = myServerMail != null ? myServerMail : new ServerMail(Labels.lblCnst.ServerMail());

		setNameChecker(dialog);
		setNameChanger(this);

		txtUrl = addVariableText(Labels.lblCnst.SmtpHost(), cible.getSmtpHostVS(), WidgetWidth.SMALL_PX, null);
		txtPort = addVariableText(LabelsCommon.lblCnst.Port(), cible.getPortVS(), WidgetWidth.PORT, null);
		txtFromMail = addVariableText(Labels.lblCnst.FromMail(), cible.getFromEmailVS(), WidgetWidth.SMALL_PX, null);

		checkSecured = addCheckbox(LabelsCommon.lblCnst.Secured(), cible.isSecured(), securedChangeHandler);
		checkTls = addCheckbox(Labels.lblCnst.TLS(), cible.isTls(), tlsChangeHandler);

		txtLogin = addText(LabelsCommon.lblCnst.Login(), cible.getLogin(), WidgetWidth.SMALL_PX, false);
		txtPassword = addText(LabelsCommon.lblCnst.Password(), cible.getPassword(), WidgetWidth.SMALL_PX, true);

		if (!cible.isSecured()) {
			txtLogin.setEnabled(false);
			txtPassword.setEnabled(false);
		}
		
		checkName(getTxtName(), cible.getName());
	}

	private ValueChangeHandler<Boolean> securedChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			cible.setSecured(event.getValue());

			txtLogin.setEnabled(event.getValue());
			txtPassword.setEnabled(event.getValue());
			
			if(!event.getValue()) {
				checkTls.setValue(false);
			}
		}
	};

	private ValueChangeHandler<Boolean> tlsChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			cible.setTls(event.getValue());

			if(event.getValue()) {
				checkSecured.setValue(true);
				
				txtLogin.setEnabled(event.getValue());
				txtPassword.setEnabled(event.getValue());
			}
			else {
				txtLogin.setEnabled(checkSecured.getValue());
				txtPassword.setEnabled(checkSecured.getValue());
			}
		}
	};

	@Override
	public boolean isValid() {
		return isNameValid;
	}

	@Override
	public Resource buildItem() {
		VariableString url = txtUrl.getVariableText();
		VariableString port = txtPort.getVariableText();
		VariableString fromEmail = txtFromMail.getVariableText();
		
		String login = txtLogin.getText();
		String password = txtPassword.getText();

		cible.setSmtpHost(url);
		cible.setPort(port);
		cible.setFromEmail(fromEmail);
		cible.setLogin(login);
		cible.setPassword(password);
		
		return cible;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		cible.setName(value);
	}
}
