package bpm.workflow.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.ActivityThread;
import bpm.workflow.runtime.WorkflowInstance;
import bpm.workflow.runtime.model.activities.XorActivity;

/**
 * The abstract class of the activities
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public abstract class AbstractActivity extends WorkflowObject implements IActivity {

	private List<IActivity> sources = new ArrayList<IActivity>();
	private List<IActivity> targets = new ArrayList<IActivity>();
	protected ActivityState activityState = ActivityState.INITIAL;
	protected boolean activityResult = false;
	protected Date startDate;
	protected Date stopDate;
	protected WorkflowInstance workflowInstance;
	protected String failureCause;

	public void addSource(IActivity source) {
		if (!sources.contains(source)) {
			sources.add(source);
		}

	}

	public void addTarget(IActivity target) {
		if (!targets.contains(target)) {
			targets.add(target);
		}

	}

	public void deleteSource(IActivity source) {
		sources.remove(source);

	}

	public void deleteTarget(IActivity source) {
		targets.remove(source);

	}

	public List<IActivity> getSources() {
		
		if(sources != null && sources.size() > 1) {
			Collections.sort(sources, new Comparator<IActivity>() {
				@Override
				public int compare(IActivity o1, IActivity o2) {
					if(o1 instanceof XorActivity) {
						return 1;
					}
					if(o2 instanceof XorActivity) {
						return -1;
					}
					return 0;
				}
			});
		}
		
		return sources;
	}

	public List<IActivity> getTargets() {
		return targets;
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("activity");

		e.addElement("id").setText(id);
		e.addElement("name").setText(name);
		e.addElement("xPos").setText(xPos + "");
		e.addElement("yPos").setText(yPos + "");
		e.addElement("yRel").setText(yPos + "");
		e.addElement("width").setText(width + "");
		e.addElement("height").setText(height + "");
		e.addElement("description").setText(description);
		e.addElement("parent").setText(parent);
		return e;
	}

	public ActivityState getState() {
		return activityState;
	}

	public boolean getResult() {
		return activityResult;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getStopDate() {
		return stopDate;
	}

	public void finishActivity() throws Exception {

		Logger.getLogger(getClass()).debug("Finish " + getClass().getName() + " activity");

		stopDate = new Date();

		if (!(activityState == ActivityState.FAILED)) {
			activityState = ActivityState.ENDED;
		}
		else {
			boolean throwException = false;
			if (getTargets() != null && !getTargets().isEmpty()) {
				for (IActivity act : getTargets()) {
					if(!(act instanceof XorActivity)) {
						throwException = true;
						break;
					}
				}
			}
			else {
				throwException = true;
			}
			
			if(throwException) {
				throw new Exception("The workflow failed.\n An activity of the workflow have failed and there is no XOR activity to manage it.\n Reason : " + getFailureCause());
			}
		}

		if (getTargets() == null || getTargets().isEmpty()) {
			Logger.getLogger(getClass()).info("The workflow execution finished without problems.");
			return;
		}
		for (IActivity act : getTargets()) {
			ActivityThread th = new ActivityThread(act);
			th.start();
		}
	}

	@Override
	public void setInstance(WorkflowInstance workflowInstance) {
		this.workflowInstance = workflowInstance;
	}

	public boolean checkPreviousActivities() {

		if (workflowInstance.getParentThread() != null && workflowInstance.getParentThread().isInterrupted()) {
			try {
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int nbXor = 0;
		for (IActivity act : getSources()) {
			if (act instanceof XorActivity) {
				nbXor++;
			}
		}
		
		Logger.getLogger(getClass()).debug(getName() + " waiting for " + nbXor + " xor to finish");
		
		int nbXorEnded = 0;
		
		List<IActivity> xorSources = new ArrayList<>();
		
		boolean result = true;
		for (IActivity act : getSources()) {
			if (!(act.getState() == ActivityState.ENDED) && !(act.getState() == ActivityState.FAILED)) {
				// Try to see if the activity is a child because of loops
				// If it's not a child just don't run and wait
				List<String> alreadyBrowsed = new ArrayList<String>();
				if (!isChild(act, this, alreadyBrowsed)) {
					Logger.getLogger(getClass()).debug(getName() + " is child");
					result = false;
				}
			}
			if (act instanceof XorActivity) {
				for(IActivity b : act.getSources()) {
					boolean found = false;
					for(IActivity a : xorSources) {
						if(a.getName().equals(b.getName())) {
							found = true;
							break;
						}
					}
					if(!found) {
						xorSources.add(b);
					}
				}
				
				if(act.getState() == ActivityState.ENDED) {
					nbXorEnded++;
//					result = true;
				}	
			}
				
		}
		
		Logger.getLogger(getClass()).debug(getName() + " has " + nbXorEnded + " xor ended");
		Logger.getLogger(getClass()).debug(getName() + " has " + xorSources.size() + " xor sources");
		
		if(nbXorEnded >= nbXor) {
			result = true;
		}
		else {
			if(xorSources.size() == 1 && nbXorEnded >= 1) {
				return true;
			}
		}
		
		if (!result) {
			return false;
		}

		Logger.getLogger(this.getClass()).debug("Begin exection of the " + this.getClass() + " activity");
		startDate = new Date();
		activityState = ActivityState.STARTED;

		return true;
	}

	private boolean isChild(IActivity act, IActivity current, List<String> alreadyBrowsed) {
		if (!alreadyBrowsed.contains(current.getName())) {
			for (IActivity target : current.getTargets()) {
//				if (target.getId().equals("End")) {
//					return true;
//				}
				if (target.getId().equals(act.getId())) {
					return true;
				}
				alreadyBrowsed.add(current.getName());
				if (isChild(act, target, alreadyBrowsed)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getFailureCause() {
		return failureCause;
	}
}