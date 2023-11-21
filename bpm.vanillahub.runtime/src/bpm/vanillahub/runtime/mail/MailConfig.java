package bpm.vanillahub.runtime.mail;

import java.util.Locale;

import bpm.vanillahub.core.Constants;
import bpm.vanillahub.runtime.i18N.Labels;

public class MailConfig {

	private String recipient;
	private String text;
	private String title;

	public MailConfig(String recipient, String title, String text, String workflowName, String targetName, String currentUsername) {
		this.recipient = recipient;
		this.title = title;
		this.text = prepareMail(text, workflowName, targetName, currentUsername);
	}

	public String getTitle() {
		return title;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getText(Locale locale, String startDate, String endDate, boolean hasErrors) {
		return prepareMail(locale, text, startDate, endDate, hasErrors);
	}

	private String prepareMail(String text, String workflowName, String targetName, String currentUsername) {
		if(text == null)
			return "";
		
		text = text.replace(Constants.MAIL_WORKFLOW_NAME, workflowName);
		text = text.replace(Constants.MAIL_TARGET_USERNAME, targetName);
		text = text.replace(Constants.MAIL_CURRENT_USERNAME, currentUsername);
		return text;
	}
	
	private String prepareMail(Locale locale, String text, String startDate, String endDate, boolean hasErrors) {
		if(text == null)
			return "";
		
		text = text.replace(Constants.MAIL_START_DATE, startDate);
		text = text.replace(Constants.MAIL_END_DATE, endDate);
		text = text.replace(Constants.MAIL_HAS_ERRORS, hasErrors ? Labels.getLabel(locale, Labels.Yes) : Labels.getLabel(locale, Labels.No));
		return text;
	}
}
