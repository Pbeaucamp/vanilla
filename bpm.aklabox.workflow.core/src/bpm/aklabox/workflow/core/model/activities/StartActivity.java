package bpm.aklabox.workflow.core.model.activities;

public class StartActivity extends Activity {

	private static final long serialVersionUID = 1L;

	public StartActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
	}

	public StartActivity() {
		this.activityId = "start";
		this.activityName = "Start";
	}

}
