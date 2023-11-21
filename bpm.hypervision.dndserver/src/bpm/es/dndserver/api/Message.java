package bpm.es.dndserver.api;

import bpm.es.dndserver.Messages;

public class Message {

	public static final int ERROR = 0;
	public static final int WARNING = 1;
	
	private int itemId;
	private String itemName;
	private int level;
	
	private String message;

	public Message(int itemId, String itemName, int level, String message) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.level = level;
		this.message = message;
	}

	public int getItemId() {
		return itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public int getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (level == ERROR){
			b.append(Messages.Message_0);
		}
		else{
			b.append(Messages.Message_1);
		}
		if (itemName != null){
			b.append(itemName);
		}
		b.append(" (" + itemId + ") : "); //$NON-NLS-1$ //$NON-NLS-2$
		b.append(getMessage());
		
		
		return b.toString();
	}
}
