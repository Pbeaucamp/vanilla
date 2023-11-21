
package bpm.smart.runtime.workflow.activity;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.FileOutputActivity;
import bpm.smart.core.model.workflow.activity.FileOutputActivity.TypeOutput;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.smart.runtime.workflow.utils.CibleHelper;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;

public class ActivityFileOutputRunner extends ActivityRunner<FileOutputActivity> {
	
	public ActivityFileOutputRunner(FileOutputActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		TypeOutput type = getActivity().getTypeOutput();
		String datasetName = getActivity().getDataset(instance.getCurrentParameters(), instance.getCurrentVariables());
		Cible cible = getActivity().getCible(resourceManager.getCibles());
		String fileName = getActivity().getOutputFile(instance.getCurrentParameters(), instance.getCurrentVariables());

		String script = getScript(type, datasetName);
		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
		model.setOutputs(new String[] {"manual_result"});
		model = instance.getManager().executeScriptR(model);
		
		List<Serializable> outputs = model.getOutputVars();
		if (outputs == null || outputs.isEmpty()) {
			log.addError("No R dataset found for name " + datasetName + " with logs = " + model.getOutputLog());
			log.setResult(Result.ERROR);
//			throw new Exception("No R dataset found for name " + datasetName);
			instance.getProgress().setResult(Result.ERROR);
		}
		else {
			new CibleHelper(log, instance.getCurrentLocale(), cible, fileName, buildInputStream(outputs.get(0)), instance.getCurrentParameters(), instance.getCurrentVariables());
			if(log.getResult().equals(Result.ERROR)) {
				instance.getProgress().setResult(Result.ERROR);	
			}
		}

		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		
		if(log.getResult().equals(Result.SUCCESS)) {
//			executeChildActivities();
		}

		return log;
	}

	private ByteArrayInputStream buildInputStream(Serializable output) {
		return new ByteArrayInputStream((byte[])output);
	}

	private String getScript(TypeOutput type, String datasetName) {
		StringBuffer buf = new StringBuffer();
		switch (type) {
		case CSV:
			buf.append("filetemp <- tempfile()\n");
			buf.append("write.csv(" + datasetName + ", file = filetemp, sep = \",\")\n");
			buf.append("manual_result <- readBin(filetemp, 'raw', 1024*1024)");
			break;
		case TXT:
			buf.append("filetemp <- tempfile()\n");
			buf.append("write.table(" + datasetName + ", file = filetemp, sep = \"\\t\")\n");
			buf.append("manual_result <- readBin(filetemp, 'raw', 1024*1024)");
			break;
		case XLS:
			buf.append("filetemp <- tempfile()\n");
			buf.append("write.xlsx(" + datasetName + ", file = filetemp, sheetName=\"Sheet1\")\n");
			buf.append("manual_result <- readBin(filetemp, 'raw', 1024*1024)");
			break;

		default:
			break;
		}
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(resourceManager.getCibles());
	}
	
}
