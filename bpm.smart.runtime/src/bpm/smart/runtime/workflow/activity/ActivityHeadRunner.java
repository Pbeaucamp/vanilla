package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.HeadActivity;
import bpm.smart.core.model.workflow.activity.HeadActivity.TypeHead;
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

public class ActivityHeadRunner extends ActivityRunner<HeadActivity> {

	public ActivityHeadRunner(HeadActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
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

		if (model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		else {
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);
			
			byte[] file = getDatasetOutputAsCsv(activity.getOutputDataset(instance.getCurrentParameters(), instance.getCurrentVariables()));
			
			ActivityOutput outs = OutputFileManager.writeFile(instance, file, OutputType.CSV, "output_" + activity.getName());
			
			log.getOutputs().add(outs);
			
//			executeChildActivities();
		}

		return log;

	}

	private String getScript() throws Exception {
		HeadActivity act = getActivity();

		String datasetInput = act.getInputDataset();

		String output = act.getOutputDataset(instance.getCurrentParameters(), instance.getCurrentVariables());
		String linesNumber = act.getLinesNumber(instance.getCurrentParameters(), instance.getCurrentVariables());
		
		String type = act.getTypeHead() == TypeHead.HEAD ? "head" : "tail";

		StringBuilder buf = new StringBuilder();
		buf.append(output + "<-" + type + "(" + datasetInput + ", n = " + linesNumber + "L)\n");
		buf.append(output);

		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
