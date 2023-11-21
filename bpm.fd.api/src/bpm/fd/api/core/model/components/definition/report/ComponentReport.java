package bpm.fd.api.core.model.components.definition.report;

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
import bpm.fd.api.core.model.components.definition.IRepositoryObjectReference;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.vanilla.platform.core.IRepositoryApi;

public class ComponentReport implements IComponentDefinition, IRepositoryObjectReference{
	private String comment = "";
	private Dictionary dictionary;
	private String name; 
	private String cssClass;
	
	private Integer directoryItemId;
	private List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	public ComponentReport(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		
		ReportOptions opts = new ReportOptions();
		options.add(opts);
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	
	public int getDirectoryItemId(){
		return directoryItemId;
	}
	
	public void setDirectoryItemId(int directoryItemId){
		this.directoryItemId = directoryItemId;
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
		return options;
	}

	public IComponentOptions getOptions(Class<?> classOption) {
		for(IComponentOptions o : options){
			if (classOption == o.getClass()){
				return o;
			}
		}
		return null;
	}

	public Collection<ComponentParameter> getParameters() {
		return new ArrayList<ComponentParameter>(parameters);
	}

	public IComponentRenderer getRenderer() {
		
		return null;
	}

	public boolean hasParameter() {
		return parameters.size() > 0;
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
		Element e = DocumentHelper.createElement("report");
		e.addElement("comment").setText(getComment() + "");
		e.addAttribute("name", getName());
		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		
		e.addElement("directoryItemId").setText(directoryItemId + "");
		
		for(ComponentParameter p : parameters){
			e.addElement("parameter").addAttribute("name", p.getName());
		}
		
		for(IComponentOptions o: options){
			e.add(o.getElement());
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

	/**
	 * set this component needed parameter, they come from the IRepositoryConnection.getParameters()
	 * @param parameters
	 */
	public void defineParameter(List<ComponentParameter> parameters) {
		this.parameters = new ArrayList<ComponentParameter>(parameters);
		
	}


	public void addComponentParameter(ComponentParameter parameter) {
		if (parameter == null){
			return;
		}
		if (this.parameters == null){
			this.parameters = new ArrayList<ComponentParameter>();
		}
		if (!this.parameters.contains(parameter)){
			this.parameters.add(parameter);
		}
		
	}


	public void removeComponentParameter(ComponentParameter parameter) {
		if (this.parameters == null){
			return;
		}
		this.parameters.remove(parameter);
		
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
	
	public int[] getObjectSubtypes() {
		return new int[]{IRepositoryApi.BIRT_REPORT_SUBTYPE, IRepositoryApi.JASPER_REPORT_SUBTYPE};
	}


	public int[] getObjectType() {
		return new int[]{IRepositoryApi.FWR_TYPE, IRepositoryApi.CUST_TYPE};
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
	public IComponentDefinition copy() {
		ComponentReport copy = new ComponentReport("Copy_of_" + name, dictionary);
		
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
		
		copy.setDirectoryItemId(directoryItemId);
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
}
