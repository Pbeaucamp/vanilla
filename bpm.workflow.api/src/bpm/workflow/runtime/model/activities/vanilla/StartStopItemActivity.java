package bpm.workflow.runtime.model.activities.vanilla;

import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IRepositoryItem;
import bpm.workflow.runtime.resources.BiRepositoryObject;

/**
 * This activity is used to start or stop an item on the repository
 * @author Marc Lanquetin
 *
 */
public class StartStopItemActivity extends AbstractActivity implements IRepositoryItem, IComment {

	private String comment;
	private BiRepositoryObject repositoryItem;
	private boolean start;
	private static int number = 0;
	
	public StartStopItemActivity(){number++;}
	
	public StartStopItemActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	@Override
	public IActivity copy() {
		StartStopItemActivity copy = new StartStopItemActivity();
		
		copy.setComment(comment);
		copy.setDescription(description);
		copy.setItem(repositoryItem);
		copy.setName(name);
		copy.setParent(parent);
		copy.setPositionHeight(height);
		copy.setPositionWidth(width);
		copy.setPositionX(xPos);
		copy.setPositionY(yPos);
		copy.setRelativePositionY(yRel);
		copy.setStart(start);
		
		return copy;
	}

	@Override
	public String getProblems() {

		StringBuilder buf = new StringBuilder();
		
		if(repositoryItem == null) {
			buf.append("A repository item is needed for the activity " + name + " to work.\n");
		}
		
		return buf.toString();
	}

	@Override
	public BiRepositoryObject getItem() {
		return repositoryItem;
	}

	@Override
	public void setItem(BiRepositoryObject obj) {
		repositoryItem = obj;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		this.comment = text;
	}

	/**
	 * True if the item should be started, false to stop it
	 * @param start
	 */
	public void setStart(boolean start) {
		this.start = start;
	}

	/**
	 * 
	 * @return true if the item should be started, false to stop it
	 */
	public boolean isStart() {
		return start;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		
		e.setName("startstopactivity");
		if(comment != null) {
			e.addElement("comment").setText(comment);
		}
		if(repositoryItem != null) {
			e.add(repositoryItem.getXmlNode());
		}
		
		e.addElement("start").setText(start + "");
		
		return e;
	}
	
	public void setStart(String start) {
		this.start = Boolean.parseBoolean(start);
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				int itemId = repositoryItem.getId();
				boolean isStart = start;

				RepositoryItem item = workflowInstance.getRepositoryApi().getRepositoryService().getDirectoryItem(itemId);

				boolean isOn = item.isOn();
				
				if(isOn != isStart) {
					item.setOn(isStart);
					workflowInstance.getRepositoryApi().getAdminService().update(item);
				}
				activityResult = true;
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
	}
	
}
