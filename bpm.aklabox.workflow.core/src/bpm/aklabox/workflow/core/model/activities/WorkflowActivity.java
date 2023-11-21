package bpm.aklabox.workflow.core.model.activities;


public class WorkflowActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private int selectedWorkflow = 0;

	//private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public WorkflowActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, int selectedWorkflow) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		
		this.setSelectedWorkflow(selectedWorkflow);
	}

	public WorkflowActivity() {
		this.activityId = "workflowActivity";
		this.activityName = "Workflow";
	}

	public int getSelectedWorkflow() {
		return selectedWorkflow;
	}

	public void setSelectedWorkflow(int selectedWorkflow) {
		this.selectedWorkflow = selectedWorkflow;
	}



}
