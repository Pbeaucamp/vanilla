package bpm.fd.api.core.model.components.definition.datagrid;

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

public class ComponentDataGrid implements IComponentDefinition{
	

	public static final String PROPERTY_RENDERER_CHANGED = "bpm.fd.api.core.model.components.definition.filter.renderer";
	private String name;

	private Dictionary dictionary;
	private DataGridRenderer renderer = DataGridRenderer.getRenderer(DataGridRenderer.RENDERER_HORIZONTAL);
	private DataGridData datas = new DataGridData();
	private String cssClass = "grid";
	
	private List<IComponentOptions> options = new ArrayList<IComponentOptions>();
	
	private String comment = "";
	private ComponentStyle style ;
	
	private LinkedHashMap<ParameterDescriptor, ComponentParameter> parameters = new LinkedHashMap<ParameterDescriptor, ComponentParameter>();
	
	private List<ComponentParameter> additionalParameters = new ArrayList<ComponentParameter>();
//	private HashMap<ComponentParameter, Integer> additionalParameters = new HashMap<ComponentParameter, Integer>();
	
	private DataGridDrill drillInfo = new DataGridDrill();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private DataGridLayout layout = new DataGridLayout();
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	public ComponentDataGrid(String name, Dictionary owner){
		this.name = name;
		this.dictionary = owner;
		options.add(new DataGridOptions());	
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	
	public ComponentStyle getComponentStyle() {
		return style;
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
	
	public List<IComponentOptions> getOptions(){
		return new ArrayList<IComponentOptions>(options);
	}
	
	public IComponentDatas getDatas(){
		return datas;
	}
	
	public void setComponentDatas(DataGridData datas){
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
		Element e = DocumentHelper.createElement("dataGrid");
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
		
		for(ComponentParameter p : additionalParameters){
			Element _p = e.addElement("parameter").addAttribute("name", p.getName());
			
		}
		
		e.add(drillInfo.getElement());
		
		if (getComponentStyle() != null){
			e.add(getComponentStyle().getElement());
		}
		
		if (getLayout() != null){
			e.add(getLayout().getElement());
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
//		l.addAll(additionalParameters.keySet());
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
		
		return null;
	}


	public IComponentRenderer getRenderer() {
		return renderer;
	}


	public void setRenderer(DataGridRenderer renderer) {
		this.renderer = renderer;
		
	}


	public DataGridLayout getLayout(){
		return layout;
	}
	
	public void setLayout(DataGridLayout layout) {
		this.layout = layout;
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

	
	

	
	public DataGridDrill getDrillInfo(){
		return drillInfo;
	}
	
	public List<ComponentParameter> getOutputParameters() {
		return new ArrayList<ComponentParameter>(additionalParameters);
	}


	public boolean isDrillable() {
		return drillInfo.getModelPage() != null;
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
		ComponentDataGrid copy = new ComponentDataGrid("Copy_of_" + name, dictionary);
		
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
		
		copy.setComponentDatas((DataGridData) datas.copy());
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
