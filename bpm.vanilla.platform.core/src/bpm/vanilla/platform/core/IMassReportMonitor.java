package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.MassReportState;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * This interface is made to provide a monitor function when using a MassReporting operation from a workflow.
 * 
 * 
 * @author ludo
 *
 */
public interface IMassReportMonitor {
	public static final String SERVLET_MASS_REPORT_MONITOR = "/vanilla40/massReportMonitoring";
	public static enum ActionType implements IXmlActionType{
		GET_STATE(Level.DEBUG), SET_GENERATED(Level.DEBUG), SET_ASKED(Level.DEBUG), LIST(Level.DEBUG), DELETE(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	

//	/**
//	 * Store the fact that the workflow is using mass reporting on the given report
//	 * 
//	 * The workflow and the report must be coming from the same repository
//	 * otherwise an exception will be thrown
//	 * 
//	 * The MassReport action will be stored only if there is no massReport action existing
//	 * for the same association workflow/report ids.
//	 * 
//	 * This method should be called when performing a workflow deployment.
//	 * 
//	 * @param workflowItemId
//	 * @param launchedReportId
//	 * @throws Exception
//	 */
//	public void addMassReport(IObjectIdentifier workflowItemId, IObjectIdentifier launchedReportId) throws Exception;
	
	public void setReportGenerated(IObjectIdentifier workflowItemId, IObjectIdentifier launchedReportId, String workflowInstanceUuid, String activityInstanceUuid) throws Exception;
	
	public void setReportGenerationAsked(IObjectIdentifier workflowItemId, IObjectIdentifier launchedReportId, String workflowInstanceUuid, String activityInstanceUuid) throws Exception;
	
	public MassReportState getMassReportState(IObjectIdentifier workflowItemId, IObjectIdentifier reportIdentifier, String workflowInstanceUuid) throws Exception;

	public List<WorkflowRunInstance> getWorklowsUsingMassReporting() throws Exception;

	public void delete(List<WorkflowRunInstance> instances) throws Exception;
}
