package bpm.aklabox.workflow.core.model.activities;

public class CounterActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private int numLoops = 0;

	public CounterActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, int numLoops) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.numLoops = numLoops;
	}

	public CounterActivity() {
		this.activityId = "counter";
		this.activityName = "Counter";
	}

	public int getNumLoops() {
		return numLoops;
	}

	public void setNumLoops(int numLoops) {
		this.numLoops = numLoops;
	}

}
