package bpm.aklabox.workflow.core.model.activities;


public class DisplayFormActivity extends Activity implements IStandardForm {

	private static final long serialVersionUID = 1L;

	private int formEngine;

	public DisplayFormActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public DisplayFormActivity() {
		this.activityId = "displayForm";
		this.activityName = "Display Form";
	}

	public int getFormEngine() {
		return formEngine;
	}

	public void setFormEngine(int formEngine) {
		this.formEngine = formEngine;
	}



}
