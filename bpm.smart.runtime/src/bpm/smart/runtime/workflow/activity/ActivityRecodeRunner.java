package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.RecodeActivity;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.smart.runtime.workflow.utils.OutputFileManager;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.ActivityOutput.OutputType;

public class ActivityRecodeRunner extends ActivityRunner<RecodeActivity> {

	public ActivityRecodeRunner(RecodeActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(getActivity().getName());
		log.setStartDate(new Date());
		
		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());
		
		RScriptModel model = new RScriptModel();
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
		
		model.setScript(getScript());
		
		model = instance.getManager().executeScriptR(model);
		
		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		
		if(model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		
		else {
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);
			
			byte[] file = getDatasetOutputAsCsv(activity.getOutputDataset().getString(instance.getCurrentParameters(), instance.getCurrentVariables()));
			
			ActivityOutput outs = OutputFileManager.writeFile(instance, file, OutputType.CSV, "output_" + activity.getName());
			
			log.getOutputs().add(outs);
			
//			executeChildActivities();
		}
		
		return log;
	}

	private String getScript() {
		
		RecodeActivity act = ((RecodeActivity)activity);
		
		StringBuilder dsVals = new StringBuilder();
		StringBuilder recodeVals = new StringBuilder();
		
		boolean first = true;
		for(String value : act.getValueMapping().keySet()) {
			if(first) {
				first = false;
			}
			else {
				dsVals.append(",");
				recodeVals.append(",");
			}
			dsVals.append("'" + value + "'");
			recodeVals.append("'" + act.getValueMapping().get(value) + "'");
//			dsVals.append(value);
//			recodeVals.append(act.getValueMapping().get(value));
		}
		
		StringBuilder buf = new StringBuilder();
		
		String output = act.getOutputDataset().getString(instance.getCurrentParameters(), instance.getCurrentVariables());
		
		buf.append("library(\"plyr\")\n");
		buf.append("recode_dataset_values<-c(" + dsVals.toString() + ")\n");
		buf.append("recode_recode_values<-c(" + recodeVals.toString() + ")\n");
		buf.append("recode_column_values<-unlist(" + act.getInputDataset() + "[" + (act.getColumnIndex() + 1) + "])\n");
		buf.append("recode_map_values<-mapvalues(recode_column_values, recode_dataset_values, recode_recode_values)\n");
		buf.append( output + "<-cbind(" + act.getInputDataset() + ", 'recode_values'=recode_map_values)\n");
		buf.append(output);
		
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
