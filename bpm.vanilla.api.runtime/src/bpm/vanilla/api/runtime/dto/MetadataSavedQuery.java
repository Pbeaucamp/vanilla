package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataSavedQuery extends MetadataComponent {
	
	private List<MetadataQueryColumn> children;
	
	public MetadataSavedQuery(Group group,RepositoryItem it,IBusinessPackage businessPackage,SavedQuery savedQuery) throws Exception {
		super(savedQuery.getName(),new String[]{businessPackage.getBusinessModel().getName(),businessPackage.getName(),"Saved Queries"},it,"SavedQuery");
		
		QuerySql sql = savedQuery.loadQuery(group.getName(), businessPackage);
		this.children = new ArrayList<>();
		for(IDataStreamElement col : sql.getSelect()) {
			this.children.add(new MetadataQueryColumn(group,it,businessPackage,savedQuery,col));
		}
	}

	public List<MetadataQueryColumn> getChildren() {
		return children;
	}
	
	
}
