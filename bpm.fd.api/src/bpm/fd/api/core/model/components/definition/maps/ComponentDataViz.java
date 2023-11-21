package bpm.fd.api.core.model.components.definition.maps;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.IComponentRenderer;
import bpm.fd.api.core.model.components.definition.ISizeable;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;

public class ComponentDataViz implements IComponentDefinition, ISizeable{
	
	private String comment = "";
	private String cssClass = null;
	private Dictionary dictionary;
	private String name;
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private ComponentStyle componentStyle;
	
	protected int width = 400;
	protected int height = 400;
	
	public ComponentDataViz(String name, Dictionary model) {
		setName(name);
		this.dictionary = model;
	}

	public void addComponentParameter(ComponentParameter parameter) {
		
	}

	public String getComment() {
		return comment;
	}

	public ComponentStyle getComponentStyle() {
		return componentStyle;
	}
	
	public Dictionary getDictionary() {
		return dictionary;
	}

	public List<IComponentOptions> getOptions() {
		if(options.isEmpty()) {
			options.add(new DataVizOption());
		}
		return new ArrayList<IComponentOptions>(options);
	}

	public IComponentOptions getOptions(Class<?> classOption) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == classOption){
				return opt;
			}
		}
		
		return null;
	}
	
	public Collection<ComponentParameter> getParameters() {
		return new ArrayList<ComponentParameter>(); 
	}
	public boolean hasParameter() {
		return parameters.size() > 0;
	}

	public void removeComponentParameter(ComponentParameter parameter) {
		if (this.parameters == null){
			return;
		}
		this.parameters.remove(parameter);
		
	}

	public void setComment(String comment) {
		this.comment = comment;
		
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME_CHANGED, oldName, this.name);

		
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		
	}

	public String getCssClass() {
		return cssClass;
	}
	
	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}

	public String getId() {
		return getName().replace(" ", "_");
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}

	public String getName() {
		return name;
	}

	public boolean hasEvents() {
		for(ElementsEventType e : eventScript.keySet()){
			if (eventScript.get(e) != null && !"".equals(eventScript.get(e))){
				return true;
			}
		}
		return false;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
		
	}

	public void setJavaScript(ElementsEventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)){
			eventScript.put(type, script);
		}
		firePropertyChange(IBaseElement.EVENTS_CHANGED, null, script);

		
	}
	
	public void setComponentOption(IComponentOptions options) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == options.getClass()){
				this.options.remove(opt);
				break;
			}
		}
		
		this.options.add(options);
		
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("dataviz");
		e.addElement("comment").setText(getComment());
		e.addAttribute("name", getName());

		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		if (getDatas() != null){
			e.add(getDatas().getElement());
		}
		if (getRenderer() != null){
			e.add(getRenderer().getElement());
		}
		for(IComponentOptions opt : getOptions()){
			e.add(opt.getElement());
		}
				
		if (getComponentStyle() != null){
			e.add(getComponentStyle().getElement());
		}
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		return e;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public IComponentRenderer getRenderer() {
		return null;
	}

	@Override
	public IComponentDatas getDatas() {
		return null;
	}

	@Override
	public Integer getDatasOutputFieldIndex() {
		return null;
	}

	@Override
	public IComponentDefinition copy() {
		return null;
	}
}
