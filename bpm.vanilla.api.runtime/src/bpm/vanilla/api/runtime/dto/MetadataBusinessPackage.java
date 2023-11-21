package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.DataSource;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataSource;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataBusinessPackage extends MetadataComponent{
	
	private String businessModelName;
	private List<MetadataComponent> children;
	private boolean isOnOlap;
	
	public MetadataBusinessPackage(Group group,RepositoryItem it,IBusinessPackage businessPackage) throws Exception {
		super(businessPackage.getName(),new String[]{businessPackage.getBusinessModel().getName()},it,"BusinessPackage");
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.isOnOlap = businessPackage.isOnOlapDataSource();
		children = new ArrayList<>();
		for(IBusinessTable businessTable : businessPackage.getBusinessTables(group.getName())) {
			children.add(new MetadataBusinessTable(group,it,businessPackage,businessTable));
		}
		children.add(new MetadataResources(group,it,businessPackage));
		children.add(new MetadataSavedQueries(group,it,businessPackage));
	}
	
	public String getBusinessModelName() {
		return businessModelName;
	}
	
	public boolean isOnOlap() {
		return isOnOlap;
	}

	public List<MetadataComponent> getChildren() {
		return children;
	}

	
}
