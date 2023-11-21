package bpm.fd.api.core.model.components.definition.maps.openlayers;

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
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class ComponentMapWMS implements IComponentDefinition, ISizeable {
	private String comment = "";
	private String cssClass = null;
	private Dictionary dictionary;
	private String name;
	
	private int width = 300;
	private int height = 200;
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private ComponentStyle componentStyle;
	
	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	
	private MapDatas mapData;
	
	public ComponentMapWMS(String name, Dictionary dictionaty) {
		setName(name);
		this.dictionary = dictionaty;
	}
	
	public void addComponentParameter(ComponentParameter parameter) {
		parameters.put(parameter.getParameterDescriptor(), parameter);
	}

	public String getComment() {
		return comment;
	}
	
	public void addColorRange(ColorRange range){
		colorRanges.add(range);
	}
	
	public void removeColorRange(ColorRange range){
		colorRanges.remove(range);
	}

	public ComponentStyle getComponentStyle() {
		return componentStyle;
	}

	public Integer getDatasOutputFieldIndex() {
		return null;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public List<IComponentOptions> getOptions() {
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
		if (mapData.getDataSet() != null){
			for(ParameterDescriptor p : mapData.getDataSet().getDataSetDescriptor().getParametersDescriptors()){
				 if (parameters.get(p) != null){
					 
				 }
				 else{
					 parameters.put(p, new ComponentParameter(p, p.getPosition()));
				 }
			}
			List<ParameterDescriptor> l = mapData.getDataSet().getDataSetDescriptor().getParametersDescriptors();
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

	public Element getElement() {
		Element e = DocumentHelper.createElement("mapwms");
		e.addElement("comment").setText(getComment());
		e.addAttribute("name", getName());
		e.addAttribute("width", getWidth() + "");
		e.addAttribute("height", getHeight() + "");

		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		if (getDatas() != null){
			e.add(getDatas().getElement());
		}
		if (getRenderer() != null){
			e.add(getRenderer().getElement());
		}
		Element colorRanges = e.addElement("colorRanges");
		for(ColorRange r : getColorRanges()){
			Element er = colorRanges.addElement("colorRange");
			er.addAttribute("color", r.getHex());
			
			er.addAttribute("legend", r.getName());
			if (r.getMax() != null){
				er.addAttribute("maxValue", r.getMax() + "");
			}
			if (r.getMin() != null){
				er.addAttribute("minValue", r.getMin() + "");
			}
			
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

	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	public String getId() {
		return getName().replace(" ", "_");
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
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




	public int getHeight() {
		return height;
	}




	public int getWidth() {
		return width;
	}




	public void setHeight(int height) {
		this.height = height;
	}




	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public IComponentRenderer getRenderer() {
		
		return null;
	}

	@Override
	public IComponentDatas getDatas() {
		return mapData;
	}
	
	public void setDatas(MapDatas mapData) {
		this.mapData = mapData;
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

	public void setColorRanges(List<ColorRange> colorRanges) {
		this.colorRanges = colorRanges;
	}

	public List<ColorRange> getColorRanges() {
		return colorRanges;
	}

	@Override
	public IComponentDefinition copy() {
		ComponentMapWMS copy = new ComponentMapWMS("Copy_of_" + name, dictionary);
		
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
		
		copy.setColorRanges(new ArrayList<ColorRange>(colorRanges));
		copy.setDatas((MapDatas) mapData.copy());
		copy.setHeight(height);
		copy.setWidth(width);
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}
	
}
