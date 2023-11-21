package bpm.workflow.runtime.model.activities;

import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Stop activity
 * @author CAMUS, MARTIN
 *
 */
public class StopActivity extends AbstractActivity {
	
	public StopActivity(){
		this.name = "End";
		this.id = "End";
		
		setPositionX(200);
		setPositionY(200);
		
		setPositionHeight(57);
		setPositionWidth(58);
	}	
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("stopActivity");
		return e;
	}

	public IActivity copy() {
		return null;
	}

	public String getProblems() {
		return "";
	}
	
	public void decreaseNumber() {
		
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			startDate = new Date();
			activityState = ActivityState.STARTED;
			activityResult = true;
			
			super.finishActivity();
		}
	}
}
