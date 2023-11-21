package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.aklabox.workflow.core.model.activities.Activity;

public class Instance implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int workflowId;
	private String instanceName;
	private int versionNumber;
	private String instanceStatus;
	private int userId;
	private String userEmail;
	private Date instanceDate = new Date();
	
	private List<ActivityLog> activityLogs = new ArrayList<ActivityLog>();
	private String modelLogs;
	
	private int directoryId = 0;
	
	
	private Workflow workflow;
	private Map<String, Integer> delegations = new HashMap<>();
	private String modelDelegations;

	public Instance() {
	}

	public Instance(int workflowId, String instanceName, int versionNumber, String instanceStatus, int userId, String userEmail, Date instanceDate) {
		super();
		this.workflowId = workflowId;
		this.instanceName = instanceName;
		this.versionNumber = versionNumber;
		this.instanceStatus = instanceStatus;
		this.userId = userId;
		this.userEmail = userEmail;
		this.instanceDate = instanceDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Date getInstanceDate() {
		return instanceDate;
	}

	public void setInstanceDate(Date instanceDate) {
		this.instanceDate = instanceDate;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setActivityLogs(List<ActivityLog> activityLogs) {
		this.activityLogs = activityLogs;
	}

	public List<ActivityLog> getActivityLogs() {
		if (activityLogs != null && !activityLogs.isEmpty() && activityLogs.size() > 1) {
			if (activityLogs.get(0).getEndDate() != null && activityLogs.get(1).getStartDate().before(activityLogs.get(0).getEndDate())) {
				List<ActivityLog> tempList = new ArrayList<ActivityLog>(activityLogs);
				Collections.reverse(tempList);
				return tempList;
			}
		}
		return activityLogs;
	}
	
	public void addActivityLog(ActivityLog log) {
		activityLogs.add(log);
	}
	
	public String getModelLogs() {
		return modelLogs;
	}

	public void setModelLogs(String modelLogs) {
		this.modelLogs = modelLogs;
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}
	


	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Map<String, Integer> getDelegations() {
		return delegations;
	}

	public void setDelegations(Map<String, Integer> delegations) {
		this.delegations = delegations;
	}
	
	public void addDelegation(String activityId, int userId){
		if(delegations == null){
			delegations = new HashMap<>();
		}
		delegations.put(activityId, userId);
	}

	public String getModelDelegations() {
		return modelDelegations;
	}

	public void setModelDelegations(String modelDelegations) {
		this.modelDelegations = modelDelegations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
