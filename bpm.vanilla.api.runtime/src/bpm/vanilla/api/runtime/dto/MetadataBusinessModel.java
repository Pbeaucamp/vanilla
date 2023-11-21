package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataBusinessModel extends MetadataComponent{
	
	private List<MetadataBusinessPackage> children;
	
	public MetadataBusinessModel(Group group,RepositoryItem it,IBusinessModel businessModel) throws Exception {
		super(businessModel.getName(),null,it,"BusinessModel");
		
		children = new ArrayList<>();
		for(IBusinessPackage businessPackage : businessModel.getBusinessPackages(group.getName())) {
			children.add(new MetadataBusinessPackage(group,it,businessPackage));
		}
	}

	public List<MetadataBusinessPackage> getChildren() {
		return children;
	}
	
	
	
}
