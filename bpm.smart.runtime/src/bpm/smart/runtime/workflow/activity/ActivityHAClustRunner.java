
package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.HAClustActivity;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.smart.runtime.workflow.utils.OutputFileManager;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.ActivityOutput.OutputType;
import bpm.workflow.commons.beans.Result;

public class ActivityHAClustRunner extends ActivityRunner<HAClustActivity> {

	public ActivityHAClustRunner(HAClustActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		Boolean rotate = getActivity().isRotate();
		
		String dataset = getActivity().getDatasetName();
		String dist = getActivity().getDistType().getType();
		String clust = getActivity().getClustType().getType();

		String script = getScript(dataset, dist, clust, rotate);
		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
		model.setOutputs(new String[] {"manual_result"});
		model = instance.getManager().executeScriptR(model);
		
		
		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		if(model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		
		else {
			
			String out = model.getOutputFiles()[0];
			byte[] bytes = out.getBytes();
			
			ActivityOutput outs = OutputFileManager.writeFile(instance, bytes, OutputType.CHART, "output_" + activity.getName());
			
			log.getOutputs().add(outs);
			
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);
//			executeChildActivities();
		}
		
		return log;
	}

	private String getScript(String dataset, String dist, String clust, boolean rotate) {
		StringBuffer buf = new StringBuffer();
	
			buf.append("library(ggplot2)\n");
			buf.append("library(ggdendro)\n");
			buf.append("hc <- hclust(dist(" + dataset + ", '" + dist + "'), '" + clust + "')\n");
			buf.append("ggdendrogram(hc, rotate = " + (rotate ? "TRUE" : "FALSE") + ")");
			
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
