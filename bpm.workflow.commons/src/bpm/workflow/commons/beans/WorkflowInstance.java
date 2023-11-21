package bpm.workflow.commons.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.workflow.commons.beans.Log.Level;

@Entity
@Table(name = "workflow_instance")
public class WorkflowInstance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "workflowId")
	private int workflowId;

	@Column(name = "result")
	private Result result;

	@Column(name = "startDate")
	private Date startDate;
	@Column(name = "endDate")
	private Date endDate;

	@Column(name = "stopByUser")
	private boolean stopByUser;
	@Column(name = "isFinish")
	private boolean isFinish;

	@Column(name = "workflowNumber")
	private int workflowNumber;
	@Column(name = "totalWorkflow")
	private int totalWorkflow;

	@Transient
	private List<ActivityLog> activityLogs = new ArrayList<ActivityLog>();
	@Column(name = "modelLogs", length = 10000000)
	private String modelLogs;

	@Transient
	private String workflowName;

	@Column(name = "uuid")
	private String uuid;

	public WorkflowInstance() {
		this.startDate = new Date();
	}

	public WorkflowInstance(String uuid, Workflow workflow) {
		this.uuid = uuid;
		setWorkflow(workflow.getId(), workflow.getName());
		this.startDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Result getResult() {
		return result;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getDuration() {
		if (startDate == null || endDate == null) {
			return -1;
		}
		return endDate.getTime() - startDate.getTime();
	}

	public void setStopByUser(boolean stopByUser) {
		this.stopByUser = stopByUser;
	}

	public boolean isStopByUser() {
		return stopByUser;
	}

	public boolean isFinish() {
		if (result != null && result != Result.RUNNING) {
			return true;
		}
		for (ActivityLog log : activityLogs) {
			if (log.getActivityName().equals("Stop")) {
				if (log.getEndDate() != null) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public void setActivityLogs(List<ActivityLog> activityLogs) {
		this.activityLogs = activityLogs;
	}

	public List<ActivityLog> getActivityLogs() {
		if (activityLogs != null && !activityLogs.isEmpty() && activityLogs.size() > 1) {
			if (activityLogs.get(0).getEndDate() != null && activityLogs.get(1).getStartDate().before(activityLogs.get(0).getEndDate())) {
				List<ActivityLog> tempList = (List<ActivityLog>) ((ArrayList<ActivityLog>) activityLogs).clone();
				Collections.reverse(tempList);
				return tempList;
			}
		}
		return activityLogs;
	}

	public void setWorkflowNumber(int workflowNumber) {
		this.workflowNumber = workflowNumber;
	}

	public int getWorkflowNumber() {
		return workflowNumber;
	}

	public void setTotalWorkflow(int totalWorkflow) {
		this.totalWorkflow = totalWorkflow;
	}

	public int getTotalWorkflow() {
		return totalWorkflow;
	}

	public String getLogs(Level level) {
		StringBuffer buf = new StringBuffer();
		if (getActivityLogs() != null) {
			for (ActivityLog log : getActivityLogs()) {
				buf.append(log.toString(level));
			}
		}
		return buf.toString();
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
		return (hours != 0 ? hours + "h" : "") + (minutes != 0 ? minutes + "m" : "") + seconds + "s " + milliseconds;
	}

	public String getModelLogs() {
		return modelLogs;
	}

	public void setModelLogs(String modelLogs) {
		this.modelLogs = modelLogs;
	}

	public void setWorkflow(int workflowId, String workflowName) {
		this.workflowId = workflowId;
		this.workflowName = workflowName;
	}
	
	public String getWorkflowName() {
		return workflowName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void addActivityLog(ActivityLog log) {
		activityLogs.add(log);
	}

	public List<ActivityOutput> getOutputs() {
		List<ActivityOutput> outs = new ArrayList<ActivityOutput>();

		for (ActivityLog log : activityLogs) {
			outs.addAll(log.getOutputs());
		}

		return outs;
	}

	public int getWorkflowId() {
		return workflowId;
	}

}
