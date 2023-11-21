package bpm.fd.api.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.FactoryStructure;

/**
 * The simple dashboard creation is useless so i make this class abstract.
 * A simple dashboard is a multipage roject without any additional model 
 * @author ludo
 *
 */
public abstract class FdProject {
	public static final String PROPERTY_ADD_RESOURCE = "bpm.fd.api.core.model.addResource";
	public static final String PROPERTY_REMOVE_RESOURCE = "bpm.fd.api.core.model.removeResource";
	
	private FdModel fdModel;
	private Dictionary dictionary;
	private List<IResource> resources = new ArrayList<IResource>();
	
	private FdProjectDescriptor descriptor;
	private List<String> locales = new ArrayList<String>();
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public FdProject(FdProjectDescriptor descriptor, FdModel model, Dictionary dictionary){
		this.descriptor = descriptor;
		this.fdModel = model;
		model.setProject(this);
		this.dictionary = dictionary;
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener  listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener  listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	protected void firePropertyChange(String propName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propName, oldValue, newValue);
		
	}
	/**
	 * replace the descriptor by the given one
	 * @param descriptor
	 */
	public void adaptToRepositorySource(FdProjectRepositoryDescriptor descriptor) throws Exception{
		this.descriptor = descriptor;
		if (descriptor.getDictionaryDirectoryItemId() == null || descriptor.getModelDirectoryItemId() == null ||
				descriptor.getDictionaryDirectoryItemId() < 1 || descriptor.getModelDirectoryItemId() < 1){
			throw new Exception("Invalid descriptor repository model nor dictionary id");
		}
	}
	
	public FdProject(FdProjectDescriptor descriptor, FdModel model){
		this.descriptor = descriptor;
		this.fdModel = model;
		this.fdModel.setProject(this);
		dictionary = new Dictionary();
		dictionary.setName(this.descriptor.getDictionaryName());
	}
	
	public FdProject(FdProjectDescriptor descriptor){
		this.descriptor = descriptor;
		fdModel = new FdModel(new FactoryStructure(), this);
		dictionary = new Dictionary();
		dictionary.setName(this.descriptor.getDictionaryName());
	}
	
	public List<String> getLocale(){
		return new ArrayList<String>(locales);
	}
	
	public void addLocale(String locale){
		for(String s : getLocale()){
			if (s.equals(locale)){
				return;
			}
		}
		locales.add(locale);
	}
	
	public FdProjectDescriptor getProjectDescriptor(){
		return descriptor;
	}
	
	
	public FdModel  getFdModel(){
		return fdModel;
	}
	
	//ere
	public String  getFdModelAsXML(){
		return fdModel.getElement().asXML();
	}
	
	public String getFdModelAsXML(FdModel model){
		return model.getElement().asXML();
	}
	
	//ere
	public String  getDictionaryAsXML(){
		return dictionary.getElement().asXML();
	}
	
	public Dictionary getDictionary(){
		return dictionary;
	}
	
	public List<IResource> getResources(){
		return new ArrayList<IResource>(resources);
	}
	
	public List<IResource> getResources(Class<?> c){
		List<IResource> l = new ArrayList<IResource>();
		for(IResource r : getResources()){
			if (c.isAssignableFrom(r.getClass())){
				l.add(r);
			}
		}
		return l;
	}
	
	public void addResource(IResource resource){
		if (resource == null){
			return;
		}
		for(IResource r : getResources()){
			if (r.getName().equals(resource.getName())){
				return;
			}
		}
		resources.add(resource);
		firePropertyChange(PROPERTY_ADD_RESOURCE, null, resource);
	}
	
	public void removeResource(IResource resource){
		if (resources.remove(resource)){
			firePropertyChange(PROPERTY_REMOVE_RESOURCE, resource, null);
		}
	}

	public void setDictionary(Dictionary dico) {
		this.dictionary = dico;
		
	}

	/**
	 * 
	 * @return the IComponentDefinition presents in the DIctionary but
	 * not used in any FdMOdel
	 */
	public Collection<IComponentDefinition> getAvailableComponents() {
		
		
		HashMap<IComponentDefinition, ComponentConfig> map = getFdModel().getComponents();
		List<IComponentDefinition> l = new ArrayList<IComponentDefinition>();
		for(IComponentDefinition def : getDictionary().getComponents()){
			if (!map.keySet().contains(def) && !l.contains(def)){
				l.add(def);
			}
		}
		return l;
		
	}
	
	/**
	 * 
	 * @return the IComponentDefinition used in all the project's Models
	 */
	public Collection<IComponentDefinition> getUsedComponents(){
		return getFdModel().getComponents().keySet();
	}


	/**
	 * convenient method to walk into the Whole projetc looking for 
	 * Cell Configs using the given component as parameter provider
	 * 
	 * 
	 * @param def
	 */
	public void updateParameterProvider(String oldComponentName, IComponentDefinition def) {
		for(Cell c : fdModel.getCellsUsingParameterProviderNames(oldComponentName)){
			for(ComponentParameter p : c.getConfig(def).getParameters()){
				if (c.getConfig(def).getComponentNameFor(p).equals(oldComponentName)){
					c.getConfig(def).setParameterOrigin(p, def.getName());
				}
			}
		}
		
	}


	public HashMap<IComponentDefinition, ComponentConfig> getComponents() {
		return getFdModel().getComponents();
	}


	public IResource getResource(Class<?> class1, String resourceName) {
		for(IResource r : getResources(class1)){
			if (r.getName().equals(resourceName)){
				return r;
			}
		}
		return null;
	}


	public void setDescriptor(FdProjectDescriptor projectDescriptor) {
		descriptor = projectDescriptor;
	}


	
}
 