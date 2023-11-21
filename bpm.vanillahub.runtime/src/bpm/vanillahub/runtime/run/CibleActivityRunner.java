package bpm.vanillahub.runtime.run;

import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.vanillahub.runtime.utils.CibleHelper;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;

public class CibleActivityRunner extends ActivityRunner<CibleActivity> {
	
	public static final String VARIABLE_PARENT_FOLDER = "{{ParentFolder}}";

	private int workflowId;
	private List<Cible> cibles;
	private CibleHelper cibleHelper;

	public CibleActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, int workflowId, String workflowName, String launcher, CibleActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Cible> cibles) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.workflowId = workflowId;
		this.cibles = cibles;
	}

	@Override
	protected void run(Locale locale) {
		Cible cible = (Cible) activity.getResource(cibles);
		String targetItem = extractTargetItem(activity.getTargetItem(parameters, variables));

		try {
			if (cibleHelper == null) {
				this.cibleHelper = new CibleHelper();
			}
			cibleHelper.manageStream(this, locale, workflowId, cible, targetItem, result, result.getFileName(), result.getInputStream(), parameters, variables, result.getInfosComp());
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}

	private String extractTargetItem(String targetItem) {
		SimpleResultInformation infos = result.getInfosComp().get(TypeResultInformation.SIMPLE) != null ? (SimpleResultInformation) result.getInfosComp().get(TypeResultInformation.SIMPLE) : null;
		return targetItem != null ? targetItem.replace("{{ParentFolder}}", infos != null ? infos.getValue() : "{{ParentFolder}}") : null;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(cibles);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(cibles);
	}

	@Override
	protected void clearResources() {
	}
}
