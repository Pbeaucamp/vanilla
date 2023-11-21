package bpm.workflow.runtime.model.activities;

import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;

/**
 * Wait for a period in the process
 * @author Charles MARTIN
 *
 */
public class TimerActivity extends AbstractActivity {
	private static int number = 0;
	
	private String timeH="";
	

	/**
	 * 
	 * @return the time to wait
	 */
	public String getTimeH() {
		return timeH;
	}

	/**
	 * Set the time to wait
	 * @param timeH
	 */
	public void setTimeH(String timeH) {
		this.timeH = timeH;
	}

	/**
	 * do not use, for XML parsing only
	 */
	public TimerActivity(){
		number++;
	}
	
	public TimerActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	public IActivity copy() {
		TimerActivity a = new TimerActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("timerActivity");
		if(!this.getTimeH().equalsIgnoreCase("")){
		e.addElement("timeH").setText(this.getTimeH());
		}
		return e;
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				Thread.sleep(Integer.parseInt(timeH));

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
