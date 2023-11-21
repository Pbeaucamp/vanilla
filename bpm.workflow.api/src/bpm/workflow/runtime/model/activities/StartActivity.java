package bpm.workflow.runtime.model.activities;

import java.util.Date;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Start activity
 * @author CAMUS, MARTIN
 *
 */
public class StartActivity extends AbstractActivity {

	public StartActivity() {
		this.name = "Start";
		this.id = "Start";
		
		setPositionX(20);
		setPositionY(20);
		
		setPositionHeight(55);
		setPositionWidth(55);
		
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("startActivity");
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
		startDate = new Date();
		activityState = ActivityState.STARTED;
		activityResult = true;
		
		super.finishActivity();
	}

}
