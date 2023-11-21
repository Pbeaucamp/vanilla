package bpm.fwr.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FusionMapDTO implements IsSerializable{

	private long id;
	private String name;
	private String description;
	private String swfUrl;
	private String type;
	
	public FusionMapDTO(){ }
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSwfUrl() {
		return swfUrl;
	}
	
	public void setSwfUrl(String swfUrl) {
		this.swfUrl = swfUrl;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
