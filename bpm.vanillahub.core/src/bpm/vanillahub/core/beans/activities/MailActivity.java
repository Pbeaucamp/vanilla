package bpm.vanillahub.core.beans.activities;

import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.core.beans.activities.attributes.Email;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.workflow.commons.beans.TypeActivity;

public class MailActivity extends ActivityWithResource<ServerMail> {

	private String message;
	private List<User> users;
	private List<Email> emails;

	private boolean joinLog = false;
	private boolean sendOnlyIfError = false;

	public MailActivity() { }
	
	public MailActivity(String name) {
		super(TypeActivity.MAIL, name);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public boolean isJoinLog() {
		return joinLog;
	}

	public void setJoinLog(boolean joinLog) {
		this.joinLog = joinLog;
	}
	
	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
	
	public boolean sendOnlyIfError() {
		return sendOnlyIfError;
	}
	
	public void setSendOnlyIfError(boolean sendOnlyIfError) {
		this.sendOnlyIfError = sendOnlyIfError;
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0 && message != null && !message.isEmpty() && ((users != null && !users.isEmpty()) || (emails != null && !emails.isEmpty()));
	}
}
