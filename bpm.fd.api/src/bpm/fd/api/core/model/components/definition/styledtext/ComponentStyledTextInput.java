package bpm.fd.api.core.model.components.definition.styledtext;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;

public class ComponentStyledTextInput implements IComponentDefinition {
	private String name;
	private String comment = "";
	private Dictionary dictionary;
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private String cssClass;
	
	private List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public ComponentStyledTextInput(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		StyledTextOptions opts = new StyledTextOptions();
		options.add(opts);
				
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
			
	}
	
	public String getComment() {
		return comment;
	}

	public IComponentDatas getDatas() {
		return null;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public List<IComponentOptions> getOptions() {
		return new ArrayList<IComponentOptions>(options);
	}

	public IComponentOptions getOptions(Class<?> classOption) {
		for(IComponentOptions o : getOptions()){
			if (o.getClass() == classOption){
				return o;
			}
		}
		return null;
	}

	public Collection<ComponentParameter> getParameters() {
		return new ArrayList<ComponentParameter>(parameters);
	}
	
	
	public void addComponentParameter(ComponentParameter parameter){
		parameters.add(parameter);
	}
	
	public void removeComponentParameter(ComponentParameter parameter){
		parameters.remove(parameter);
	}

	public IComponentRenderer getRenderer() {
		
		return null;
	}

	public boolean hasParameter() {
		return !parameters.isEmpty();
	}

	public void setComment(String comment) {
		this.comment = comment;

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);

	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);

	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("styledTextInput");
		e.addElement("comment").setText(getComment());
		e.addAttribute("name", getName());
		
		if (getCssClass() != null && !getCssClass().equals("")){
			e.addAttribute("cssClass", getCssClass());
		}
		
		for(ComponentParameter p : parameters){
			e.addElement("parameter").addAttribute("name", p.getName());
		}
		
		e.add(getOptions().get(0).getElement());
		
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

	public String getId() {
		return getName().replace(" ", "_");
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME_CHANGED, oldName, this.name);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);

	}

	public void setComponentOption(LabelOptions options) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == options.getClass()){
				this.options.remove(opt);
				break;
			}
		}
		
		this.options.add(options);
		
	}

	public ComponentStyle getComponentStyle() {
		
		return null;
	}
	
	public Integer getDatasOutputFieldIndex() {
		
		return null;
	}

	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}

	public void setJavaScript(ElementsEventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)){
			eventScript.put(type, script);
		}
		firePropertyChange(IBaseElement.EVENTS_CHANGED, null, script);
	}
	
	public boolean hasEvents() {
		for(ElementsEventType e : eventScript.keySet()){
			if (eventScript.get(e) != null && !"".equals(eventScript.get(e))){
				return true;
			}
		}
		return false;
	}

	@Override
	public IComponentDefinition copy() {
		ComponentStyledTextInput copy = new ComponentStyledTextInput("Copy_of_" + name, dictionary);
		
		copy.setCssClass(cssClass);
		for(IComponentOptions opt : getOptions()) {
			try {
				copy.setComponentOption((LabelOptions) ((LabelOptions) opt).copy());
			} catch (Exception e) {
			}
		}
		
		Collection<ElementsEventType> events = getEventsType();
		if(events != null && !events.isEmpty()) {
			for(ElementsEventType type : events) {
				copy.setJavaScript(type, getJavaScript(type));
			}
		}
		
		Collection<ComponentParameter> params = getParameters();
		if(params != null && !params.isEmpty()) {
			for(ComponentParameter type : params) {
				copy.addComponentParameter(type);
			}
		}
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
}