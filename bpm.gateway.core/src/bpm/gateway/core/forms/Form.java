package bpm.gateway.core.forms;

import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.server.userdefined.Parameter;

/**
 * this class provide information about an Orbeon Xform stored on a 
 * vanilla repository
 * @author LCA
 *
 */
public class Form {
	private int directoryItemId;
	
		
	protected HashMap<String, String> mapping = new HashMap<String, String>();
	
	private String name;


	public void map(Parameter p, int paramid){
		
		for(String s : mapping.keySet()){
			if (s.equals(p.getName())){
				mapping.put(s, paramid + "");
				return;
			}
		}
		mapping.put(p.getName(), paramid + "");
	}
	
	public void map(String p, String paramid){
		mapping.put(p, paramid);
	}

	public HashMap<String, String> getMappings(){
		return new HashMap<String, String>(mapping);
	}
	

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	
	public void setDirectoryItemId(String directoryItemId) {
		this.directoryItemId = Integer.parseInt(directoryItemId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Element getElement(){
		Element el = DocumentHelper.createElement("form");
		el.addElement("directoryItem").setText(directoryItemId + "");
		el.addElement("name").setText(name);
		

		for(String p : mapping.keySet()){
			Element m = el.addElement("map");
			m.addElement("parameterName").setText(p);
			m.addElement("parameterId").setText(mapping.get(p)+ "");
			
		}
		
		return el; 
	}

	public void unmap(String mp) {
		for(String p : mapping.keySet()){
			if (mp.equals(p)){
				mapping.remove(p);
				return;
			}
		}
		
	}
}
