package bpm.vanilla.server.commons.server.tasks;

import java.util.Calendar;
import java.util.Date;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.tasks.ITaskState;

public class GatewayState implements ITaskState {

	private Date creationDate;
	private Date stopDate;
	private Date startDate;
	private ActivityState state = ActivityState.WAITING;
	private ActivityResult result = ActivityResult.UNDEFINED;
	private String failureCause;
	private transient ITask task;

	public GatewayState(ITask task) {
		this.creationDate = Calendar.getInstance().getTime();
		this.task = task;
	}

	public ITask getTask() {
		return task;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Long getDuration() {
		if (getStoppedDate() == null || getStartedDate() == null) {
			return null;
		}
		return getStoppedDate().getTime() - getStartedDate().getTime();
	}

	public Long getElapsedTime() {
		if (getStartedDate() == null) {
			return null;
		}
		if (isStopped()) {
			return getDuration();
		}

		return new Date().getTime() - getStartedDate().getTime();
	}

	public String getFailingCause() {
		return failureCause;
	}

	public Date getStartedDate() {
		if (startDate == null) {
			startDate = creationDate;
		}
		return startDate;
	}

	public Date getStoppedDate() {
		return stopDate;
	}

	public ActivityResult getTaskResult() {
		return result;
	}

	public ActivityState getTaskState() {
		return state;
	}

	public boolean hasFailed() {
		return getTaskResult() == ActivityResult.FAILED;
	}

	public boolean hasSucceed() {
		return getTaskResult() == ActivityResult.SUCCEED;
	}

	public boolean isStarted() {
		return getStartedDate() != null;
	}

	public boolean isStopped() {
		return getStoppedDate() != null;
	}

	public void setStarted() {
		startDate = Calendar.getInstance().getTime();
		state = ActivityState.RUNNING;
	}

	public void setStopped() {
		stopDate = Calendar.getInstance().getTime();
		state = ActivityState.ENDED;
	}

	public void setSucceed() {
		stopDate = Calendar.getInstance().getTime();
		result = ActivityResult.SUCCEED;
	}

	public void setFailed(String failureCause) {
		stopDate = Calendar.getInstance().getTime();
		this.failureCause = failureCause;
		result = ActivityResult.FAILED;
	}

}
