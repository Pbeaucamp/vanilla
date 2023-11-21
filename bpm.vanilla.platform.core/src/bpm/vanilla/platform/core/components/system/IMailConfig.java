package bpm.vanilla.platform.core.components.system;

/**
 * Attachements are sent straight thru the method call
 * 
 * @author manu
 *
 */
public interface IMailConfig {

	/**
	 * this is not the address of the sender!
	 * this is the name of the sender
	 * 
	 * @return
	 */
	public String getSender();
	
	/**
	 * this is the proper email of the recipient
	 * @return
	 */
	public String getRecipient();
	
	/**
	 * The title of the email
	 * @return
	 */
	public String getTitle();
	
	/**
	 * the raw text
	 * 
	 * @return
	 */
	public String getText();
	
	/**
	 * Is it rich text (ie=html)
	 * @return
	 */
	public boolean isRichText();
}
