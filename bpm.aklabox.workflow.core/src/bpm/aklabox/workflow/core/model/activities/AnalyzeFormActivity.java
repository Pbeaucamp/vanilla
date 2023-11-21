package bpm.aklabox.workflow.core.model.activities;



public class AnalyzeFormActivity extends Activity implements IVariable, IStandardForm {

	private static final long serialVersionUID = 1L;

	private int variableResource;
	private int formEngine;

	public AnalyzeFormActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public AnalyzeFormActivity() {
		this.activityId = "analyzeForm";
		this.activityName = "Analyze Form";
	}

	public int getVariableResource() {
		return variableResource;
	}

	public void setVariableResource(int variableResource) {
		this.variableResource = variableResource;
	}

	public int getFormEngine() {
		return formEngine;
	}

	public void setFormEngine(int formEngine) {
		this.formEngine = formEngine;
	}



}
