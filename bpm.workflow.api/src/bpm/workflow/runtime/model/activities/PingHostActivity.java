package bpm.workflow.runtime.model.activities;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;

/**
 * Ping a Host thanks to its IP address
 * @author Charles MARTIN
 *
 */
public class PingHostActivity extends AbstractActivity implements IConditionnable,IComment{

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String path;
	private String comment;
	private String varRefName;
	private static int number = 0;
	
	
	/**
	 * do not use, only for xml
	 */
	public PingHostActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
		number++;
	}	
	
	public PingHostActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
	}
	
	public String getSuccessVariableSuffix() {
		return "_pathResult";
	}

	public PingHostActivity copy() {
		PingHostActivity a = new PingHostActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		if (varRefName != null)
			a.setVariable(varRefName);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("pingHostActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (path != null){
		e.addElement("path").setText(this.path);
		}
		if (varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (path == null) {
			buf.append("For activity " + name + ", the host IP is not set.\n");
		}
		
		
		return buf.toString();
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		
		return listeVar;
	}
	
	public String getVariable() {
		return varRefName;
	}



	public void setVariable(String varRefName) {
		this.varRefName = varRefName;
	}
	
	/**
	 * Set the IP to ping
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;
		
	}
	/**
	 * 
	 * @return the IP to ping
	 */
	public String getPath(){
		return this.path;
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
			if(path.contains("{$")){
				path = workflowInstance.parseString(path);
			}
	
	
			try{
				int timeOut = 3000; 
				boolean status = InetAddress.getByName(path).isReachable(timeOut);
	
				Thread.sleep(4000);
				if(status){
					activityResult = true;
				}
				else{
					activityResult = false;
				}
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("ping at " + path + " failed - " + ex.getMessage());
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = ex.getMessage();
			}
			
			
			super.finishActivity();
		}
	}
	
	

}
