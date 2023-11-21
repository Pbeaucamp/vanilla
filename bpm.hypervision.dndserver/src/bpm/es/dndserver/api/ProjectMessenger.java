package bpm.es.dndserver.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectMessenger {

	private HashMap<Integer, List<Message>> messageByItem = new HashMap<Integer, List<Message>>();
	
	private HashMap<Integer, String> idToName = new HashMap<Integer, String>();
	
	private boolean hasError;
	
	public ProjectMessenger() {
	
	}
	
	public void clearAllMessages() {
		hasError = false;
		idToName.clear();
		messageByItem.clear();
	}
	
	public List<Message> getAllMessages() {
		ArrayList<Message> msgs = new ArrayList<Message>();
		
		for (Integer i : messageByItem.keySet()) {
			msgs.addAll(messageByItem.get(i));
		}
		
		return msgs;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public boolean hasWarnings() {
		return !messageByItem.isEmpty();
	}
	
	public void addMessage(Message msg) {
		
		if (messageByItem.get(msg.getItemId()) != null) {
			messageByItem.get(msg.getItemId()).add(msg);
		}
		else {
			List<Message> msgs = new ArrayList<Message>();
			msgs.add(msg);
			
			messageByItem.put(msg.getItemId(), msgs);
		}
		
		idToName.put(msg.getItemId(), msg.getItemName());
		
		if (msg.getLevel() == Message.ERROR) {
			hasError = true;
		}
	}
	
	public void addBulkMessages(List<Message> msgs) {
		for (Message msg : msgs) {
			addMessage(msg);
		}
	}
}
