package bpm.fd.api.core.model.components.definition.slicer;

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
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;

public class ComponentSlicer implements IComponentDefinition{
	private String name;
	private String cssClass = "";
	private String comment = "";
	private Dictionary dictionary;
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private SlicerData data = new SlicerData();
	
	
	public ComponentSlicer(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
		options.add(new SlicerOptions());
	}
	
	public String getComment() {
		return comment;
	}

	
	public IComponentDatas getDatas() {
		return data;
	}
	public Dictionary getDictionary() {
		return dictionary;
	}
	public List<IComponentOptions> getOptions() {
		return options;
	}
	public IComponentOptions getOptions(Class<?> classOption) {
		return options.get(0);
	}
	public Collection<ComponentParameter> getParameters() {
		if (data != null && data.getDataSet() != null ){
			for(ParameterDescriptor p : data.getDataSet().getDataSetDescriptor().getParametersDescriptors()){
				 if (parameters.get(p) != null){

				 }
				 else{
					 parameters.put(p, new ComponentParameter(p, p.getPosition()));
				 }
			}
			List<ParameterDescriptor> l = data.getDataSet().getDataSetDescriptor().getParametersDescriptors();
			List<ParameterDescriptor> toRemove = new ArrayList<ParameterDescriptor>();
			for(ParameterDescriptor p : parameters.keySet()){
				if (!l.contains(p)){
					toRemove.add(p);
				}
			}
			for(ParameterDescriptor p : toRemove){
				parameters.remove(p);
			}
		}
		
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		l.addAll(parameters.values());

		
		return l;
	}
	public IComponentRenderer getRenderer() {
		return null;
	}
	public boolean hasParameter() {
		if (getDatas() != null){
			return getDatas().getDataSet().getDataSetDescriptor().getParametersDescriptors().size() > 0  ;
		}
		return false;
	}
	public void setComment(String comment) {
		this.comment = comment;
		
	}
	public void setName(String name){
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
	public Element getElement() {
		Element e = DocumentHelper.createElement("slicer");
		e.addAttribute("name", getName());
		e.addElement("comment").setText(getComment());

		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}

		if (getDatas() != null){
			e.add(getDatas().getElement());
		}

		Element options = e.addElement("options");
		for(IComponentOptions opt : getOptions()){
			options.add(opt.getElement());
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
	public String getId() {
		return getName().replace(" ", "_");
	}
	public String getName() {
		return name;
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}
	public void setCssClass(String css) {
		this.cssClass = css;
		
	}
	public void addComponentParameter(ComponentParameter parameter) {
		
		
	}
	public void removeComponentParameter(ComponentParameter parameter) {
		
		
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
	
	public void setComponentOption(IComponentOptions options) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == options.getClass()){
				this.options.remove(opt);
				break;
			}
		}
		
		this.options.add(options);
		
	}
	
	public void setData(IComponentDatas data) {
		this.data = (SlicerData) data;
	}

	@Override
	public IComponentDefinition copy() {
		ComponentSlicer copy = new ComponentSlicer("Copy_of_" + name, dictionary);
		
		copy.setCssClass(cssClass);
		for(IComponentOptions opt : getOptions()) {
			try {
				copy.setComponentOption( opt.copy());
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
		
		copy.setData(data.copy());
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
	

}
