package bpm.workflow.runtime.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.model.activities.LoopGlobale;

/**
 * The pool of activities
 * @author CHARBONNIER, MARTIN
 *
 */
public class PoolModel extends WorkflowObject {
	public static final String PROCESS = "Process";
	
	private List<String> ref = new ArrayList<String>();
	private List<IActivity> activities = new ArrayList<IActivity>();
	
	public PoolModel() {
		super();
	}
	
	/**
	 * Name must be the name of the group associated to the pool
	 * @param name 
	 */
	public PoolModel(String name) {
		setName(name);
	}

	/**
	 * Add a child in the pool
	 * @param activity
	 * @throws WorkflowException
	 */
	public void addChild(IActivity activity) throws WorkflowException {
		if (activities.contains(activity)) {
			throw new WorkflowException("The activity " + activity.getName() + "is already in this pool");
		}
		this.activities.add(activity);
	}
	
	/**
	 * Remove a child from the pool
	 * @param activity
	 * @throws WorkflowException
	 */
	public void removeChild(IActivity activity) throws WorkflowException {
		if (!activities.contains(activity)) {
			throw new WorkflowException("The activity " + activity.getName() + "is not in this pool");
		}
		this.activities.remove(activity);
	}
	
	/**
	 * 
	 * @return the list of the children of the pool
	 */
	public List<IActivity> getChildrens() {
		return activities;
	}
	
	/**
	 * 
	 * @param activity
	 * @return true if the activity is in the pool
	 */
	public boolean contains(IActivity activity) {
		return activities.contains(activity);
	}
	
	/**
	 * Only for digester
	 * @param id
	 */
	public void addReference(String id) {
//		ref.add(id);
	}
	
	/**
	 * Only for digester
	 * @return
	 */
	public List<String> getReferences() {
		return ref;
	}
	
	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("pool");
		e.addElement("name").setText(name);
		e.addElement("xPos").setText(xPos + "");
		e.addElement("yPos").setText(yPos + "");
		e.addElement("width").setText(width + "");
		e.addElement("height").setText(height + "");
		e.addElement("desciption").setText(description);
		
//		Element act = e.addElement("activities");
//		for (IActivity a : activities) {
//			
//			act.addElement("activityid").setText(a.getId());
//			
//		}
		
		
		return e;
	}

	

}
