
package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.KmeansActivity;
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

public class ActivityKmeansRunner extends ActivityRunner<KmeansActivity> {

	public ActivityKmeansRunner(KmeansActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		Boolean withGraph = getActivity().isWithGraph();
		
		String dataset = getActivity().getDatasetName();
		String algo = getActivity().getAlgoType().getType();
		String colx = getActivity().getxColumnName();
		String coly = getActivity().getyColumnName();
		
		String nb_clust = getActivity().getNbClust() + "";
		String iter_max = getActivity().getIterMax() + "";
		String n_start = getActivity().getnStart() + "";

		String script = getScript(dataset, colx, coly, algo, nb_clust, iter_max, n_start, withGraph);
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
			
			if(withGraph){
				String out = model.getOutputFiles()[0];
				//out = out.substring(out.indexOf(",") + 1);
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

	private String getScript(String dataset, String colx, String coly, String algo, String nbClust, String iterMax, String nStart, boolean withGraph) {
		StringBuffer buf = new StringBuffer();
	
		try {
			Integer.parseInt(colx);
			buf.append("col1 <- " + dataset + "[," + colx + "]\n");
		} catch(Exception e) {
			buf.append("col1 <- " + dataset + "$" + colx + "\n");
		}
		try {
			Integer.parseInt(colx);
			buf.append("col2 <- " + dataset + "[," + coly + "]\n");
		} catch(Exception e) {
			buf.append("col2 <- " + dataset + "$" + coly + "\n");
		}
		buf.append("mat_kmeans<-cbind(col1, col2)\n");
		buf.append("result <- kmeans(mat_kmeans, centers = " + nbClust + ", iter.max = " + iterMax + ", nstart = " + nStart + ", algorithm = '" + algo + "')\n");
		buf.append("manual_result <- result$cluster");
		if(withGraph){
			buf.append("\n");
			buf.append("library(ggplot2)\n");
			//buf.append("graphics.off()\n");
			buf.append("df <- " + dataset + "\n");
			buf.append("df$cluster <- factor(result$cluster)\n");
			buf.append("varcenters <- as.data.frame(result$centers)\n");
			buf.append("sizes <- as.vector(result$size)\n");
			buf.append("p <- ggplot(data=df, aes(x=" + colx + ", y=" + coly + ", color=cluster ))\n");
			buf.append("p <- p + geom_point()\n");
			buf.append("p <- p + geom_point(data=varcenters, aes(x=col1,y=col2, color='Center'))\n");
			buf.append("p <- p + geom_point(data=varcenters, aes(x=col1,y=col2, color='Center'), size=sizes, alpha=.2, show_guide=FALSE)\n");
			buf.append("p");
		}
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}
	
}
