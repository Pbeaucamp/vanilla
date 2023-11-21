package bpm.aklabox.workflow.core.model.activities;

public class SubWorkflowActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private String selectedCheckBox;
	private int subWorkflowId;
	
	public SubWorkflowActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public SubWorkflowActivity() {
		this.activityId = "Sub Workflow";
		this.activityName = "Sub Workflow";
	}

	public String getSelectedCheckBox() {
		return selectedCheckBox;
	}

	public void setSelectedCheckBox(String selectedCheckBox) {
		this.selectedCheckBox = selectedCheckBox;
	}

	public int getSubWorkflowId() {
		return subWorkflowId;
	}

	public void setSubWorkflowId(int subWorkflowId) {
		this.subWorkflowId = subWorkflowId;
	}

}
