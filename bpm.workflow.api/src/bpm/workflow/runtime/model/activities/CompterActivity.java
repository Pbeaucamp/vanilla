package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IConditionnable;

public class CompterActivity extends AbstractActivity implements IConditionnable {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String nbLoop="";
	private static int number = 0;
	
/**
 * 
 * @return the number of maximum allowed loops
 */
	public String getnbLoop() {
		return nbLoop;
		
	}

	/**
	 * Set the number of maximum allowed loops
	 * @param nbLoop
	 */
	public void setnbLoop(String nbLoop) {
		this.nbLoop = nbLoop;
	}


	public CompterActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(2);
		number++;
	}
	
	public CompterActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(2);
	}
	
	public String getSuccessVariableSuffix() {
		return "_pathResult";
	}

	public IActivity copy() {
		CompterActivity a = new CompterActivity();
		a.setName("copy of " + name);
		return a;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (nbLoop.equalsIgnoreCase("")) {
			buf.append("For activity " + name + ", the number of loops is not set.\n");
		}
		return buf.toString();
	}
	
	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		
		
		return listeVar;
	}
	
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("compterActivity");
		
		if(!this.getnbLoop().equalsIgnoreCase("")){
		e.addElement("nbLoop").setText(this.getnbLoop());
		}
		
		return e;
	}
	
	
	public void decreaseNumber() {
		number--;
	}

	private int currentIteration = 0;
	
	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try {
				if(Integer.parseInt(nbLoop) > currentIteration) {
					activityResult = false;
					currentIteration++;
				}
				else {
					activityResult = true;
				}
			} catch(Exception e) {
				activityResult = true;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
		
	}
}
