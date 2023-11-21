package bpm.geoloc.creator.model;

import java.io.Serializable;

public class CkanResource implements Serializable {

	private String id;
	private String name;
	private String format;
	private String url;
	
	public CkanResource() { }
	
	public CkanResource(String id, String name, String format, String url) {
		this.id = id;
		this.name = name;
		this.format = format;
		this.url = url;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return name + " (" + format + ")";
	}
}
