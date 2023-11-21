package bpm.vanilla.api.runtime.dto;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.SavedQuery;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataQueryColumn extends MetadataComponent {

	private String businessModelName;
	private String businessPackageName;
	private String businessTableName;
	private String savedQueryName;
	private String sortType;
	private String columnType;
	
	public MetadataQueryColumn(Group group,RepositoryItem it,IBusinessPackage businessPackage,SavedQuery query,IDataStreamElement column) {
		super(column.getName(),new String[] {businessPackage.getBusinessModel().getName(),businessPackage.getName(),"Saved Queries",query.getName()},it,"MetadataColumn");
		this.businessModelName = businessPackage.getBusinessModel().getName();
		this.businessPackageName = businessPackage.getName();
		
		IBusinessTable businessTable = findBusinessTable(group,businessPackage,column);
		if(businessTable != null) {
			this.businessTableName = businessTable.getName();
		}
		else {
			this.businessTableName = null;
		}
		
		this.savedQueryName = query.getName();
		this.columnType = column.getType().name();
		this.sortType = "None";
	}

	public String getBusinessModelName() {
		return businessModelName;
	}

	public String getBusinessPackageName() {
		return businessPackageName;
	}
	
	public String getBusinessTableName() {
		return businessTableName;
	}

	public String getSavedQueryName() {
		return savedQueryName;
	}

	public String getColumnType() {
		return columnType;
	}
	
	public String getSortType() {
		return sortType;
	}

	private IBusinessTable findBusinessTable(Group group,IBusinessPackage businessPackage,IDataStreamElement column) {
		for(IBusinessTable businessTable : businessPackage.getBusinessTables(group.getName())) {
			for(IDataStreamElement tableCol : businessTable.getColumns(group.getName())) {
				if(tableCol.getName().equals(column.getName())) {
					return businessTable;
				}
			}
		}
		
		return null;
	}
	
}
