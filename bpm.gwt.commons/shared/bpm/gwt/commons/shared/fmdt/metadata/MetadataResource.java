package bpm.gwt.commons.shared.fmdt.metadata;

import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataResource implements IsSerializable {
	
	public String name;
	
	private DatabaseColumn column;
	
	private MetadataResources parent;
	
	public MetadataResource() { }

	public MetadataResource(String name, DatabaseColumn column) {
		this.name = name;
		this.column = column;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public DatabaseColumn getColumn() {
		return column;
	}
	
	public void setColumn(DatabaseColumn column) {
		this.column = column;
	}
	
	public MetadataResources getParent() {
		return parent;
	}
	
	public void setParent(MetadataResources parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
