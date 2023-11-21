package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class MetadataResourcesCategorie extends MetadataComponent {
	
	private String businessModelName;
	private String businessPackageName;
	private List<MetadataResource> children;
	
	public MetadataResourcesCategorie(RepositoryItem it,IBusinessPackage businessPackage,String catName,List<IResource> resources) throws Exception {
		super(catName,new String[]{businessPackage.getBusinessModel().getName(),businessPackage.getName(),"Resources"},it,catName);
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.businessPackageName = businessPackage.getName();
		
		this.children = new ArrayList<>();
		for(IResource resource : resources) {
			if(resource instanceof IFilter) {
				this.children.add(new MetadataFilter(it,businessPackage,catName,(IFilter)resource));
			}
			else if(resource instanceof Prompt) {
				this.children.add(new MetadataPrompt(it,businessPackage,catName,(Prompt)resource));
			}
		}
	}

	public String getBusinessModelName() {; 
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}

	public List<MetadataResource> getChildren() {
		return children;
	}
	
	
}
