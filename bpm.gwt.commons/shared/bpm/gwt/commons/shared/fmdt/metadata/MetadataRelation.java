package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataRelation implements IsSerializable {
	
	private MetadataModel parent;
	private List<TableRelation> relations;
	
	public MetadataRelation() { }

	public MetadataRelation(List<TableRelation> relations) {
		this.relations = relations;
	}
	
	public MetadataModel getParent() {
		return parent;
	}
	
	public void setParent(MetadataModel parent) {
		this.parent = parent;
	}
	
	public List<TableRelation> getRelations() {
		return relations;
	}
	
	public void setRelations(List<TableRelation> relations) {
		this.relations = relations;
	}
	
	public void addRelation(TableRelation relation) {
		if (relations == null) {
			this.relations = new ArrayList<TableRelation>();
		}
		this.relations.add(relation);
	}
	
	@Override
	public String toString() {
		return "Relations";
	}
}
