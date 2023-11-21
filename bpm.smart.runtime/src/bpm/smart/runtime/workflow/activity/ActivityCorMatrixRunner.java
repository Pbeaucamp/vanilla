
package bpm.smart.runtime.workflow.activity;

import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity;
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

public class ActivityCorMatrixRunner extends ActivityRunner<CorMatrixActivity> {

	public ActivityCorMatrixRunner(CorMatrixActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());

		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());

		Boolean withGraph = getActivity().isWithGraph();
		
		String dataset = getActivity().getDatasetName();
		String corAlgo = getActivity().getCorType().getType();
		List<String> colNames = getActivity().getColnames();

		String script = getScript(dataset, colNames, corAlgo, withGraph);
		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
		model.setOutputs(new String[] {"manual_mat_cor"});
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

	private String getScript(String dataset, List<String> colNames, String corAlgo, boolean withGraph) {
		StringBuffer buf = new StringBuffer();
		  
		String numCols ="";
		for(String col : colNames){
			numCols += "'" + col +"',";
		}
		numCols = numCols.substring(0, numCols.length()-1);
		
		buf.append("manual_mat_cor <- round(cor(" + dataset + "[c(" + numCols + ")]),2)\n");
		buf.append("dd <- as.dist((1-manual_mat_cor)/2)\n");
		buf.append("hc <- hclust(dd)\n");
		buf.append("mat_cor<-manual_mat_cor[hc$order, hc$order]\n");
		buf.append("mat_cor2 <- mat_cor\n");
		buf.append("mat_cor2[lower.tri(mat_cor)] <- NA");
		if(withGraph){
			buf.append("\n");
			buf.append("library(ggplot2)\n");
			buf.append("library(reshape2)\n");
			buf.append("melted_cormat <- melt(mat_cor2)\n");
			buf.append("melted_cormat <- na.omit(melted_cormat)\n");
			buf.append("p <- ggplot(data = melted_cormat, aes(Var2, Var1, fill = value))\n");
			buf.append("p <- p + geom_tile(color = 'white')\n");
			buf.append("p <- p + scale_fill_gradient2(low ='dark red', high = 'dark green', mid = 'yellow', midpoint = 0, limit = c(-1,1), name='Gradient')\n");
			buf.append("p <- p + theme_minimal()\n");
			buf.append("p <- p + theme(axis.text.x = element_text(angle = 45, vjust = 1, size = 12, hjust = 1))\n");
			buf.append("p <- p + coord_fixed()\n");
			buf.append("p <- p + geom_text(aes(Var2, Var1, label = value), color = 'black', size = 4)\n");
			buf.append("p <- p + theme(axis.title.x = element_blank(), axis.title.y = element_blank(), panel.grid.major = element_blank(), panel.border = element_blank(), panel.background = element_blank(), axis.ticks = element_blank(), legend.justification = c(1, 0), legend.position = c(0.5, 0.8), legend.direction = 'horizontal')\n");
			buf.append("p <- p + guides(fill = guide_colorbar(barwidth = 7, barheight = 1,  title.position = 'top', title.hjust = 0.5))\n");
			buf.append("p");
		}
		return buf.toString();
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

}
