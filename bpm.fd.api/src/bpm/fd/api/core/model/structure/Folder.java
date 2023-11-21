package bpm.fd.api.core.model.structure;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class Folder implements IStructureElement,IContainer{

	public static final String CSS_ACTIVE_LI = "li:active";
	public static final String CSS_ACTIVE_A = "a:active";
	
	private String name = "Folder";
	private String id;
	private String cssClass = "menu ";
	
	private List<FolderPage> content = new ArrayList<FolderPage>();
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private IStructureElement parentStructureElement;
	
	private ComponentStyle style = new ComponentStyle();
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	private Point position = new Point(0, 10);
	private Point size = new Point(100, 50);
	protected Folder(String id, String name){
		setName(name);
		this.id = id;
		
		style.setStyleFor(CSS_ACTIVE_LI, "active");
		style.setStyleFor(CSS_ACTIVE_A, "active");
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
		
	}
	
	public ComponentStyle getComponentStyle(){
		return style;
	}
	
	public boolean addToContent(IStructureElement element) {
		if (element == null || !(element instanceof FolderPage) || content.contains(element)){
			return false;
		}
		content.add((FolderPage)element);
		((FolderPage)element).setParentStructureElement(this);
		listeners.firePropertyChange(P_CONTENT_ADDED, null, element);
		return true;
	}

	public List<IBaseElement> getContent() {
		return (List)content;
	}

	public IStructureElement getParentStructureElement() {
		return parentStructureElement;
	}

	public void removeComponent(IComponentDefinition component) {
		
	}

	public boolean removeFromContent(IStructureElement element) {
		boolean b = content.remove(element);
		if (b){
			listeners.firePropertyChange(P_CONTENT_REMOVED, null, element);
		}
		
		return b;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getCssClass() {
		return cssClass;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("folder");
		e.addAttribute("id", getId().substring("folder_".length()));
		e.addAttribute("name", getName());
		
		e.addAttribute("x", getPosition().x + "");
		e.addAttribute("y", getPosition().y + "");
		e.addAttribute("width", getSize().x + "");
		e.addAttribute("height", getSize().y + "");
		
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		e.add(style.getElement());
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		for(FolderPage page : content){
			e.add(page.getElement());
			
		}
		
		return e;
	}

	public String getId() {
		return "Folder_" + id;
	}

	public String getName() {
		return name;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	public void setCssClass(String css) {
		if (css.contains("menu ")){
			this.cssClass = css;
		}
		else{
			this.cssClass = "menu " + css;
		}
		
		
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = "folder";
//		this.name = name;
	}

	public void setParentStructureElement(IStructureElement parent){
		this.parentStructureElement = parent;
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

	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName) {
		return new ArrayList<Cell>();
	}
	public void setPosition(int x, int y) {
		Point p = getPosition();
		position = new Point(0, y);
		listeners.firePropertyChange(EVENT_LAYOUT, p, position);
		
	}
	public Point getPosition(){
		return position;
	}

	public void setSize(int width, int height) {
		Point p = getSize();
		size = new Point(width < 20 ? 20 : width, height);
		listeners.firePropertyChange(EVENT_LAYOUT, p, size);
	}
	public Point getSize(){
		return size;
	}

	
}
