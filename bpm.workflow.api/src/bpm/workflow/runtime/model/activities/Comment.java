package bpm.workflow.runtime.model.activities;


import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;

public class Comment extends AbstractActivity implements IComment{
	
	public static final int SIMPLE = 0;
	public static final int SUPPORT = 1;
	public static final int BLOCK = 2;
	public static final int QUESTION = 3;
	private String comment;
	
	
	public static final String[] TYPES_NAME = {"Simple Comment", "Demand of support", "Blockage in development", "General question of order"};
	private int type = 0;

	
	/**
	 * Create a comment with the specified name
	 * @param name
	 */
	protected Comment(String name){
		this.name = name.replaceAll("[^a-zA-Z0-9]", "");
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	/**
	 * do not use, for XML parsing only
	 */
	public Comment(){
		
	}
	
	/**
	 * do not use, for XML parsing only
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Create a comment with the specified : 
	 * @param name
	 * @param description
	 * @param type
	 */
	protected Comment(String name, String description, String type){
		this.name = name;
		this.description = description;
		this.type = new Integer(type);
	}
	
/**
 * 
 * @return the type of the comment (String)
 */
	public String getType() {
		return TYPES_NAME[type];
	}
	
	/**
	 * 
	 * @return the type of the comment (int)
	 */
	public int getTypeInt() {
		return type;
	}

	/**
	 * Set the type of the comment
	 * @param type
	 */
	public void setType(String type) {
		this.type = new Integer(type).intValue();
	}

	/**
	 * Set the type of the comment
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("comment");
		e.setName("comment");
		e.addElement("id").setText(id);
		e.addElement("name").setText(name);
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (description != null){
			e.addElement("description").setText(description);
		}
		e.addElement("type").setText(type+"");
		e.addElement("xPos").setText(xPos + "");
		e.addElement("yPos").setText(yPos + "");
		e.addElement("width").setText(width + "");
		e.addElement("height").setText(height + "");
		return e;
	}

	public IActivity copy() {
		Comment a = new Comment();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public void decreaseNumber() {
		
	}

	@Override
	public void execute() throws Exception {
		//The comment is not a real activity it does nothing
		if(super.checkPreviousActivities()) {

			activityResult = true;
			
			super.finishActivity();
		}
	}
	
	
}
