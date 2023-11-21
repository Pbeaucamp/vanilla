package bpm.aklabox.workflow.core.model.activities;

public class AlertSignatureActivity extends Activity {
	
	private static final long serialVersionUID = 1L;
	
	public AlertSignatureActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public AlertSignatureActivity() {
		this.activityId = "AlertSignatureActivity";
		this.activityName = "Alert signature";
	}
	
}
