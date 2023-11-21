package bpm.vanillahub.runtime.run;

import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Result;

public abstract class ActivityRunner<T extends Activity> implements IRunner {

	private WorkflowProgress workflowProgress;

	protected String workflowName;
	protected String launcherName;
	protected T activity;
	protected List<Parameter> parameters;
	protected List<Variable> variables;
	protected ResultActivity result;
	protected IVanillaLogger logger;

	private boolean isLoop;
	protected boolean loopEnd;

	private Integer activityIdentifier;

	public ActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, T activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		this.workflowProgress = workflowProgress;
		this.logger = logger;
		this.workflowName = workflowName;
		this.launcherName = launcher;
		this.activity = activity;
		this.parameters = parameters;
		this.variables = variables;
		this.isLoop = isLoop;
		this.loopEnd = false;
		this.activityIdentifier = new Object().hashCode();
	}

	private void buildResult(ResultActivity parentResult) {
		this.result = new ResultActivity(workflowProgress, parentResult, activity.getName(), activityIdentifier);
	}

	@Override
	public ResultActivity runActivity(Locale locale, ResultActivity parentResult) throws Exception {
		buildResult(parentResult);

		if (activity instanceof MailActivity) {
			if (parentResult != null) {
				setResult(parentResult.getResult());
			}
			else {
				setResult(Result.SUCCESS);
			}
			run(locale);
		}
		else if (parentResult != null && parentResult.getResult() == Result.ERROR) {
			addError(Labels.getLabel(locale, Labels.AProblemOccuredPreviousBox));
			setResult(Result.ERROR);
		}
		else {
			run(locale);
		}
		
		if (result.getFileName() != null && !result.getFileName().isEmpty()) {
			initVariableFileName(result.getFileName());
			result.setVariableFileNameDefined(true);
		}
		
		return result;
	}

	private void initVariableFileName(String fileName) {
		if (variables != null) {
			for (Variable variable : variables) {
				if (variable.isFileNameVariable()) {
					variable.setRuntimeValue(fileName);
				}
			}
		}
	}

	@Override
	public void addInfo(String info) {
		if (result != null) {
			result.addInfo(info);
		}
	}

	@Override
	public void addWarning(String warning) {
		if (result != null) {
			result.addWarning(warning);
		}
	}

	@Override
	public void addDebug(String debug) {
		if (result != null) {
			result.addDebug(debug);
		}
	}

	@Override
	public void addError(String error) {
		if (result != null) {
			result.addError(error);
			result.setResult(Result.ERROR);
		}
	}

	@Override
	public void setResult(Result resultat) {
		if (result != null) {
			result.setResult(resultat);
		}
	}

	public void setNumberTotalOfFiles(int filesNumber) {
		if (result != null) {
			result.setNumberTotalOfFiles(filesNumber);
		}
	}

	protected void iterateNumberOfFileIgnored() {
		if (result != null) {
			result.iterateNumberOfFileIgnored();
		}
	}

	public int getNumberOfFileIgnored() {
		if (result != null) {
			return result.getNumberOfFileIgnored();
		}
		return 0;
	}

	protected void iterateNumberOfFileTraited(int batch) {
		if (result != null) {
			result.iterateNumberOfFileTraited(batch);
		}
	}

	public int getNumberOfFileTraited() {
		if (result != null) {
			return result.getNumberOfFileTraited();
		}
		return 0;
	}

	protected void iterateLoop() {
		if (result != null) {
			result.iterateLoop();
		}
	}
	
	public ResultActivity getResult() {
		return result;
	}

	@Override
	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}

	@Override
	public boolean isLoop() {
		return isLoop;
	}

	@Override
	public void setLoopEnd(boolean loopEnd) {
		this.loopEnd = loopEnd;
	}

	@Override
	public boolean isLoopEnd() {
		return loopEnd;
	}
	
	protected boolean isStopByUser() {
		return workflowProgress != null ? workflowProgress.isStopByUser() : false;
	}
	
	public WorkflowProgress getWorkflowProgress() {
		return workflowProgress;
	}

	protected abstract void clearResources();

	protected abstract void run(Locale locale) throws Exception;
}
