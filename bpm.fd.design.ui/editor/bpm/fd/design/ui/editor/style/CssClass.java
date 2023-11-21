package bpm.fd.design.ui.editor.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CssClass {
	private String name;
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	public CssClass(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public String getValue(String propertyName){
		return properties.get(propertyName);
	}
	public void setValue(String pName, String pValue){
		this.properties.put(pName, pValue);
	}
	public List<String> getProperties() {
		return new ArrayList<String>(properties.keySet());
	}
	
}
