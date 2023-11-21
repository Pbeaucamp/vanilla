package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.SavedQuery;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataSavedQueries extends MetadataComponent {

	private String businessModelName;
	private String businessPackageName;
	private List<MetadataSavedQuery> children;
	
	public MetadataSavedQueries(Group group,RepositoryItem it,IBusinessPackage businessPackage) throws Exception {
		super("Saved Queries",new String[]{businessPackage.getBusinessModel().getName(),businessPackage.getName()},it,"SavedQueriesComponent");
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.businessPackageName = businessPackage.getName();
		
		if((businessPackage.getSavedQueries() != null) && (!businessPackage.getSavedQueries().isEmpty())) {
			this.children = new ArrayList<>();
			
			for(SavedQuery query : businessPackage.getSavedQueries()) {
				children.add(new MetadataSavedQuery(group,it,businessPackage,query));
			}
		}
	}

	public String getBusinessModelName() {
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}

	public List<MetadataSavedQuery> getChildren() {
		return children;
	}
	
	
}
