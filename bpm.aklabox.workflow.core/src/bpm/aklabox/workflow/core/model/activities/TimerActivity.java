package bpm.aklabox.workflow.core.model.activities;

import bpm.aklabox.workflow.core.IAklaflowConstant;

public class TimerActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private long time = 0;
	private String unit = IAklaflowConstant.SECONDS;

	public TimerActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, long time) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.time = time;
	}

	public TimerActivity() {
		this.activityId = "timer";
		this.activityName = "Timer";
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
