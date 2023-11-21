package bpm.vanilla.platform.core.components;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.xstream.IXmlActionType;


public interface WorkflowService extends IVanillaServerManager {
	
	public static enum ActionType implements IXmlActionType{
		START(Level.INFO), INFO(Level.DEBUG),LIST_FORMS(Level.DEBUG), START_ASYNC(Level.INFO), GET_PREVIOUS_TASK(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public static final String HIDDEN_BONITA_PROCESS_INSTANCE_UUID = "bpm.vanilla.platform.core.components.workflow.hidden.bonitaProcessInstanceUUID";
	public static final String HIDDEN_BONITA_ACTIVITY_INSTANCE_UUID = "bpm.vanilla.platform.core.components.workflow.hidden.bonitaActivityInstanceUUID";

	public static String EVENT_NOTIFICATION_SERVLET = "/workflow/component/eventNotification";
	public static final String SERVLET_RUNTIME = "/workflow/runtime";
	public static final String WORKFLOW_JOB_MONITOR_SERVLET = "/workflow/monitor";
	public static final String P_WORKFLOW_RUN_IDENTIFIER = "workflowProcessVanillaUUID";
	public static final String P_WORKFLOW_MONITOR_INCLUDE_TITLE = "hasTitle";
	public static final String LIST_FORMS = "bpm.vanilla.platform.core.components.actionListForms";
	public static final String SERVLET_TASK_SUBMISSION = "/workflow/submitTask";
	
	/**
	 * this folder is relative to the specified P_VANILLA_FILES folder
	 */
	public static final String WORKFLOW_MASS_HISTORIZATION_FOLDER = "/reportMassGeneration";
	
	public IRunIdentifier startWorkflow(IRuntimeConfig config) throws Exception;
	
	public IRunIdentifier startWorkflowAsync(IRuntimeConfig config) throws Exception;
	
	/**
	 * return the processComplete state(state on each of ots activities)
	 * @param runIdentifier
	 * @return
	 * @throws Exception
	 */
	public WorkflowInstanceState getInfos(IRunIdentifier runIdentifier, int itemId, int repositoryId) throws Exception;
}
