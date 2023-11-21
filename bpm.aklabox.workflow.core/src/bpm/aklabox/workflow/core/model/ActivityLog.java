package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.aklabox.workflow.core.model.Log.Level;
import bpm.aklabox.workflow.core.model.activities.IActivity;
import bpm.document.management.core.model.User;

public class ActivityLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String activityName;

	private Result result;

	private Date startDate;
	private Date endDate;

	private int loop = 0;

	private String activitySourceName;
	private String assignedUser;

	private List<Log> logs = new ArrayList<Log>();

	private IActivity activity;

	private String activityId;
	
	private User executor;
	
	private boolean status = true;

	public ActivityLog() {
		super();
	}

	public ActivityLog(IActivity activity) {
		this.activityName = activity.getActivityName();
		this.activity = activity;
		this.activityId = activity.getActivityId();
	}

	public String getActivityName() {
		return activityName;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void iterateLoop() {
		loop++;
	}

	public int getLoop() {
		return loop;
	}

	public long getDuration() {
		if (startDate == null || endDate == null) {
			return -1;
		}
		return endDate.getTime() - startDate.getTime();
	}

	public String getDurationAsString() {
		long duration = getDuration();
		if (duration == -1) {
			return "";
		}

		int milliseconds = (int) duration % 1000;
		int seconds = (int) (duration / 1000) % 60;
		int minutes = (int) ((duration / (1000 * 60)) % 60);
		int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
		return (hours != 0 ? hours + ":" : "") + (minutes != 0 ? minutes + ":" : "") + seconds + "." + milliseconds;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public void addInfo(String message) {
		this.logs.add(new Log(Level.INFO, message));
	}

	public void addWarning(String message) {
		this.logs.add(new Log(Level.WARNING, message));
	}

	public void addError(String message) {
		this.logs.add(new Log(Level.ERROR, message));
	}

	public void addDebug(String message) {
		this.logs.add(new Log(Level.DEBUG, message));
	}

	public String toString(Level level) {
		StringBuffer buf = new StringBuffer();

		if (level == Level.ALL || level == Level.INFO) {
			buf.append("INFO - '" + activityName + "' - Start : " + startDate + "\n");
		} else {
			buf.append("Activité - '" + activityName + "'\n");
		}

		boolean hasLogs = false;
		if (logs != null && !logs.isEmpty()) {
			for (Log log : logs) {
				if (level == Level.ALL || level == log.getLevel()) {
					hasLogs = true;
					buf.append(log.toString() + "\n");
				}
			}
		}

		if (!hasLogs) {
			buf.append("Il n'y a pas de log disponible pour le niveau " + level + "\n");
		}

		if (level == Level.ALL || level == Level.INFO) {
			buf.append("INFO - '" + activityName + "' - End : " + endDate + "\n");
		} else {
			buf.append("\n");
		}

		return buf.toString();
	}

	public String getActivitySourceName() {
		return activitySourceName;
	}

	public void setActivitySourceName(String activitySourceName) {
		this.activitySourceName = activitySourceName;
	}

	public String getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(String assignedUser) {
		this.assignedUser = assignedUser;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public IActivity getActivity() {
		return activity;
	}

	public void setActivity(IActivity activity) {
		this.activity = activity;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public User getExecutor() {
		return executor;
	}

	public void setExecutor(User executor) {
		this.executor = executor;
	}

}
