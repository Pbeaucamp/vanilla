package bpm.workflow.runtime.model.activities;

import java.util.Date;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.Transition;

/**
 * Xor activity
 * @author CAMUS, CHARBONNIER, MARTIN
 *
 */
public class XorActivity extends AbstractActivity implements IComment{
	private static int number = 0;
	private String comment;
	private String type = "AND";
	
	public static String[] TYPES = {"AND", "OR", "XOR"};
	
	
	
	/**
	 * Create a XOR activity with the specified name
	 * @param name
	 */
	public XorActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}
	
	public XorActivity() {
		number++;
	}

	public IActivity copy() {
		XorActivity a = new XorActivity();
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("xorActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		e.addElement("type").setText(type);
		return e;
	}

	public String getProblems() {
		return "";
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
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			//Not call the classic finish activity.
			//Find the right path to take.
			
			if(this.getSources().size() == 1) {
				activityResult = this.getSources().get(0).getResult();
			}
			else {
				activityResult = calculateResult();
			}

			activityState = ActivityState.ENDED;
			stopDate = new Date();

			for(IActivity act : getTargets()) {
				
				Transition t = workflowInstance.getModel().getTransition(this, act);
				if(t.getCondition().getValue().equalsIgnoreCase(""+activityResult)) {
					Logger.getLogger(getClass()).debug(getName() + " executing " + act.getName() + " activity");
					act.execute();
					break;
				}
				else if(t.getCondition().getValue().equalsIgnoreCase("state")) {
					Logger.getLogger(getClass()).debug(getName() + " executing " + act.getName() + " activity");
					act.execute();
				}
			}
		}
	}

	/**
	 * Need to calculate the logical result between the sources
	 * @return
	 */
	private boolean calculateResult() {
		if(type.equals("AND")) {
			boolean result = true;
			for(IActivity act : getSources()) {
				result = result && act.getResult();
			}
			return result;
		}
		else if(type.equals("OR")) {
			boolean result = false;
			for(IActivity act : getSources()) {
				result = result || act.getResult();
			}
			return result;
		}
		else if(type.equals("XOR")) {
			
		}
		return false;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
