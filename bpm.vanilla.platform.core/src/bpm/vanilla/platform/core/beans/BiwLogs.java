package bpm.vanilla.platform.core.beans;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BiwLogs {

	private int id;
	private String repositoryID;
	private String directoryItem;
	private int runid;
	private Date runstart;
	private String processID;
	private String activityID;
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
	public void setRunidString(String runid) {
		this.runid = Integer.parseInt(runid);
	}
	public Date getRunstart() {
		return runstart;
	}
	public void setRunstart(Date runstart) {
		this.runstart = runstart;
	}

	public String getRepositoryID() {
		return repositoryID;
	}
	public void setRepositoryID(String repositoryID) {
		this.repositoryID = repositoryID;
	}
	public String getDirectoryItem() {
		return directoryItem;
	}
	public void setDirectoryItem(String directoryItem) {
		this.directoryItem = directoryItem;
	}
	public String getProcessID() {
		return processID;
	}
	public void setProcessID(String processID) {
		this.processID = processID;
	}
	public String getActivityID() {
		return activityID;
	}
	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDateString(String startDate) {		
		try {
			this.startDate = sdf.parse(startDate);
		} catch (ParseException e) {
			
//			e.printStackTrace();
		};
	}
	
	public void setStartDate(Date startDate) {
			this.startDate = startDate;	
	}
	
	public int getDifference() {
		return difference;
	}
	public void setDifference(String difference) {
		this.difference = Integer.parseInt(difference);
	}
	
	public void setDifference(int into){
		this.difference = into;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDateString(String stopDate) {
		try {
			this.stopDate = sdf.parse(stopDate);
		} catch (ParseException e) {
			
//			e.printStackTrace();
		}
	}
	
	public void setRunstartString(String runDate) {
		try {
			this.runstart = sdf.parse(runDate);
		} catch (ParseException e) {
			
//			e.printStackTrace();
		}
	}
	

	public void setStopDate(Date _stopDate) {
			this.stopDate = _stopDate;
	}

	
	
	
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id){
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <log>\n");
		buf.append("         <id>" +  this.id + "</id>\n");
		buf.append("         <repositoryID>" + this.repositoryID + "</repositoryID>\n");
		buf.append("         <directoryItem>" + this.directoryItem + "</directoryItem>\n");
		buf.append("         <runid>" + this.runid + "</runid>\n");
		buf.append("         <runstart>" + this.runstart + "</runstart>\n");
		buf.append("         <processID>" + this.processID + "</processID>\n");
		buf.append("         <activityID>" + this.activityID + "</activityID>\n");
		buf.append("         <startDate>" + this.startDate + "</startDate>\n");
		buf.append("         <stopDate>" + this.stopDate + "</stopDate>\n");
		buf.append("         <difference>" + this.difference + "</difference>\n");
		buf.append("    </log>\n");
		
		return buf.toString();
	}
	
	
	
}
