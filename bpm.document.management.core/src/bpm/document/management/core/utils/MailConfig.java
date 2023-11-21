package bpm.document.management.core.utils;

/**
 * read the doc on the interface
 * @author manu
 *
 */
public class MailConfig {

	private String recipient;
	private String sender;
	private String text;
	private String title;
	
	private boolean isRichText;
	
	public MailConfig() {
		
	}
	
	
	public MailConfig(String recipient, String sender, String text, String title, boolean isRichText) {
		this.recipient = recipient;
		this.sender = sender;
		this.text = text;
		this.title = title;
		this.isRichText = isRichText;
	}

	public String getTitle() {
		return title;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getText() {
		return text;
	}

	public String getSender() {
		return sender;
	}

	public boolean isRichText() {
		return isRichText;
	}
}
