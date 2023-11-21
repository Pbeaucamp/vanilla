package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlfrescoExport implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String frequency;
	private String sourceName;
	private int sourceId=0;
	private String dest;
	private List<AlfrescoType> types = new ArrayList<AlfrescoType> ();
	
	public AlfrescoExport() {
		super();
	}
	
	public AlfrescoExport(int id, String name, String frequency, String sourceName, int sourceId,
			String dest, int typesId) {
		super();
		this.id = id;
		this.name = name;
		this.frequency = frequency;
		this.sourceName = sourceName;
		this.dest = dest;
		this.sourceId=sourceId;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	
	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public List<AlfrescoType> getTypes() {
		return types;
	}
	public void setTypes(List<AlfrescoType> types) {
		this.types = types;
	}
	public void addType (AlfrescoType type) {
		types.add(type);
	}
	public void removeType(AlfrescoType type) {
		this.types.remove(type);
	}
	
	
}
