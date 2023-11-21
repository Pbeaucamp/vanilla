package bpm.fd.api.core.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.dom4j.Element;

import bpm.fd.api.core.model.resources.IResource;

/**
 * 
 * @author LCA
 *
 */
public class FdProjectRepositoryDescriptor extends FdProjectDescriptor{
	private Integer modelDirectoryItemId ;
	private Integer dictionaryDirectoryItemId;
	private Date loadingModelDate = null;
	private Date loadingDictionaryDate = null;
	
	private HashMap<IResource, Integer> resourcesId = new HashMap<IResource, Integer>();
	private HashMap<FdModel, Integer> modelPageId = new HashMap<FdModel, Integer>();
	
	
	public FdProjectRepositoryDescriptor(FdProjectDescriptor descriptor){
		this.setAuthor(descriptor.getAuthor());
		this.setDescription(descriptor.getDescription());
		this.setCreation(descriptor.getCreation());
		this.setDictionaryName(descriptor.getDictionaryName());
		this.setLocation(descriptor.getLocation());
		this.setModelName(descriptor.getModelName());
		this.setProjectName(descriptor.getProjectName());
		this.setProjectVersion(descriptor.getProjectVersion());
		this.setInternalApiDesignVersion(descriptor.getInternalApiDesignVersion());
	}
	
	public Date getLoadingModelDate() {
		return loadingModelDate;
	}

	public void setLoadingModelDate(Date loadingDate) {
		this.loadingModelDate = loadingDate;
	}

	public Collection<IResource> getResourcesKeys(){
		return resourcesId.keySet();
	}
	
	public Collection<FdModel> getPagesKeys(){
		return modelPageId.keySet();
	}
	
	public Date getLoadingDictionaryDate() {
		return loadingDictionaryDate;
	}

	public void setLoadingDictionaryDate(Date loadingDate) {
		this.loadingDictionaryDate = loadingDate;
	}

	
	
	public void addResourceId(IResource resource, int id){
		resourcesId.put(resource, id);
	}
	
	public void addModelPageId(FdModel modelPage, int id){
		modelPageId.put(modelPage, id);
	}
	
	public Integer getResourceId(IResource resource){
		return resourcesId.get(resource);
	}
	
	public Integer getModelPageId(FdModel model){
		return modelPageId.get(model);
	}
	
	public Integer getModelDirectoryItemId() {
		return modelDirectoryItemId;
	}
	public void setModelDirectoryItemId(Integer modelDirectoryItemId) {
		this.modelDirectoryItemId = modelDirectoryItemId;
	}
	public Integer getDictionaryDirectoryItemId() {
		return dictionaryDirectoryItemId;
	}
	public void setDictionaryDirectoryItemId(Integer dictionaryDirectoryItemId) {
		this.dictionaryDirectoryItemId = dictionaryDirectoryItemId;
	}

	@Override
	public Element getElement() {
		
		Element e =  super.getElement();
		e.addAttribute("modelRepositoryItemId", getModelDirectoryItemId() + "");
		e.addAttribute("dictionaryRepositoryItemId", getDictionaryDirectoryItemId() + "");
		
		return e;
	}

	public void replaceModelId(FdModel model, Integer modelPageId) {
		FdModel old = null;
		for(Entry<FdModel, Integer> entry : this.modelPageId.entrySet()) {
			if(entry.getValue().intValue() == modelPageId.intValue()) {
				old = entry.getKey();
				break;
			}
		}
		this.modelPageId.remove(old);
		this.modelPageId.put(model, modelPageId);
	}

	public void replaceResourceId(IResource res, Integer resourceId) {
		IResource old = null;
		for(Entry<IResource, Integer> entry : resourcesId.entrySet()) {
			if(entry.getValue().intValue() == resourceId.intValue()) {
				old = entry.getKey();
				break;
			}
		}
		resourcesId.remove(old);
		resourcesId.put(res, resourceId);
	}

}
