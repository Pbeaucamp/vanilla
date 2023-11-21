package bpm.vanilla.platform.core.components.report;

import java.util.Date;

import bpm.vanilla.platform.core.beans.IRuntimeState;

/**
 * a utility class to gather information about a GatewayRun
 * 
 * @author ludo
 * 
 */
public class ReportRuntimeState implements IRuntimeState {

	private static final long serialVersionUID = 1L;

	private ActivityState state;
	
	private int groupId;
	
	private String reportName;
	private String failureCause;
	
	private Date startedDate;
	private Date stoppedDate;
	
	public ReportRuntimeState() { }

	@Override
	public ActivityState getState() {
		return state;
	}

	public void setState(ActivityState state) {
		this.state = state;
	}

	public void setName(String reportName) {
		this.reportName = reportName;
	}

	public void setStoppedDate(Date stoppedDate) {
		this.stoppedDate = stoppedDate;
	}
	
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}
	
	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
	}

	@Override
	public Long getDurationTime() {
		if(getStoppedDate() == null) {
			return null;
		}
		
		if(getStartedDate() == null) {
			return null;
		}
		
		return getStoppedDate().getTime() - getStartedDate().getTime();
	}

	@Override
	public String getFailureCause() {
		return failureCause;
	}

	@Override
	public String getName() {
		return reportName;
	}

	@Override
	public Date getStartedDate() {
		return startedDate;
	}

	@Override
	public Date getStoppedDate() {
		return stoppedDate;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
}
