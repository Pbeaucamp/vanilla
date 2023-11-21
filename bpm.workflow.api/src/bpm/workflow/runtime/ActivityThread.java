package bpm.workflow.runtime;

import bpm.workflow.runtime.model.IActivity;

public class ActivityThread extends Thread {

	private IActivity activity;
	
	public ActivityThread(IActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void run() {
		try {
			activity.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
