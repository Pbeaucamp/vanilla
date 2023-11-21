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
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
/**
 * a Map component has a List of ColorRange objects
 * 
 * {@link ColorRange}
 * 
 * @author ludo
 *
 */
public class ComponentMap implements IComponentDefinition, ISizeable{

	private String comment = "";
	private String cssClass = null;
	private Dictionary dictionary;
	private String name;
	private IMapDatas datas = new MapDatas();
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	private ComponentStyle componentStyle;
	
	private MapRenderer renderer ;
	
	private List<ColorRange> colorRanges = new ArrayList<ColorRange>();
	
	private IMapInfo mapInfo = new MapInfo();
	private MapDrillInfo drillInfo;
	private boolean isShowLabels = true;
	
	
	public ComponentMap(String name, Dictionary dictionaty) {
		setName(name);
		this.dictionary = dictionaty;
		this.drillInfo = new MapDrillInfo(this);
	}

	
	public MapDrillInfo getDrillInfo(){
		return drillInfo;
	}
	
	/**
	 * @return the mapInfo
	 */
	public IMapInfo getMapInfo() {
		return mapInfo;
	}




	/**
	 * 
	 * @return the defined color for values intervals
	 */
	public List<ColorRange> getColorRanges(){
		return new ArrayList<ColorRange>(colorRanges);
	}
	
	/**
	 * add the given range
	 * 
	 * 
	 * @param range
	 * @return true if the range has been successfully added
	 */
	public void addColorRange(ColorRange range){
		colorRanges.add(range);
	}
	
	
	public void removeColorRange(ColorRange range){
		colorRanges.remove(range);
	}
	
	public void addComponentParameter(ComponentParameter parameter) {
		
	}

	public String getComment() {
		return comment;
	}

	public ComponentStyle getComponentStyle() {
		return componentStyle;
	}

	public IComponentDatas getDatas() {
		return datas;
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
		if (datas != null && datas.getDataSet() != null ){
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
	
		
		return l;
	}

	public IComponentRenderer getRenderer() {
		return renderer;
	}
	
	public void setRenderer(MapRenderer renderer) {
		this.renderer = renderer;
		datas = (IMapDatas)datas.getAdapter(renderer);
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
		Element e = DocumentHelper.createElement("map");
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
		e.add(mapInfo.getElement());
				
		if (getComponentStyle() != null){
			e.add(getComponentStyle().getElement());
		}
		
		e.addElement("isShowLabels").setText(isShowLabels + "");
		
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
		
		
		if (getDrillInfo() != null){
			e.add(getDrillInfo().getElement());
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
	
	public void setComponentDatas(IMapDatas datas){
		this.datas = datas;
	}




	public int getHeight() {
		return getMapInfo().getHeight();
	}




	public int getWidth() {
		return getMapInfo().getWidth();
	}




	public void setHeight(int height) {
		getMapInfo().setHeight(height);
		
	}




	public void setWidth(int width) {
		getMapInfo().setWidth(width);
		
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




	public void setShowLabels(boolean isShowLabels) {
		this.isShowLabels = isShowLabels;
	}




	public boolean isShowLabels() {
		return isShowLabels;
	}


	@Override
	public IComponentDefinition copy() {
		ComponentMap copy = new ComponentMap("Copy_of_" + name, dictionary);
		
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
		
		copy.setComponentDatas((IMapDatas) datas.copy());
		
		copy.getMapInfo().setHeight(mapInfo.getHeight());
		copy.getMapInfo().setWidth(mapInfo.getWidth());
		
		copy.setShowLabels(isShowLabels);
		copy.setRenderer(renderer);
		
		copy.setComment(comment);
		
		try {
			dictionary.addComponent(copy);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		return copy;
	}

}
