package bpm.workflow.runtime.communication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log of the runtime of an activity in a process
 * @author Charles MARTIN
 *
 */
public class LogBIW {
	private int id;
	private String repositoryId;
	private String itemId;
	private int runid;
	private Date runstart;
	private String processId;
	private String activityId;
	private Date startDate;
	private Date stopDate;
	private int difference;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public int getRunid() {
		return runid;
	}
	public void setRunid(int runid) {
		this.runid = runid;
	}
	public Date getRunstart() {
		return runstart;
	}
	public void setRunstart(Date runstart) {
		this.runstart = runstart;
	}

	
	
	public int getDifference() {
		return difference;
	}
	public void setDifference(int difference) {
		this.difference = difference;
	}
	public LogBIW(){
		
	}
	/**
	 * Create a log
	 * @param repository url
	 * @param repository item Id
	 * @param runing instance id
	 * @param Date of the start of the run
	 * @param process Id
	 * @param activity Id
	 * @param start Date of the activity
	 * @param stop Date of the activity
	 * @param difference time between the start and the stop
	 */
	public LogBIW(String _repositoryId, String _itemId,int _runid,Date _runstart, String _processId, String _activityId, Date _startDate, Date _stopDate,int _difference){
		
		this.repositoryId = _repositoryId;
		this.itemId = _itemId;
		this.runid = _runid;
		this.runstart = _runstart;
		this.processId = _processId;
		this.activityId = _activityId;
		this.startDate = _startDate;
		this.stopDate = _stopDate;
		this.difference = _difference;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getRepositoryId() {
		return repositoryId;
	}
	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <log>\n");
		buf.append("    	<id>" + id + "</id>\n");
		buf.append("    	<repositoryID>" + repositoryId + "</repositoryID>\n");
		buf.append("    	<directoryItem>" + itemId + "</directoryItem>\n");
		buf.append("         <runid>" + this.runid + "</runid>\n");
		buf.append("         <runstart>" + sdf.format(runstart) + "</runstart>\n");
		buf.append("    	<processID>" + processId + "</processID>\n");
		buf.append("    	<activityID>" + activityId + "</activityID>\n");
		buf.append("    	<startDate>" + sdf.format(startDate) + "</startDate>\n");
		buf.append("    	<stopDate>" + sdf.format(stopDate) + "</stopDate>\n");
		buf.append("    	<difference>" + difference + "</difference>\n");
		buf.append("    </log>\n");
		
		return buf.toString();
	}

}
