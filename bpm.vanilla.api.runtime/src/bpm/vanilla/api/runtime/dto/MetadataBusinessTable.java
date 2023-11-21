package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataBusinessTable extends MetadataComponent{

	private String businessModelName;
	private String businessPackageName;
	private List<MetadataComponent> children;
	
	public MetadataBusinessTable(Group group,RepositoryItem it,IBusinessPackage businessPackage,IBusinessTable businessTable) {
		super(businessTable.getName(),new String[] {businessTable.getModel().getName(),businessPackage.getName()},it,"BusinessTable");
		this.businessModelName = businessTable.getModel().getName();
		this.businessPackageName = businessPackage.getName();
		
		if(businessTable.getColumns(group.getName()) != null) {
			children = new ArrayList<>();
			for(IDataStreamElement col : businessTable.getColumns(group.getName())) {
				children.add(new MetadataColumn(it,businessPackage,businessTable,col));
			}
		}
		else {
			children = null;
		}

	}


	public String getBusinessModelName() {
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}


	public List<MetadataComponent> getChildren() {
		return children;
	}

}
