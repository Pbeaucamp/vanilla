package bpm.vanilla.platform.core.runtime.dao.massreport;

/**
 * been to store that a workflow contains a report mass generation
 * @author ludo
 *
 */
public class MassReportReference {
	private int repositoryId;
	private int workflowItemId;
	private String processInstanceId;
	private int id;
	/**
	 * @return the repositoryId
	 */
	protected int getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @param repositoryId the repositoryId to set
	 */
	protected void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	/**
	 * @return the workflowItemId
	 */
	protected int getWorkflowItemId() {
		return workflowItemId;
	}
	/**
	 * @param workflowItemId the workflowItemId to set
	 */
	protected void setWorkflowItemId(int workflowItemId) {
		this.workflowItemId = workflowItemId;
	}
	/**
	 * @return the processInstanceId
	 */
	protected String getProcessInstanceId() {
		return processInstanceId;
	}
	/**
	 * @param processInstanceId the processInstanceId to set
	 */
	protected void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	/**
	 * @return the id
	 */
	protected int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	protected void setId(int id) {
		this.id = id;
	}
	
	
	
}
