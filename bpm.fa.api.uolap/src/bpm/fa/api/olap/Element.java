package bpm.fa.api.olap;

import java.io.Serializable;

public abstract class Element implements Serializable {
	private String name;
	private String uname;
	private String caption;
	
	public Element(String name, String uname, String caption) {
		this.name 	= name;
		this.uname	= uname;
		this.caption= caption;
	}
	
	public Element() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getUniqueName() {
		return uname;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
}
