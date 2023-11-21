package bpm.fwr.api.beans.dataset;

import java.util.List;


public class FwrRelationStrategy implements IResource {

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private int metadataId;
	private String modelParent;
	
	private List<String> tableNames;
	private List<String> relationKeys;
	
	public FwrRelationStrategy() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public String getModelParent() {
		return modelParent;
	}

	public List<String> getTableNames() {
		return tableNames;
	}

	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}

	public List<String> getRelationKeys() {
		return relationKeys;
	}

	public void setRelationKeys(List<String> relationKeys) {
		this.relationKeys = relationKeys;
	}

	public String getLabel() {
		StringBuilder buf = new StringBuilder();
		buf.append(name);
		buf.append(" -> ");
		for(String t : tableNames) {
			buf.append(t + " ");	
		}
		return buf.toString();
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}

	public void setModelParent(String modelParent) {
		this.modelParent = modelParent;
	}
}
