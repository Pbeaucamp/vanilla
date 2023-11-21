package bpm.vanilla.api.runtime.dto;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public abstract class MetadataComponent {
	
	protected String id;
	protected String name;
	protected int metadataID;
	protected String type;
	
	public MetadataComponent() {
		
	}
	
	public MetadataComponent(String id, String name, int metadataID, String type) {
		this.id = id;
		this.name = name;
		this.metadataID = metadataID;
		this.type = type;
	}

	public MetadataComponent(String name,String[] componentNames ,RepositoryItem metadataItem,String type) {
		this.name = name;
		this.metadataID = metadataItem.getId();
		this.type = type; 
		
		this.id = String.valueOf(metadataID) + ".";
		
		if(componentNames != null) {
			for(String componentName : componentNames) {
				this.id += componentName + ".";
			}		
		}
		
		this.id += name;

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getMetadataID() {
		return metadataID;
	}

	public String getType() {
		return type;
	}

	
	
}
