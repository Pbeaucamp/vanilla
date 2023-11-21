package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataResources extends MetadataComponent {
	
	private String businessModelName;
	private String businessPackageName;
	private List<MetadataResourcesCategorie> children;
	
	public MetadataResources(Group group,RepositoryItem it,IBusinessPackage businessPackage) throws Exception {
		super("Resources",new String[]{businessPackage.getBusinessModel().getName(),businessPackage.getName()},it,"ResourcesComponent");
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.businessPackageName = businessPackage.getName();
		
		List<IResource> filters = new ArrayList<>();
		List<IResource> prompts = new ArrayList<>();
		for(IResource resource : businessPackage.getResources(group.getName())) {
			if(resource instanceof IFilter) {
				filters.add((IFilter) resource);
			}
			else if(resource instanceof Prompt) {
				prompts.add((Prompt) resource);
			}
		}
		
		this.children = new ArrayList<>();
		
		if(!filters.isEmpty()) {
			this.children.add(new MetadataResourcesCategorie(it,businessPackage,"Filters",filters));
		}
		if(!prompts.isEmpty()) {
			this.children.add(new MetadataResourcesCategorie(it,businessPackage,"Prompts",prompts));
		}
		
		
	}

	public String getBusinessModelName() {
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}

	public List<MetadataResourcesCategorie> getChildren() {
		return children;
	}
	
	
}
