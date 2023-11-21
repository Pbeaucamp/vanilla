package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkflowModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Activity> activities;
	private List<Link> links;
	
	public WorkflowModel() { }

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public void addActivity(Activity activity) {
		if (activities == null) {
			activities = new ArrayList<Activity>();
		}
		this.activities.add(activity);
	}

	public void clearActivities() {
		this.activities = null;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void addLink(Link link) {
		if (links == null) {
			links = new ArrayList<Link>();
		}
		this.links.add(link);
	}

	public void clearLinks() {
		this.links = null;
	}
}
