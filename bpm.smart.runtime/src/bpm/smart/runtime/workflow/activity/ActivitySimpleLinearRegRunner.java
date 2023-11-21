
package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.SimpleLinearRegActivity;
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

public class ActivitySimpleLinearRegRunner extends ActivityRunner<SimpleLinearRegActivity> {

	public ActivitySimpleLinearRegRunner(SimpleLinearRegActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		Boolean graph = getActivity().isWithGraph();
		
		String dataset = getActivity().getDatasetName();
		String xcol = getActivity().getxColumnName();
		String ycol = getActivity().getyColumnName();

		String script = getScript(dataset, xcol, ycol, graph);
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
			if(graph){
				String out = model.getOutputFiles()[0];
				byte[] bytes = out.getBytes();

				ActivityOutput outs = OutputFileManager.writeFile(instance, bytes, OutputType.CHART, "output_" + activity.getName());
				
				log.getOutputs().add(outs);
			}
			
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);
//			executeChildActivities();
		}
		
		return log;
	}

	private String getScript(String dataset, String xcol, String ycol, boolean graph) {
		StringBuffer buf = new StringBuffer();
	
			buf.append("reg<-lm(" + dataset + "$" + xcol + " ~ " + dataset + "$" + ycol + ", data = " + dataset + ")\n");
			buf.append("reg\n");
			buf.append("coeff<-coefficients(reg)\n");
			buf.append("eq <- paste0('y = ', round(coeff[2],1), '*x + ', round(coeff[1],1))\n");
			buf.append("eq");
			if(graph){
				buf.append("\n");
				buf.append("library(ggplot2)\n");
				buf.append("sp <- ggplot(data=" + dataset + ", aes(x=" + dataset + "$" + xcol + ", y=" + dataset + "$" + ycol + ")) + geom_point()\n");
				buf.append("sp + stat_smooth(method='lm', se=FALSE, colour='red', size =1)+ ggtitle(eq)");
			}
			
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
