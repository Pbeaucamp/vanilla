package bpm.workflow.commons.beans;

import java.io.Serializable;

public class Link implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Activity startActivity;
	private Activity endActivity;
	
	public Link() { }
	
	public Link(Activity startActivity, Activity endActivity) {
		this.startActivity = startActivity;
		this.endActivity = endActivity;
	}
	
	public Activity getStartActivity() {
		return startActivity;
	}
	
	public Activity getEndActivity() {
		return endActivity;
	}
}
