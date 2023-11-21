package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.DatabaseTable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataModel implements IsSerializable {
	
	private String name;

	private List<DatabaseTable> tables;
	private List<MetadataPackage> packages;
	private MetadataRelation relation;
	
	private Metadata parent;
	
	public MetadataModel() { }
	
	public MetadataModel(String name) {
		this.name = name;
		setPackages(new ArrayList<MetadataPackage>());
	}
	
	public MetadataModel(String name, MetadataPackage pack, List<DatabaseTable> tables, MetadataRelation relation) {
		this.name = name;
		this.tables = tables;
		setRelation(relation);
		
		addPackage(pack);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<DatabaseTable> getTables() {
		return tables;
	}
	
	public void setTables(List<DatabaseTable> tables) {
		this.tables = tables;
	}
	
	public List<MetadataPackage> getPackages() {
		return packages;
	}
	
	public void setPackages(List<MetadataPackage> packages) {
		this.packages = packages;
		if (packages != null) {
			for (MetadataPackage pack : packages) {
				pack.setParent(this);
			}
		}
	}
	
	public void addPackage(MetadataPackage pack) {
		if (packages == null) {
			this.packages = new ArrayList<MetadataPackage>();
		}
		pack.setParent(this);
		this.packages.add(pack);
	}
	
	public MetadataRelation getRelation() {
		return relation;
	}

	public void setRelation(MetadataRelation relation) {
		relation.setParent(this);
		this.relation = relation;
	}
	
	public Metadata getParent() {
		return parent;
	}
	
	public void setParent(Metadata parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
