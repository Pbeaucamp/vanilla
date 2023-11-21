package bpm.fd.api.core.model.components.definition.styledtext;

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

public class DynamicLabelComponent implements IComponentDefinition {
	
	private String name;
	private String comment = "";
	
	private Dictionary dictionary;
	private DynamicLabelData datas;
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private String cssClass;

	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private List<ComponentParameter> additionalParameters = new ArrayList<ComponentParameter>();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	private DynamicLabelRenderer renderer = new DynamicLabelRenderer();
	
	private String label;

	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
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

	public DynamicLabelComponent(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		DynamicLabelOptions opts = new DynamicLabelOptions();
		options.add(opts);
		opts.setText("Label");
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
			
	}
	
	public String getComment() {
		return comment;
	}
	
	public IComponentDatas getDatas(){
		return datas;
	}
	
	public void setComponentDatas(DynamicLabelData datas){
		this.datas = datas;	
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
		if (datas != null && datas.getDataSet() != null){
			for(ParameterDescriptor p : datas.getDataSet().getDataSetDescriptor().getParametersDescriptors()){
				 if (parameters.get(p) != null){
					 
				 }
				 else{
					 parameters.put(p, new ComponentParameter(p, p.getPosition()));
				 }
			}
			List<ParameterDescriptor> l = datas.getDataSet().getDataSetDescriptor().getParametersDescriptors();
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
//		l.addAll(additionalParameters);
		return l;
	}

	public void addComponentParameter(ComponentParameter parameter){
		for(ComponentParameter p : parameters.values()){
			if (p.getName().equals(parameter.getName())){
				return;
			}
			
		}
		
//		for(ComponentParameter p : drillInfo.getOutputParameters()){
//			if (p.getName().equals(parameter.getName())){
//				return;
//			}
//			
//		}
		
		additionalParameters.add(parameter);
	}
	
	public void removeComponentParameter(ComponentParameter parameter){
		additionalParameters.remove(parameter);
//		drillInfo.removeParameter(parameter);
	}

//	public IComponentRenderer getRenderer() {
//		return renderer;
//	}

	public boolean hasParameter() {
		if (datas != null){
			return datas.getDataSet().getDataSetDescriptor().getParametersDescriptors().size() > 0 || additionalParameters.size() > 0;
		}
		return false;
	}

	public void setComment(String comment) {
		this.comment = comment;

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);

	}
	public void setText(String text){
		DynamicLabelOptions opt = (DynamicLabelOptions)getOptions(DynamicLabelOptions.class);
		String old = opt.getText();
		opt.setText(text);
		firePropertyChange(IComponentDefinition.PROPERTY_NAME_CHANGED, old, text);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);

	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dynamicLabel");
		e.addElement("comment").setText(getComment());
		e.addAttribute("name", getName());
		if (getRenderer() != null){
			e.add(getRenderer().getElement());
		}
		if (getCssClass() != null && !getCssClass().equals("")){
			e.addAttribute("cssClass", getCssClass());
		}
		if (getLabel() != null){
			e.addElement("label").setText(getLabel());
		}
		
		for(ComponentParameter p : additionalParameters){
			e.addElement("parameter").addAttribute("name", p.getName());
		}
		
		if (getDatas() != null){
			e.add(getDatas().getElement());
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

	public void setComponentOption(DynamicLabelOptions options) {
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
	
	public void setEventScript(HashMap<ElementsEventType, String> eventScript) {
		this.eventScript = eventScript;
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
//	public void setRenderer(DynamicLabelRenderer renderer) {
//		this.renderer = renderer;
//	}

	@Override
	public IComponentDefinition copy() {
		DynamicLabelComponent copy = new DynamicLabelComponent("Copy_of_" + name, dictionary);
		
		copy.setCssClass(cssClass);
		for(IComponentOptions opt : getOptions()) {
			try {
				copy.setComponentOption((DynamicLabelOptions) ((DynamicLabelOptions) opt).copy());
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

		copy.setLabel(label);
		copy.setComment(comment);
//		copy.setRenderer(renderer);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
	
	public List<ComponentParameter> getOutputParameters() {
		return new ArrayList<ComponentParameter>(additionalParameters);
	}

	@Override
	public IComponentRenderer getRenderer() {
		return renderer;
	}
}
