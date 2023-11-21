package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.workflow.commons.beans.Log.Level;

public class ActivityLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String activityName;

	private Result result;

	private Date startDate;
	private Date endDate;

	private int loop = 0;
	
	private int numberTotalOfFiles = 0;
	private int numberOfFileIgnored = 0;
	private int numberOfFileTraited = 0;

	private List<Log> logs = new ArrayList<Log>();
	
	private List<ActivityOutput> outputs = new ArrayList<ActivityOutput>();
	
	private String scriptR; 

	public ActivityLog() {
	}

	public ActivityLog(String activityName) {
		this.activityName = activityName;
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
	
	public void setNumberTotalOfFiles(int numberTotalOfFiles) {
		this.numberTotalOfFiles = numberTotalOfFiles;
	}
	
	public int getNumberTotalOfFiles() {
		return numberTotalOfFiles;
	}

	public void iterateNumberOfFileIgnored() {
		numberOfFileIgnored++;
	}

	public int getNumberOfFileIgnored() {
		return numberOfFileIgnored;
	}

	public void iterateNumberOfFileTraited(int batch) {
		numberOfFileTraited = numberOfFileTraited + batch;
	}

	public int getNumberOfFileTraited() {
		return numberOfFileTraited;
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
		}
		else {
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
		}
		else {
			buf.append("\n");
		}

		return buf.toString();
	}

	public List<ActivityOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<ActivityOutput> outputs) {
		this.outputs = outputs;
	}

	public String getScriptR() {
		return scriptR;
	}

	public void setScriptR(String scriptR) {
		this.scriptR = scriptR;
	}
	
	
}
