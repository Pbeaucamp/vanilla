package bpm.workflow.runtime.model;

import java.util.Date;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.WorkflowInstance;

/**
 * Interface of the activities
 * @author CHARBONNIER,MARTIN
 *
 */
public interface IActivity {
	/**
	 * 
	 * @return the name of the activity
	 */
	public String getId();
	/**
	 * 
	 * @return the name of the activity
	 */
	public String getName();
	/**
	 * Set the name of the activity
	 * @param name
	 */
	public void setName(String name);

	

	
	//for display purpose
	
	/**
	 * @return the x position of the activity
	 */
	public int getPositionX();
	/**
	 * 
	 * @return the y position of the activity
	 */
	public int getPositionY();
	/**
	 * 
	 * @return relative y position of the activity
	 */
	public int getRelativePositionY();
	/**
	 * 
	 * @return width of the activity
	 */
	public int getPositionWidth();
	/**
	 * 
	 * @return the height of the activity
	 */
	public int getPositionHeight();
	
	/**
	 * Set the x position of the activity
	 * @param x
	 */
	public void setPositionX(int x);
	/**
	 * Set the y position of the activity
	 * @param y
	 */
	public void setPositionY(int y);
	public void setRelativePositionY(int y);
	public void setPositionWidth(int width);
	public void setPositionHeight(int height);
	
	/**
	 * 
	 * @return the sources of the activity
	 */
	public List<IActivity> getSources();
	/**
	 * Add a source 
	 * @param source
	 */
	public void addSource(IActivity source);
	/**
	 * Delete a source
	 * @param source
	 */
	public void deleteSource(IActivity source);
	
	/**
	 * 
	 * @return the targets of the activity
	 */
	public List<IActivity> getTargets();
	/**
	 * Add a target 
	 * @param the target
	 */
	public void addTarget(IActivity source);
	/**
	 * Delete a target
	 * @param the target to delete
	 */
	public void deleteTarget(IActivity source);
	public Element getXmlNode();
	
	public IActivity copy();
	
	public String getProblems();
	
	/**
	 * 
	 * @return the container of this activity
	 */
	public String getParent();
	/**
	 * Set the container of this activity
	 * @param parent
	 */
	public void setParent(String parent);
	
	/**
	 * used to determined if the activity is running, finished, etc...
	 * @return the state of the activity
	 */
	public ActivityState getState();
	
	/**
	 * When implementing this, you need to : 
	 *  - set the startDate at the beginning
	 *  - set the activity result at the end
	 *  - set the state whenever you want
	 *  - set the stopDate at the end
	 * @throws Exception
	 */
	public void execute() throws Exception;
	
	/**
	 * Return true if the activity is a success.
	 * If the activity as no result elements, return true when the activity finishes.
	 * @return the result of the activity
	 */
	public boolean getResult();
	
	public Date getStartDate();
	
	public Date getStopDate();
	
	public void setInstance(WorkflowInstance workflowInstance);
	
	public String getFailureCause();
}

