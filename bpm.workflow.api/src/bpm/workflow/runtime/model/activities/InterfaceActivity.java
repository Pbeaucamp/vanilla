package bpm.workflow.runtime.model.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.ICanFillVariable;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IManual;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.InterfaceObject;

/**
 * Launch a form : FreeDashboard form or Orbeon from
 * @author CHARBONNIER, MARTIN
 *
 */
public class InterfaceActivity extends AbstractActivity implements IRepositoryItem, IComment, IManual, ICanFillVariable {
	private InterfaceObject biObject;
	private String group = "";
	private String comment;
	private HashMap<String, String> mapping = new HashMap<String, String>();
	private String type = "";
	
	public static final String ORBEON_TYPE  = "0";
	public static final String FD_TYPE  = "1";
	
	private static int number = 0;
	
	/**
	 * Create an interface activity with the specified name 
	 * @param name
	 */
	public InterfaceActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	

	
	public InterfaceActivity(){
		super();
		number++;
	}
	
	/**
	 * 
	 * @return the type of the form : "0" -> Orbeon and "1"-> FD
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of the form
	 * @param type ("0"-> Orbeon and "1"-> FD)
	 */
	public void setType(String type) {
		this.type = type;
	}

	
	/**
	 * Set the InterfaceObject
	 * @param biObject
	 */
	public void setInterface(InterfaceObject biObject) {
		this.biObject = biObject;
	}
	
	/**
	 * 
	 * @return the InterfaceObject
	 */
	public InterfaceObject getInterface() {
		return biObject;
	}
	
	
	public void setGroupForValidation(String group) {
		this.group = group;
	}
	
	public String getGroupForValidation() {
		return group;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("interfaceActivity");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (group != null){
			e.addElement("group").setText(group);
		}
		if (!mapping.isEmpty()) {
			e.addElement("mapping");
			for (String key : mapping.keySet()) {
				
				if(!key.equalsIgnoreCase("") && !mapping.get(key).equalsIgnoreCase("")){
					Element map = e.element("mapping").addElement("map"); 	
					map.addElement("key").setText(key);
					map.addElement("value").setText(mapping.get(key));
				}
			}
		}
		
		e.addElement("type").setText(type);
		
		if (biObject != null){
			e.add(biObject.getXmlNode());
		}
		return e;
	}

	public IActivity copy() {
		InterfaceActivity a = new InterfaceActivity();
		a.setName("copy of " + name);
		a.setType(type);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (biObject == null) {
			buf.append("You have to select an Interface for the activity " + name + "\n");
		}
		if (group == null) {
			buf.append("You have to define a performer for the activity " + name + "\n");
		}
		
		return buf.toString();
	}
	
	/**
	 * Add a mapping between the form field and the other object parameter
	 * @param form
	 * @param biObject
	 */
	public void addParameterMapping(String form, String biObject) {
		mapping.put(form, biObject);
	}
	
	/**
	 * Remove the mapping between the form field and the other object parameter
	 * @param form
	 * @param biObject
	 */
	public void removeParameterMapping(String form, String biObject) {
		for (String k : mapping.keySet()) {
			if (k.equalsIgnoreCase(form) && mapping.get(k).equalsIgnoreCase(biObject)) {
				mapping.remove(k);
			}
		}
	}

	/**
	 * 
	 * @return all the mappings
	 */
	public HashMap<String, String> getMappings() {
		return mapping;
	}
	
	public List<String> getParameters(IRepositoryApi sock) {
		if (biObject != null) {
			try {
				return biObject.getParameters(sock);
			} catch (Exception e) {
				return new ArrayList<String>();
			}
		}
		else {
			return new ArrayList<String>();
		}
	}
	
	
	public String getItemName() {
		if (biObject != null)
			return biObject.getItemName();
		else
			return null;
	}

	public void setItemName(String name) {
		if (biObject != null)
			biObject.setItemName(name);
		
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}
	
	public void decreaseNumber() {
		number--;
	}
	
	@Override
	public BiRepositoryObject getItem() {
		return biObject;
	}
	@Override
	public void setItem(BiRepositoryObject obj) {
		if(obj instanceof InterfaceObject) {
			this.biObject = (InterfaceObject) obj;
		}
	}

	@Override
	public void execute() throws Exception {
		
		
	}
}
