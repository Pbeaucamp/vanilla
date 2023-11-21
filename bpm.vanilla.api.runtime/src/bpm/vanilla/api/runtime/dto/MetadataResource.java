package bpm.vanilla.api.runtime.dto;


import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.IResource;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataResource extends MetadataComponent {
	
	protected String businessModelName;
	protected String businessPackageName;
	
	public MetadataResource() {
		super();
	}
	
	public MetadataResource(RepositoryItem it,IBusinessPackage businessPackage,String catName,String type,IResource resource) {
		super(resource.getName(),new String[]{businessPackage.getBusinessModel().getName(),businessPackage.getName(),"Resources",catName},it,type);
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.businessPackageName = businessPackage.getName();
	}

	public MetadataResource(String id, String name, int metadataID, String type, String businessModelName,String businessPackageName) {
		super(id, name, metadataID, type);
		this.businessModelName = businessModelName;
		this.businessPackageName = businessPackageName;
	}

	public String getBusinessModelName() {
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}
	
	
}
