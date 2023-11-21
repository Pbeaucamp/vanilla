package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.ServerMail;

public class ServerMailManager extends ResourceManager<ServerMail> {

	private static final String MAIL_FILE_NAME = "mails.xml";

	public ServerMailManager(String filePath) {
		super(filePath, MAIL_FILE_NAME, "Mails");
	}
	
	@Override
	protected void manageResourceForAdd(ServerMail resource) { }
	
	@Override
	protected void manageResourceForModification(ServerMail newResource, ServerMail oldResource) {
		String name = oldResource.getName();
		VariableString url = oldResource.getSmtpHostVS();
		VariableString port = oldResource.getPortVS();
		VariableString fromEmail = oldResource.getFromEmailVS();
		boolean secured = oldResource.isSecured();
		boolean tls = oldResource.isTls();
		String login = oldResource.getLogin();
		String password = oldResource.getPassword();
		
		newResource.updateInfo(name, url, port, fromEmail, secured, tls, login, password);
	}
}
