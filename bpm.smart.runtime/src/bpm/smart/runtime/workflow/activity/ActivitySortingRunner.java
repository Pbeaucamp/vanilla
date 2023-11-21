package bpm.smart.runtime.workflow.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.SortingActivity;
import bpm.smart.core.model.workflow.activity.SortingActivity.SortType;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.smart.runtime.workflow.utils.OutputFileManager;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.ActivityOutput.OutputType;

public class ActivitySortingRunner extends ActivityRunner<SortingActivity> {

	public ActivitySortingRunner(SortingActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
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

	private String getScript() throws Exception {
		
		StringBuilder columnIndexes = new StringBuilder();
	
		List<DataColumn> columns = instance.getManager().getDatasetColumns(activity.getInputDataset());
		
		String sortType = activity.getSortType() == SortType.DESC ? "-" : "";
		
		List<Integer> toKeep = new ArrayList<Integer>();
		for(DataColumn col : activity.getColumns()) {
			int i =1;
			for(DataColumn datasetCol : columns) {
				if(col.getColumnName().equals(datasetCol.getColumnName())) {
					toKeep.add(i);
					break;
				}
				i++;
			}
		}
		for(int i : toKeep) {
			columnIndexes.append(sortType + activity.getInputDataset() + "[" + i + "],");
		}
		
		String indexes = columnIndexes.substring(0, columnIndexes.length() - 1);
		
		StringBuilder buf = new StringBuilder();
		
		String output = activity.getOutputDataset().getString(instance.getCurrentParameters(), instance.getCurrentVariables());
		buf.append(output + "<-" + activity.getInputDataset() + "[order(" + indexes + "), ]\n");		
		buf.append(output);
		
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

}
