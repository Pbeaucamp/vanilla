package bpm.fd.api.core.model.structure;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class Cell implements IStructureElement, IContainer{
	private List<IBaseElement> cellContents = new ArrayList<IBaseElement>();
	
	private int colSpan = 1;
	
	private HashMap<IComponentDefinition, ComponentConfig> configs = new LinkedHashMap<IComponentDefinition, ComponentConfig>();
	private String cssClass;
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	private String id;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private String name = "Cell"; 
	private IStructureElement parentStructureElement;
	
	private int rowSpan = 1;
	
	private Point position = new Point(10, 10);
	private Point size = new Point(50, 20);
	
	protected Cell(String id, String name){
		this.id = id;
		this.name = name;
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
		
	}
	protected HashMap<IComponentDefinition, ComponentConfig> getInternalConfigs(){
		return configs;
	}
	public boolean addBaseElementToContent(IBaseElement element){
		if (element == null){
			return false;
		}
		if (!cellContents.contains(element)){
			boolean b = cellContents.add(element);
			if (element instanceof IComponentDefinition){
				ComponentConfig conf = new ComponentConfig(this,(IComponentDefinition)element);
				configs.put((IComponentDefinition)element, conf);				
			}
			else if (element instanceof Cell && !((Cell)element).getContent().isEmpty()){
				for(IBaseElement e : ((Cell)element).getContent()){
					if (e instanceof IComponentDefinition){
						ComponentConfig conf = new ComponentConfig(this,(IComponentDefinition)e);
						configs.put((IComponentDefinition)e, conf);			
					}
				}
			}
			
			listeners.firePropertyChange(P_CONTENT_ADDED, null, element);
			return b;
		}
			
		return false;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public boolean addToContent(IStructureElement element) {
		return addBaseElementToContent(element);
	}

	
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		if (getParentStructureElement() != null){
			getParentStructureElement().firePropertyChange(propertyName, oldValue, newValue);
		}
	}
	
	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName) {
		
		List<Cell> l = new ArrayList<Cell>();
		for(IBaseElement e : getContent()){
			if (e instanceof IComponentDefinition){
				for(ComponentParameter p : ((IComponentDefinition)e).getParameters()){
					
					if (getConfig((IComponentDefinition)e).getComponentNameFor(p).equals(oldComponentName)){
						l.add(this);
					}
				}
			}
			else if ( e instanceof IStructureElement){
				l.addAll(((IStructureElement)e).getCellsUsingParameterProviderNames(oldComponentName));
			}
		}
		return l;
	}
	/**
	 * @return the colSpan
	 */
	public int getColSpan() {
		return colSpan;
	}
	
	public ComponentConfig getConfig(IComponentDefinition component){
		return configs.get(component);
	}
	
	public Collection<ComponentConfig> getConfigs(){
		return configs.values();
	}

	public HashMap<IComponentDefinition, ComponentConfig> getConfigurations(){
		return configs;
	}

	public List<IBaseElement> getContent(){
		return new ArrayList<IBaseElement>(cellContents);
	}

	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("cell");
		e.addAttribute("id", getId().substring("cell_".length()));
		e.addAttribute("name", getName());
		
		e.addAttribute("rowSpan", getRowSpan() + "");
		e.addAttribute("colSpan", getColSpan() + "");
		
		e.addAttribute("x", getPosition().x + "");
		e.addAttribute("y", getPosition().y + "");
		e.addAttribute("width", getSize().x + "");
		e.addAttribute("height", getSize().y + "");
		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		
		for(IBaseElement baseE : getContent()){
			if (baseE instanceof IStructureElement){
				e.add(baseE.getElement());
			}
			else if (baseE instanceof IComponentDefinition){
				Element ref = e.addElement("component-ref").addAttribute("name", ((IComponentDefinition)baseE).getName());
				ref.addAttribute("class", baseE.getClass().getName());
				
				ComponentConfig config = configs.get((IComponentDefinition)baseE);
				if (config != null){
					for(ComponentParameter p : config.getParameters()){
						Element _e = ref.addElement("parameter").addAttribute("name", p.getName());
						_e.addAttribute("defaultValue", p.getDefaultValue());
						_e.setText(config.getComponentNameFor(p));
						//generate second info on config(DataGrid.column) for the field
						//used by component providing multiple values like a DataGrid
						if (config.getComponentOutputName(p) != null ){
							_e.addAttribute("fieldNumber", "" + config.getComponentOutputName(p));
						}
					}
				}
			}
			
		}
		
		return e;
	}

	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return "cell_" + id;
	}


	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}
	
	public void setEventScript(HashMap<ElementsEventType, String> eventScript) {
		this.eventScript = eventScript;
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public IStructureElement getParentStructureElement() {
		return parentStructureElement;
	}

	/**
	 * @return the rowSpan
	 */
	public int getRowSpan() {
		return rowSpan;
	}

	public boolean hasEvents() {
		for(ElementsEventType e : eventScript.keySet()){
			if (eventScript.get(e) != null && !"".equals(eventScript.get(e))){
				return true;
			}
		}
		return false;
	}
	
	public boolean isComponentDefined(FdModel model, IComponentDefinition def){
		ComponentConfig conf = getConfig(def);
		if (conf == null){
			return false;
		}
		if (conf.getParameters().isEmpty()){
			return true;
		}
		
		/*
		 * we need to look inside all models from the project 
		 */
		List<FdModel> modelToCheck = new ArrayList<FdModel>();
		modelToCheck.add(model);
		if (model.getProject() instanceof MultiPageFdProject){
			for(FdModel m : ((MultiPageFdProject)model.getProject()).getPagesModels()){
				if (!modelToCheck.contains(m)){
					modelToCheck.add(m);
				}
			}
			
			if (!modelToCheck.contains(model.getProject().getFdModel())){
				modelToCheck.add(model.getProject().getFdModel());
			}
		}
		
		
		
		for(ComponentParameter p : conf.getParameters()){
			String s = conf.getComponentNameFor(p);
			
			
			if (s == null || s.equals("")){
				return false;
			}
			else{
				boolean finded = false;
				for(FdModel m : modelToCheck){
					for(IComponentDefinition d : m.getComponents().keySet()){
						if (d.getName().equals(s)){
							finded = true;
						}
					}
				}
				if(!finded) {
					return false;
				}
			}
		}
		return true;
	}


	public boolean removeBaseElementToContent(IBaseElement element){
		boolean b = cellContents.remove(element);
		
		if (b){
			if (element instanceof IComponentDefinition){
				IComponentDefinition c = (IComponentDefinition)element;
				
				configs.remove(c);
			}
			listeners.firePropertyChange(P_CONTENT_REMOVED, null, element);
		}
		return b;
	}

	public void removeComponent(IComponentDefinition component) {
		removeBaseElementToContent(component);
		
		for(IBaseElement e : getContent()){
			if ( e instanceof IStructureElement){
				((IStructureElement)e).removeComponent(component);
			}
		}
		
	}

	public boolean removeFromContent(IStructureElement element) {
		return removeBaseElementToContent(element);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	/**
	 * @param colSpan the colSpan to set
	 */
	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}
	
	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		String old = this.cssClass;
		this.cssClass = cssClass;
		firePropertyChange(IComponentDefinition.PROPERTY_NAME_CHANGED, old, this.cssClass);
	}

	public void setJavaScript(ElementsEventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)){
			eventScript.put(type, script);
		}
		firePropertyChange(IBaseElement.EVENTS_CHANGED, null, script);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParentStructureElement(IStructureElement parent){
		this.parentStructureElement = parent;
	}
	/**
	 * @param rowSpan the rowSpan to set
	 */
	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public void setPosition(int x, int y) {
		Point p = getPosition();
		position = new Point(x, y);
		firePropertyChange(EVENT_LAYOUT, p, position);
	}
	public Point getPosition(){
		return position;
	}

	public void setSize(int width, int height) {
		
		Point p = getSize();
		size = new Point(width, height);
		firePropertyChange(EVENT_LAYOUT, p, getSize());
	}
	public Point getSize(){
		return size;
	}


}
