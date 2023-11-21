package bpm.fd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fd.core.component.ComponentType;
import bpm.fd.core.component.EventType;

public abstract class DashboardComponent implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String comment = "";
	private String title;

	private int top;
	private int left;
	private int width;
	private int height;

	private String cssClass = "chartfd";

	private HashMap<EventType, String> eventScript = new HashMap<EventType, String>();

	private List<ComponentParameter> parameters;

	public DashboardComponent() {
		for (EventType e : EventType.getAvailablesFor(getType())) {
			eventScript.put(e, "");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public List<ComponentParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ComponentParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(ComponentParameter parameter) {
		if (parameters == null) {
			parameters = new ArrayList<ComponentParameter>();
		}
		parameters.add(parameter);
	}

	public void removeParameter(int index) {
		if (parameters.size() > index) {
			parameters.remove(index);
		}
	}

	public List<EventType> getEventsType() {
		return new ArrayList<EventType>(eventScript.keySet());
	}
	
	public HashMap<EventType, String> getEventScript() {
		return eventScript;
	}
	
	public void setEventScript(HashMap<EventType, String> eventScript) {
		this.eventScript = eventScript;
	}

	public String getJavaScript(EventType type) {
		return eventScript.get(type);
	}

	public void setJavaScript(EventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)) {
			eventScript.put(type, script);
		}
	}

	public boolean hasEvents() {
		for (EventType e : eventScript.keySet()) {
			if (eventScript.get(e) != null && !eventScript.get(e).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		this.title = "";
		clearData();
	}

	public abstract ComponentType getType();

	protected abstract void clearData();
}
