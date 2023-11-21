package bpm.fwr.shared.models.metadata;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FwrBusinessObject implements IsSerializable{
	private String name;
	private String title;
	private String description;
	
	private HashMap<String,String> titles = new HashMap<String, String>();
	 
	public FwrBusinessObject(){ }
	
	public FwrBusinessObject(String n, String description){
		this.name = n;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<String, String> getTitles() {
		return titles;
	}

	public void setTitles(HashMap<String, String> titles) {
		this.titles = titles;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
