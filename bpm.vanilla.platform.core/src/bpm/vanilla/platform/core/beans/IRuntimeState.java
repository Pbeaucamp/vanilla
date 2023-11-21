package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

public interface IRuntimeState extends Serializable {
	/**
	 * May be used to monitor the state of the Run, as the state of a Step
	 * within a run
	 * 
	 * @author ludo
	 * 
	 */
	public static enum ActivityState {
		INITIAL, STARTED, READY, SUSPENDED, UNKNOWN, RUNNING, WAITING, ENDED, FAILED;
	}
	
	public static enum ActivityResult {
		SUCCEED, FAILED, UNDEFINED;
	}

	public String getName();

	public ActivityState getState();

	public String getFailureCause();

	public Date getStartedDate();

	public Long getDurationTime();

	public Date getStoppedDate();
}
