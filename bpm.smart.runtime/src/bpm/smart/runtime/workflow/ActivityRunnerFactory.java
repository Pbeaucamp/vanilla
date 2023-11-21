package bpm.smart.runtime.workflow;

import bpm.smart.core.model.workflow.activity.AirScriptActivity;
import bpm.smart.core.model.workflow.activity.ChartOutputActivity;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity;
import bpm.smart.core.model.workflow.activity.DecisionTreeActivity;
import bpm.smart.core.model.workflow.activity.FieldSelectionActivity;
import bpm.smart.core.model.workflow.activity.FileOutputActivity;
import bpm.smart.core.model.workflow.activity.FilterActivity;
import bpm.smart.core.model.workflow.activity.HAClustActivity;
import bpm.smart.core.model.workflow.activity.HeadActivity;
import bpm.smart.core.model.workflow.activity.KmeansActivity;
import bpm.smart.core.model.workflow.activity.RecodeActivity;
import bpm.smart.core.model.workflow.activity.SimpleLinearRegActivity;
import bpm.smart.core.model.workflow.activity.SortingActivity;
import bpm.smart.runtime.workflow.activity.ActivityAirScriptRunner;
import bpm.smart.runtime.workflow.activity.ActivityChartRunner;
import bpm.smart.runtime.workflow.activity.ActivityCorMatrixRunner;
import bpm.smart.runtime.workflow.activity.ActivityDecisionTreeRunner;
import bpm.smart.runtime.workflow.activity.ActivityFieldSelectionRunner;
import bpm.smart.runtime.workflow.activity.ActivityFileOutputRunner;
import bpm.smart.runtime.workflow.activity.ActivityFilterRunner;
import bpm.smart.runtime.workflow.activity.ActivityHAClustRunner;
import bpm.smart.runtime.workflow.activity.ActivityHeadRunner;
import bpm.smart.runtime.workflow.activity.ActivityKmeansRunner;
import bpm.smart.runtime.workflow.activity.ActivityRecodeRunner;
import bpm.smart.runtime.workflow.activity.ActivitySimpleLinearRegRunner;
import bpm.smart.runtime.workflow.activity.ActivitySortingRunner;
import bpm.smart.runtime.workflow.activity.ActivityStartRunner;
import bpm.smart.runtime.workflow.activity.ActivityStopRunner;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;

public class ActivityRunnerFactory {

	public static ActivityRunner<?> getActiviyRunner(Activity activity, WorkflowRunInstance instance, ResourceManager resourceManager) throws Exception {
		if (activity instanceof StartActivity) {
			return new ActivityStartRunner((StartActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof StopActivity) {
			return new ActivityStopRunner((StopActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof AirScriptActivity) {
			return new ActivityAirScriptRunner((AirScriptActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof RecodeActivity) {
			return new ActivityRecodeRunner((RecodeActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof FileOutputActivity) {
			return new ActivityFileOutputRunner((FileOutputActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof ChartOutputActivity) {
			return new ActivityChartRunner((ChartOutputActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof FieldSelectionActivity) {
			return new ActivityFieldSelectionRunner((FieldSelectionActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof HeadActivity) {
			return new ActivityHeadRunner((HeadActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof SortingActivity) {
			return new ActivitySortingRunner((SortingActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof FilterActivity) {
			return new ActivityFilterRunner((FilterActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof SimpleLinearRegActivity) {
			return new ActivitySimpleLinearRegRunner((SimpleLinearRegActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof HAClustActivity) {
			return new ActivityHAClustRunner((HAClustActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof CorMatrixActivity) {
			return new ActivityCorMatrixRunner((CorMatrixActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof DecisionTreeActivity) {
			return new ActivityDecisionTreeRunner((DecisionTreeActivity) activity, instance, resourceManager);
		}
		else if (activity instanceof KmeansActivity) {
			return new ActivityKmeansRunner((KmeansActivity) activity, instance, resourceManager);
		}
		throw new Exception("No runner found for activity " + activity.getClass().getName());
	}

}
