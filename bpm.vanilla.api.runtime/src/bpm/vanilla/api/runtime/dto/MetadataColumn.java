package bpm.vanilla.api.runtime.dto;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.Column.SubType;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataColumn extends MetadataComponent{

	private String businessModelName;
	private String businessPackageName;
	private String businessTableName;
	private String columnType;
	private String sortType;
	
	public MetadataColumn() {
		super();
	}

	public MetadataColumn(String id, String name, int metadataID, String type, String businessModelName, String businessPackageName, String businessTableName, String columnType, String sortType) {
		super(id, name, metadataID, type);
		this.businessModelName = businessModelName;
		this.businessPackageName = businessPackageName;
		this.businessTableName = businessTableName;
		this.columnType = columnType;
		this.sortType = sortType;
	}

	public MetadataColumn(RepositoryItem it,IBusinessPackage businessPackage,IBusinessTable businessTable,IDataStreamElement column) {
		super(column.getName(),new String[] {businessTable.getModel().getName(),businessPackage.getName(),businessTable.getName()},it,"MetadataColumn");
		this.businessModelName = businessTable.getModel().getName();
		this.businessPackageName = businessPackage.getName();
		this.businessTableName = businessTable.getName();
		this.columnType = column.getType().name();
		this.sortType = "None";
	}

	public MetadataColumn(Column column,String columnType) {
		super();
		this.metadataID = column.getMetadataId();
		this.type = "MetadataColumn";
		this.name = column.getName();
		this.columnType = columnType;
		this.businessModelName = column.getBusinessModelParent();
		this.businessPackageName = column.getBusinessPackageParent();
		this.businessTableName = column.getBusinessTableParent();
		this.id = String.valueOf(this.metadataID) + "." + this.businessModelName + "." + this.businessPackageName + "." + this.businessTableName + "." + this.name;
		
		if(column.isSorted()) {
			if(column.getSortType().equals("ASC")) {
				this.sortType = "Ascending";
			}
			else if(column.getSortType().equals("DESC")) {
				this.sortType = "Descending";
			}
		}
		else {
			this.sortType = "None";
		}
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

	public String getColumnType() {
		return columnType;
	}

	public String getSortType() {
		return sortType;
	}
	
	
	public Column createColumn() {
		Column column = new Column();
		
		column.setType(SubType.valueOf(this.columnType));
		column.setBusinessModelParent(this.businessModelName);
		column.setBusinessPackageParent(this.businessPackageName);
		column.setBusinessTableParent(this.businessTableName);
		column.setMetadataId(this.metadataID);
		column.setName(this.name);
		
		
		switch(this.sortType) {
		case "None":
			column.setSorted(false);
			break;
		case "Ascending":
			column.setSorted(true);
			column.setSortType("ASC");
			break;
		case "Descending":
			column.setSorted(true);
			column.setSortType("DESC");
			break;
		}
		
		return column;
	}
	
}
