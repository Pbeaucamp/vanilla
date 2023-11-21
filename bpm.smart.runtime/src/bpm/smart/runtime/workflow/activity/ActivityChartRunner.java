package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.ChartOutputActivity;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.smart.runtime.workflow.utils.OutputFileManager;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.ActivityOutput.OutputType;
import bpm.workflow.commons.beans.Result;

public class ActivityChartRunner extends ActivityRunner<ChartOutputActivity> {

	public ActivityChartRunner(ChartOutputActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());
		
		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());
		
		RScriptModel model = executeScript();
//		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
//		
//		model = instance.getManager().executeScriptR(model);
		
		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		
		if(model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		
		else {
			String out = model.getOutputFiles()[0];
			byte[] bytes = out.getBytes();
			
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);

			ActivityOutput outs = OutputFileManager.writeFile(instance, bytes, OutputType.CHART, "output_" + activity.getName());
			
			log.getOutputs().add(outs);
			
//			executeChildActivities();
		}
		
		return log;
	}

	private RScriptModel executeScript() throws Exception {
		
		Dataset dataset = new Dataset();
		dataset.setName(activity.getDatasetName());
		
		DataColumn col1 = new DataColumn();
		col1.setColumnName(activity.getxColumnName());
		
		DataColumn col2 = new DataColumn();
		col2.setColumnName(activity.getyColumnName());
		
		//String userREnv = instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId();
		
		return instance.getManager().generateStatRPlot(col1, col2, dataset);
	}
	
	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

}
