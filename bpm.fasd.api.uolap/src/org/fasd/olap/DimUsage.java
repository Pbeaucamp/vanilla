package org.fasd.olap;

public class DimUsage {
	private String name = "";
	private String source = "";
	private String fkey = "";
	private String caption = "";
	
	public DimUsage(){
		
	}
	public String getFkey() {
		return fkey;
	}
	public void setFkey(String key) {
		fkey = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
}
