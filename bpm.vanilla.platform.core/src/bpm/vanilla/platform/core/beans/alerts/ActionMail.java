package bpm.vanilla.platform.core.beans.alerts;

import java.util.ArrayList;
import java.util.List;


public class ActionMail implements IActionInformation {

	private static final long serialVersionUID = -8853664181032575164L;
	
	private String subject;
	private String content;
	
	private List<Subscriber> subscribers = new ArrayList<Subscriber>();

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public List<Subscriber> getSubscribers() {
		if(subscribers == null){
			subscribers = new ArrayList<Subscriber>();
		}
		return subscribers;
	}
	
	public void addSubscriber(Subscriber subscriber){
		if(subscribers == null){
			subscribers = new ArrayList<Subscriber>();
		}
		subscribers.add(subscriber);
	}
	
	@Override
	public boolean equals(Object o) {
		return subject.equals(((ActionMail)o).getSubject()) && content.equals(((ActionMail)o).getContent());
	}
}