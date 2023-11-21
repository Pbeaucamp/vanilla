package bpm.fd.api.core.model;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;

import org.dom4j.Element;

import bpm.fd.api.core.model.events.ElementsEventType;

public interface IBaseElement {
	public static final String EVENTS_CHANGED = "bpm.fd.api.core.model.eventschanged";
	public String getName();
	public String getId();
	public Element getElement();
	
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
	
	public String getCssClass();
	public void setCssClass(String css);
	
	public Collection<ElementsEventType> getEventsType();
	public HashMap<ElementsEventType, String> getEventScript();
	public String getJavaScript(ElementsEventType type);
	public void setJavaScript(ElementsEventType type, String script);
	public boolean hasEvents();
	
	
}
