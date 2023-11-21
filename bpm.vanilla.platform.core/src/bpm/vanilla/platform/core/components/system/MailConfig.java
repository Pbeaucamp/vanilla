package bpm.vanilla.platform.core.components.system;

/**
 * read the doc on the interface
 * @author manu
 *
 */
public class MailConfig implements IMailConfig {

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

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public boolean isRichText() {
		return isRichText;
	}
}
