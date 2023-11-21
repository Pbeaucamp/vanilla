package bpm.smart.runtime.workflow;

import java.io.Console;
import java.util.List;
import java.util.logging.Logger;

import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;

public abstract class ActivityRunner<T extends Activity> {

	protected T activity;
	protected ActivityLog log;
	protected WorkflowRunInstance instance;
	protected ResourceManager resourceManager;

	public ActivityRunner(T activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		this.activity = activity;
		this.instance = instance;
		this.resourceManager = resourceManager;
	}

	public void startActivity() throws Exception {
		if(instance.isIncomplete()) {
			if(activity.getName().equals(instance.getStopActivityName())) {
				return;
			}
		}
		if (checkParentActivities()) {
			ActivityLog log = executeActivity();
			instance.getProgress().addActivityLog(log);
//			Logger.getLogger(this.getClass().getName()).info(log.getLogs().toString());
			if(log.getResult().equals(Result.SUCCESS)){
				executeChildActivities();
			}
			
		}
	}

	public abstract ActivityLog executeActivity() throws Exception;

	protected boolean checkParentActivities() {
		if (activity.getParentActivities() != null) {
			for (Activity parent : activity.getParentActivities()) {
				for (ActivityLog log : instance.getProgress().getActivityLogs()) {
					if (parent.getName().equals(log.getActivityName())) {
						if (log.getResult() == Result.SUCCESS) {
							break;
						}
						else {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	protected void executeChildActivities() throws Exception {
		if(activity.hasChildActivity()) {
			for (Activity child : activity.getChildActivities()) {
				ActivityRunnerFactory.getActiviyRunner(child, instance, resourceManager).startActivity();
			}
		}
	}

	public T getActivity() {
		return activity;
	}

	public void setActivity(T activity) {
		this.activity = activity;
	}

	public ActivityLog getLog() {
		return log;
	}

	public void setLog(ActivityLog log) {
		this.log = log;
	}

	public abstract List<Parameter> getParameters();
	
	public byte[] getDatasetOutputAsCsv(String datasetName) throws Exception {
		RScriptModel box = new RScriptModel();

		String script = "filetemp <- tempfile()\n" + "write.csv(" + datasetName + ", file = filetemp)\n" + "streamcsv<-readBin(filetemp,'raw',1024*1024)";
		box.setScript(script);
		box.setOutputs("streamcsv".split(" "));
		RScriptModel result = instance.getManager().executeScriptR(box);
		
		return (byte[]) result.getOutputVars().get(0);
	}

}
