package bpm.fd.api.core.model.components.definition.filter;

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
import bpm.fd.api.core.model.components.definition.filter.validator.Validator;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;

public class ComponentFilterDefinition implements IComponentDefinition{
	public static final String PROPERTY_RENDERER_CHANGED = "bpm.fd.api.core.model.components.definition.filter.renderer";
	private String name;

	private Dictionary dictionary;
	private FilterRenderer renderer;
	private FilterData datas;
	private String cssClass;
	
	private TypeRenderer type;
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	
	private String comment = "";
	private ComponentStyle style ;
	
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private List<ComponentParameter> additionalParameters = new ArrayList<ComponentParameter>();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private Validator validator;
	
	private String valueJavaScript;
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	private String label;
	
	public ComponentFilterDefinition(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		options.add(new FilterOptions());
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	public String getLabel(){
		return label;
	}
	public void setLabel(String label){
		this.label = label;
	}
	
	
	public ComponentStyle getComponentStyle() {
		return style;
	}


	/**
	 * @return the valueJavaScript
	 */
	public String getValueJavaScript() {
		return valueJavaScript;
	}


	/**
	 * @param valueJavaScript the valueJavaScript to set
	 */
	public void setValueJavaScript(String valueJavaScript) {
		this.valueJavaScript = valueJavaScript;
	}


	/**
	 * @return the validator
	 */
	public Validator getValidator() {
		return validator;
	}


	/**
	 * @param validator the validator to set
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}


	public void addComponentParameter(ComponentParameter parameter){
		for(ComponentParameter p : parameters.values()){
			if (p.getName().equals(parameter.getName())){
				return;
			}
			
		}
		additionalParameters.add(parameter);
	}
	
	public void removeComponentParameter(ComponentParameter parameter){
		additionalParameters.remove(parameter);
	}
	
	public List<IComponentOptions> getOptions(){
		return new ArrayList<IComponentOptions>(options);
	}
	
	public IComponentDatas getDatas(){
		return datas;
	}
	
	public void setComponentDatas(FilterData datas){
		this.datas = datas;	
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		
	}
	
	public Dictionary getDictionary(){
		return dictionary;
	}
	


	public Element getElement() {
		Element e = DocumentHelper.createElement("filter");
		e.addElement("comment").setText(getComment());
		e.addAttribute("name", getName());
		
		
		if (valueJavaScript != null && !"".equals(valueJavaScript)){
			Element js = e.addElement("valueJavaScript");
			js.addCDATA(valueJavaScript);
		}
		
		
		
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
		
		for(ComponentParameter p : additionalParameters){
			e.addElement("parameter").addAttribute("name", p.getName());
		}
		
		if (getComponentStyle() != null){
			e.add(getComponentStyle().getElement());
		}
		if (getLabel() != null){
			e.addElement("label").setText(getLabel());
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
	
	public void setName(String name){
		String oldName = this.name;
		this.name = name;
		firePropertyChange(PROPERTY_NAME_CHANGED, oldName, this.name);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	public IComponentRenderer getRenderer() {
		return renderer;
	}
	
	public void setRenderer(FilterRenderer renderer) {

		try{
			
//			if (renderer == FilterRenderer.getRenderer(FilterRenderer.CHECKBOX) ||
//				renderer == FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX)){
//					if (getOptions(MultipleValuesOptions.class) == null){
//						options.add(new MultipleValuesOptions());
//					}
//			}
//			
//			else{
//				if (getOptions(MultipleValuesOptions.class) != null){
//					options.remove(getOptions(MultipleValuesOptions.class));
//				}
//				
//			}
			
			if (renderer == FilterRenderer.getRenderer(FilterRenderer.DYNAMIC_TEXT) && valueJavaScript == null){
				valueJavaScript = "";
				datas = null;
			}
			
			if (renderer == FilterRenderer.getRenderer(FilterRenderer.TEXT_FIELD) || 
				renderer == FilterRenderer.getRenderer(FilterRenderer.DATE_PIKER)){
				
					datas = null;
			}
			else{
				if (getDatas() == null){
					datas = new FilterData();
				}
			}
			
			this.renderer = renderer;
			
			if (renderer == FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX)){
				
				if (getOptions(DropDownOptions.class) == null){
					DropDownOptions opt = new DropDownOptions();
					options.add(opt);
				}
				options.remove(getOptions(MenuOptions.class));
				options.remove(getOptions(SliderOptions.class));
				style = null;
			}
			else if ( renderer == FilterRenderer.getRenderer(FilterRenderer.SLIDER)){
				if (getOptions(SliderOptions.class) == null){
					SliderOptions opt = new SliderOptions();
					options.add(opt);
				}
				options.remove(getOptions(MenuOptions.class));
				options.remove(getOptions(DropDownOptions.class));
			}
			else if (renderer == FilterRenderer.getRenderer(FilterRenderer.MENU)){
				MenuOptions opt = new MenuOptions();
				options.add(opt);
				options.remove(getOptions(DropDownOptions.class));
				options.remove(getOptions(SliderOptions.class));
//				style = new ComponentStyle();
//				style.setStyleFor("p", null);
			}
			else{
				style = null;
			}
			
			if (renderer != FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX) && getOptions(DropDownOptions.class) != null){
				options.remove(getOptions(DropDownOptions.class));
				
				
			}
			if (renderer != FilterRenderer.getRenderer(FilterRenderer.MENU) && getOptions(MenuOptions.class) != null){
				options.remove(getOptions(MenuOptions.class));
				
			}
			
			
			if (renderer != FilterRenderer.getRenderer(FilterRenderer.SLIDER) && getOptions(MenuOptions.class) != null){
				options.remove(getOptions(SliderOptions.class));
				
			}	
			
			
			
			
			
			
			if (renderer == FilterRenderer.getRenderer(FilterRenderer.TEXT_FIELD) ||
				renderer == FilterRenderer.getRenderer(FilterRenderer.DATE_PIKER) ){
				datas = null;
			}
			firePropertyChange(PROPERTY_RENDERER_CHANGED, null, renderer);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
		l.addAll(additionalParameters);
		return l;
	}

	public boolean hasParameter() {
		if (datas != null){
			return datas.getDataSet().getDataSetDescriptor().getParametersDescriptors().size() > 0 || additionalParameters.size() > 0;
		}
		return false;
	}

	public IComponentOptions getOptions(Class<?> classOption) {
		for(IComponentOptions opt : getOptions()){
			if (opt.getClass() == classOption){
				return opt;
			}
		}
		
		
		return null;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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


	public Integer getDatasOutputFieldIndex() {
		if (datas != null){
			return datas.getColumnValueIndex();
		}
		
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
		ComponentFilterDefinition copy = new ComponentFilterDefinition("Copy_of_" + name, dictionary);
		
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
		
		copy.setComponentDatas((FilterData) datas.copy());
		copy.setLabel(label);
		copy.setRenderer(renderer);
		copy.setValidator(validator);
		copy.setValueJavaScript(valueJavaScript);
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
	
}
