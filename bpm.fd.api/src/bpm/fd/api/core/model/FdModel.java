package bpm.fd.api.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;

public class FdModel implements IStructureElement{
	public static final String COMPONENT_CONFIG_CHANGED = "bpm.fd.api.core.model.structure.properties.componentConfigChanged";
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private int modelOrigin;

	private List<IBaseElement> content = new ArrayList<IBaseElement>();

	private FdProject project;
	private String cssClass;
	private String name;
	
	private HashMap<Integer, String[]> repositoryDependancies = new HashMap<Integer, String[]>();
	
	private FactoryStructure structureFactory = new FactoryStructure();
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	
	private IStructureElement parentStructure;
	
	public FdModel(FactoryStructure factory){
		this.structureFactory = factory;
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	public FdModel(FactoryStructure factory, FdProject project, String name){
		this.structureFactory = factory;
		this.name = name;
		this.project = project;
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}

	/**
	 * ere: For external access, i dont like linkage errors :)
	 * @return
	 */
	public String asXML() {
		return getElement().asXML();
	}
	
	/**
	 * remove all the dependancies reference to the repository
	 */
	public void cleanDependancies(){
		repositoryDependancies.clear();
	}
	
	/**
	 * add a dependancies to publish on repository
	 * 
	 * if the Project is a multipage, all included models will share the same 
	 * dependencies(adding a dependency to the root model will
	 * add the same dependency on all the sub pages) 
	 *
	 * 
	 * @param directoryItemId : id of the resource on the repository
	 * @param resourceClass : IResource.class
	 * @param resourceName : the resourceName
	 * 
	 * 
	 * warning: 
	 * when using this method dont forget to call cleanDependancies right after the
	 * model is published, if it's not done, and the model is republished, the references element
	 * will be still present, republished and doubled
	 */
	public void addDepenciesRepository(int directoryItemId, Class<?> resourceClass, String resourceName, String localeName){
		for(Integer i : repositoryDependancies.keySet()){
			if (i.intValue() == directoryItemId){
				return;
			}
		}
		
		
		
		
		
		if (getProject() != null && getProject() instanceof MultiPageFdProject && !FdModel.class.isAssignableFrom(resourceClass)){
			
			if (((MultiPageFdProject)getProject()).getFdModel() == this){
				for(FdModel m : ((MultiPageFdProject)getProject()).getPagesModels()){
					m.addDepenciesRepository(directoryItemId, resourceClass, resourceName, localeName);
				}
			}
			
		}
		if (FdModel.class.isAssignableFrom(resourceClass)){
			if (getProject() != null && getProject() instanceof MultiPageFdProject){
				
				if (((MultiPageFdProject)getProject()).getFdModel() == this){
					repositoryDependancies.put(directoryItemId, new String[]{resourceClass.getName(), resourceName, localeName});
				}
			}
		}
		else{
			repositoryDependancies.put(directoryItemId, new String[]{resourceClass.getName(), resourceName, localeName});
		}
		
		
	}
	
	public FactoryStructure getStructureFactory(){
		return structureFactory;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public FdModel(FactoryStructure factory, FdProject project){
		this.project = project;
		this.structureFactory = factory;
		name = project.getProjectDescriptor().getModelName();
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}
	
	protected void setProject(FdProject project){
		this.project = project;
		if (name == null || "".equals(name)){
			name = project.getProjectDescriptor().getModelName();
		}
		
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	
	public FdProject getProject(){
		return project;
	}
	

	
	
	public int getModelOrigin() {
		return modelOrigin;
	}

	
	

	public List<IBaseElement> getContent() {
		List<IBaseElement> result = new ArrayList<IBaseElement>(content);
		
		try {
			Collections.sort(result, new Comparator<IBaseElement>() {
				@Override
				public int compare(IBaseElement o1, IBaseElement o2) {
					if(o1 instanceof Cell && !(o1 instanceof StackableCell)) {
						if(!(o2 instanceof Cell) || o2 instanceof StackableCell) {
							return -1;
						}
					}
					else {
						if(o2 instanceof Cell && !(o2 instanceof StackableCell)) {
							return 1;
						}
					}
					return 0;
				}
			});
		} catch(Exception e) {
		}
		
		return result;
	}
	
	
	

	public boolean addToContent(IStructureElement element) {
		boolean b =  content.add(element);
		if (b){
			element.setParentStructureElement(this);
			listeners.firePropertyChange(P_CONTENT_ADDED, null, element);
		}
		return b;
	}

	public IStructureElement getParentStructureElement() {
		return parentStructure;
	}

	public boolean removeFromContent(IStructureElement element) {
		boolean b =  content.remove(element);
		if (b){
			
			listeners.firePropertyChange(P_CONTENT_REMOVED, null, element);
			
		}
		return b;
		
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("freedashboard");
		e.addAttribute("version", Versionning.MODEL_VERSION);
		e.addElement("id");
		//for compatibility with BIRepository Server
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Element docP = e.addElement("document-properties");
		docP.addElement("name").setText(getName());
		docP.addElement("author").setText(getProject().getProjectDescriptor().getAuthor());
		docP.addElement("description").setText(getProject().getProjectDescriptor().getDescription());
		docP.addElement("creation").setText(sdf.format(getProject().getProjectDescriptor().getCreation()));
		docP.addElement("modification").setText(sdf.format(Calendar.getInstance().getTime()));
		docP.addElement("version").setText(getProject().getProjectDescriptor().getProjectVersion());
		docP.addElement("icon").setText("");
		docP.addElement("flyover").setText("");
		
		
		
		e.add(getProject().getProjectDescriptor().getElement());
		
		Element dependencies = e.addElement("dependancies");
		
		
		//we need the dictionary in first
		//the models in second
		//and the resources in third to avoid some problem when rebuilding projetc
		
		//only dico dependancy
		for(Integer i : this.repositoryDependancies.keySet()){
			if (Dictionary.class.getName().equals(repositoryDependancies.get(i)[0])){
				Element dep = dependencies.addElement("dependantDirectoryItemId").addAttribute("name", repositoryDependancies.get(i)[1]); 
				dep.setText(i +"");
				dep.addAttribute("class", repositoryDependancies.get(i)[0]);
				
				if (repositoryDependancies.get(i)[2] != null){
					dep.addAttribute("localeName", repositoryDependancies.get(i)[2]);
				}
			}
		}
		//only models dependancy
		for(Integer i : this.repositoryDependancies.keySet()){
			if (FdModel.class.getName().equals(repositoryDependancies.get(i)[0]) || FdVanillaFormModel.class.getName().equals(repositoryDependancies.get(i)[0])){
				Element dep = dependencies.addElement("dependantDirectoryItemId").addAttribute("name", repositoryDependancies.get(i)[1]); 
				dep.setText(i +"");
				dep.addAttribute("class", repositoryDependancies.get(i)[0]);
				
				if (repositoryDependancies.get(i)[2] != null){
					dep.addAttribute("localeName", repositoryDependancies.get(i)[2]);
				}
			}
		}
		//only resources
		for(Integer i : this.repositoryDependancies.keySet()){
			if (FdModel.class.getName().equals(repositoryDependancies.get(i)[0]) || 
				FdVanillaFormModel.class.getName().equals(repositoryDependancies.get(i)[0]) ||
				Dictionary.class.getName().equals(repositoryDependancies.get(i)[0])){
				continue; 
			}
			
			Element dep = dependencies.addElement("dependantDirectoryItemId").addAttribute("name", repositoryDependancies.get(i)[1]); 
			dep.setText(i +"");
			dep.addAttribute("class", repositoryDependancies.get(i)[0]);
			
			if (repositoryDependancies.get(i)[2] != null){
				dep.addAttribute("localeName", repositoryDependancies.get(i)[2]);
			}
		}
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		
		
		Element structure = e.addElement("structure");
		if (getCssClass() != null){
			structure.addAttribute("cssClass", getCssClass());
		}
		
		for(IBaseElement baseE : getContent()){
			structure.add(baseE.getElement());
		}
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		return e;
	}

	public String getId() {
		return getName().replace(" ", "_");
	}



	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}



	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}
	public String getName() {
		return name;
	}

	private HashMap<IComponentDefinition, ComponentConfig> getComponents(IStructureElement struct) {
		HashMap<IComponentDefinition, ComponentConfig> l = new HashMap<IComponentDefinition, ComponentConfig>();
		if (struct != null){
			for(IBaseElement e : struct.getContent()){
				if (e == null){
					continue;
				}
				if (e instanceof IStructureElement){
					if (e instanceof DrillDrivenStackableCell){
						for(IComponentDefinition d : ((DrillDrivenStackableCell)e).getComponents()){
							l.put(d, ((DrillDrivenStackableCell)e).getConfig(d));
						}
					}
					else{
						HashMap<IComponentDefinition, ComponentConfig> m = getComponents((IStructureElement)e);
						for(IComponentDefinition k : m.keySet()){
							l.put(k, m.get(k));
						}
					}
					
					
				}
				else if (e instanceof IComponentDefinition){
					
					if(struct instanceof Cell) {
						l.put((IComponentDefinition)e, ((Cell)struct).getConfig((IComponentDefinition)e));
					}
					else {
						l.put((IComponentDefinition)e, ((StackableCell)struct).getConfig((IComponentDefinition)e));
					}
				}
			}
		}
		
		return l;
	}
	
	public HashMap<IComponentDefinition, ComponentConfig> getComponents(){
		return getComponents(this);
	}
	
	
	public List<ComponentConfig> getComponentConfigurations(IStructureElement struct){
		List<ComponentConfig> l = new ArrayList<ComponentConfig>();
		
		for(IBaseElement e : struct.getContent()){
			if ( e instanceof Cell){
				l.addAll(((Cell)e).getConfigs());
			}
			if (e instanceof IStructureElement){
				l.addAll(getComponentConfigurations((IStructureElement)e));
			}
		}
		return l;
	}
	
	public HashMap<IComponentDefinition, String> getComponentsOutputVariables(){
		
		if (getProject() instanceof MultiPageFdProject){
			if (((MultiPageFdProject)getProject()).getFdModel() != this){
				return ((MultiPageFdProject)getProject()).getFdModel().getComponentsOutputVariables();
			}
			
		}
		HashMap<IComponentDefinition, String> outputs = new HashMap<IComponentDefinition, String>();
		HashMap<IComponentDefinition, ComponentConfig> configs = getComponents();
		
		
		for(IComponentDefinition def : configs.keySet()){
			
			
			for(ComponentParameter p : configs.get(def).getParameters()){
				
				String sourceName = configs.get(def).getComponentNameFor(p);
				IComponentDefinition source = getProject().getDictionary().getComponent(sourceName);
				
//				for(IComponentDefinition d : configs.keySet()){
//					if (d.getName().equals(sourceName)){
//						source = d;
//						break;
//					}
//				}
				if (outputs.get(source) != null){
					continue;
				}
				else{
					outputs.put(source, p.getName() + "Parameter");
				}
			}
		}
		
				
		return outputs;
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


	public void removeComponent(IComponentDefinition def) {
		List<IComponentDefinition> toRemove = new ArrayList<IComponentDefinition>();
		List<IStructureElement> structtoRemove = new ArrayList<IStructureElement>();
		for (IBaseElement e : getContent()){
			if (e instanceof IComponentDefinition && e == def){
				toRemove.add(def);
			}
			else{
				
				if (e.getClass() == Cell.class && ((IStructureElement)e).getContent().contains(def) && FdProjectDescriptor.API_DESIGN_VERSION.equals(getProject().getProjectDescriptor().getInternalApiDesignVersion())){
					//for new api, we remove 
					structtoRemove.add(((IStructureElement)e));
				}
				else{
					((IStructureElement)e).removeComponent((IComponentDefinition)def);
				}
				

			}
			
		}
		
		content.removeAll(toRemove);
		content.removeAll(structtoRemove);
		
		
		
		firePropertyChange(P_CONTENT_REMOVED, def, null);
		for(Object o : structtoRemove){
			firePropertyChange(P_CONTENT_REMOVED, o, null);
		}
	}


	public void addDepenciesRepository(String idResourceFile, Integer integer, Class<? extends IResource> class1) {
		
		
	}

	public void setParentStructureElement(IStructureElement parent) {
		this.parentStructure = parent;
		
	}
	
	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}
	
	public void setEventScript(HashMap<ElementsEventType, String> eventScript) {
		this.eventScript = eventScript;
	}
	
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
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

	/**
	 * 
	 * @param oldComponentName
	 * @return the Cell with a component which have a parameter set by a componentNamed oldComponentname
	 */
	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName) {
		List<Cell> l = new ArrayList<Cell>();
		
		for(IBaseElement e : getContent()){
			if (e instanceof IStructureElement){
				l.addAll(((IStructureElement)e).getCellsUsingParameterProviderNames(oldComponentName));
			}
		}
		
		return l;
	}





	
}
