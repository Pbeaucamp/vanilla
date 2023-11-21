package bpm.vanilla.platform.core.beans;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;

public class MassReportState {
	private int workflowId;
	private int reportId;
	private int repositoryId;
	
	private long reportTaskNumber;
	private long reportGenerationNumber;

	
	
	public MassReportState(int workflowId, int reportId, int repositoryId) {
		super();
		this.workflowId = workflowId;
		this.reportId = reportId;
		this.repositoryId = repositoryId;
	}
	/**
	 * @return the reportTaskNumber
	 */
	public long getReportTaskNumber() {
		return reportTaskNumber;
	}
	/**
	 * @param reportTaskNumber the reportTaskNumber to set
	 */
	public void setReportTaskNumber(long reportTaskNumber) {
		this.reportTaskNumber = reportTaskNumber;
	}
	/**
	 * @return the reportGenerationNumber
	 */
	public long getReportGenerationNumber() {
		return reportGenerationNumber;
	}
	/**
	 * @param reportGenerationNumber the reportGenerationNumber to set
	 */
	public void setReportGenerationNumber(long reportGenerationNumber) {
		this.reportGenerationNumber = reportGenerationNumber;
	}
	
	
	public IObjectIdentifier getWorkflowIdentifier(){
		return new ObjectIdentifier(repositoryId, workflowId);
	}
	public IObjectIdentifier getReportIdentifier(){
		return new ObjectIdentifier(repositoryId, reportId);
	}
}
