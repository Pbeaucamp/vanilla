
package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.DecisionTreeActivity;
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

public class ActivityDecisionTreeRunner extends ActivityRunner<DecisionTreeActivity> {

	public ActivityDecisionTreeRunner(DecisionTreeActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		Boolean withGraph = getActivity().isWithGraph();
		
		String dataset = getActivity().getDatasetName();
		String rpartAlgo = getActivity().getRpartType().getType();
		String colx = getActivity().getxColumnName();
		List<String> ycolNames = getActivity().getyColumnNames();
		List<String> numColNames = getActivity().getNumColnames();
		
		String script = getScript(dataset, colx, ycolNames, numColNames, rpartAlgo, withGraph);
		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
//		model.setOutputs(new String[] {"manual_result"});
		model = instance.getManager().executeScriptR(model);
		
		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		if(model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		
		else {
			
			if(withGraph){
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

	private String getScript(String dataset, String colx, List<String> colsy, List<String> numCols, String rpartAlgo, boolean withGraph) {
		StringBuffer buf = new StringBuffer();
	
		String yCols = "";
		for(String col : colsy){
			yCols += dataset + "$" + col + "+";
		}
		yCols = yCols.substring(0, yCols.length()-1);
		
		String numcols ="";
		for(String col : numCols){
			numcols += "'" + col +"',";
		}
		numcols = numcols.substring(0, numcols.length()-1);
		
		buf.append("library(rpart)\n");
		buf.append("library(rpart.plot)\n");
		buf.append("model_tree<-rpart(" + dataset + "$" + colx + " ~ " + yCols + ", data = " + dataset + "[c(" + numcols + ")], method = '" + rpartAlgo + "', control=rpart.control(minsplit=10,cp=0))\n");
		buf.append("manual_result <- model_tree");
		if(withGraph){
			buf.append("\n");
//			buf.append("library(ggplot2)\n");
//			buf.append("library(ggdendro)\n");
//			buf.append("graphics.off()\n");
//			buf.append("ddata <- dendro_data(model_tree, type='rectangle')\n");
//			buf.append("graphics.off()\n");
//			buf.append("p <- ggplot()\n");
//			buf.append("p <- p + geom_segment(data = ddata$segments, aes(x = x, y = y, xend = xend, yend = yend))\n");
//			buf.append("p <- p + geom_text(data = ddata$labels, aes(x = x, y = y, label = label), size = 3, vjust = 0)\n");
//			buf.append("p <- p + geom_text(data = ddata$leaf_labels, aes(x = x, y = y, label = label), size = 3, vjust = 1)\n");
//			buf.append("p <- p + theme_dendro()\n");
//			buf.append("p");
			buf.append("prp(model_tree,extra=1,box.col=c('Lightgreen','Cyan4','Cyan')[model_tree$frame$yval],shadow.col='gray',faclen=0)");
		}	
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
