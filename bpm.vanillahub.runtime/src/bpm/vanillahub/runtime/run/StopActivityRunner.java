package bpm.vanillahub.runtime.run;

import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.activity.StopActivity;

public class StopActivityRunner extends ActivityRunner<StopActivity> {

	public StopActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, StopActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
	}

	@Override
	public void run(Locale locale) {
		addInfo(Labels.getLabel(locale, Labels.StopWorkflow));
		setResult(Result.SUCCESS);
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void clearResources() { }
}
