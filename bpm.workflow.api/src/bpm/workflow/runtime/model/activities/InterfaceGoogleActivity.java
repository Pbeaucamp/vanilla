package bpm.workflow.runtime.model.activities;


import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IManual;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;
import bpm.workflow.runtime.resources.InterfaceObject;

/**
 * Launch a google document
 * @author Charles MARTIN
 *
 */
public class InterfaceGoogleActivity extends AbstractActivity implements IRepositoryItem, IComment, IManual {
	private InterfaceObject biObject;
	private String group = "";
	private String comment;
	private static int number = 0;
	
	
	/**
	 * Create a Google Doc activity with the specified name
	 * @param name
	 */
	public InterfaceGoogleActivity(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	
	public InterfaceGoogleActivity(){
		super();
		number++;
	}
	
	/**
	 * Set the URL Object containing the url of the Google document
	 * @param biObject
	 */
	public void setInterface(InterfaceObject biObject) {
		this.biObject = biObject;
	}
	
	/**
	 * 
	 * @return the URL Object containing the url of the Google document
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
		e.setName("interfaceGoogleActivity");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (group != null){
			e.addElement("group").setText(group);
		}
		
		
		
		
		if (biObject != null){
			e.add(biObject.getXmlNode());
		}
		return e;
	}

	public IActivity copy() {
		InterfaceGoogleActivity a = new InterfaceGoogleActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (biObject == null) {
			buf.append("You have to select an Interface for the activity " + name + "\n");
		}
		
		
		return buf.toString();
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
