package bpm.fd.api.core.model.structure;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class FolderPage implements IStructureElement{

	private String name = "FolderPage";
	private String id;
	private String cssClass;
	
	private String modelName;
	
	
	private String title;
	
	private FdModel content;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private Folder parentStructureElement;
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	protected FolderPage(String id, String name){
		setName(name);
		this.id = id;
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}
	
	/**
	 * used only by digester
	 * @param modelName
	 */
	public void setModelName(String modelName){
		this.modelName = modelName;
		getContent();
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		if (title == null){
			return getName();
		}
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public boolean addToContent(IStructureElement element) {
		if (element instanceof FdModel){
			content = (FdModel)element;
			((FdModel)element).setParentStructureElement(this);
			listeners.firePropertyChange(P_CONTENT_ADDED, null, element);
			return true;
		}
		return false;
	}

	public List<IBaseElement> getContent() {
		List<IBaseElement> l = new ArrayList<IBaseElement>();
		if (content != null){
			l.add(content);
		}
		else if (content == null && modelName != null){
			/*
			 * look for the content if it is not yet loaded 
			 */
			IStructureElement struct = getParentStructureElement();
			while(struct != null &&  !((struct = struct.getParentStructureElement()) instanceof FdModel) ){
				
			}
			
			if (struct != null){
				if (((FdModel)struct).getProject() instanceof MultiPageFdProject){
					for(FdModel m : ((MultiPageFdProject)((FdModel)struct).getProject()).getPagesModels()){
						if (m.getName().equals(modelName)){
							content = m;
							content.setParentStructureElement(this);
							break;
						}
					}
				}
				modelName = null;
			}
		
		}
		return l;
	}

	public IStructureElement getParentStructureElement() {
		return parentStructureElement;
	}

	public void removeComponent(IComponentDefinition component) {
		
	}

	public boolean removeFromContent(IStructureElement element) {
		if (element == content){
			content = null;
			listeners.firePropertyChange(P_CONTENT_REMOVED, null, element);
			return true;
		}
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue,  Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		
	}

	public String getCssClass() {
		return cssClass;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("folderPage");
		e.addAttribute("id", getId().substring("folderPage_".length()));
		e.addAttribute("name", getName());
		e.addAttribute("title", getTitle());
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		if (content != null){
			e.addElement("content-model-name").setText(content.getName());
		}
				
		return e;
	}

	public String getId() {
		return "FolderPage_" + id;
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


	public void setParentStructureElement(IStructureElement folder) {
		if (folder instanceof Folder){
			this.parentStructureElement = (Folder)folder;
		}
		
		
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

}
