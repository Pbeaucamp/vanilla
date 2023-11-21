package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;

public abstract class Activity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private List<Activity> parentActivities;
	private List<Activity> childActivities;

	private List<String> outputs = new ArrayList<String>();
	
	private int top;
	private int left;

	private TypeActivity type;

	private boolean loop;
	
	public Activity() {}
	
	public Activity(TypeActivity type, String name) {
		this.type = type;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Activity> getParentActivities() {
		return parentActivities;
	}

	public void setParentActivities(List<Activity> parentActivities) {
		this.parentActivities = parentActivities;
	}

	public List<Activity> getChildActivities() {
		return childActivities;
	}

	public void setChildActivities(List<Activity> childActivities) {
		this.childActivities = childActivities;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public TypeActivity getType() {
		return type;
	}

	public void setType(TypeActivity type) {
		this.type = type;
	}
	
	public void setParentActivity(Activity parentActivity) {
		parentActivities = new ArrayList<Activity>();
		parentActivities.add(parentActivity);
	}

	public Activity getParentActivity() {
		if(parentActivities != null && !parentActivities.isEmpty()) {
			return parentActivities.get(0);
		}
		return null;
	}

	public void setChildActivity(Activity childActivity) {
		childActivities = new ArrayList<Activity>();
		childActivities.add(childActivity);
	}

	public Activity getChildActivity() {
		if(childActivities != null && !childActivities.isEmpty()) {
			return childActivities.get(0);
		}
		return null;
	}
	
	public abstract List<Variable> getVariables(List<? extends Resource> resources);

	public abstract List<Parameter> getParameters(List<? extends Resource> resources);

	public abstract boolean isValid();

	public boolean hasChildActivity() {
		return childActivities != null && !childActivities.isEmpty();
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}
	
	
}
