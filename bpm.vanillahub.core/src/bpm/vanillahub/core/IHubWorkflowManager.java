package bpm.vanillahub.core;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.workflow.commons.beans.IWorkflowManager;

public interface IHubWorkflowManager extends IWorkflowManager {
	
	public static final String VANILLA_HUB_SERVLET = "/hubWorkflowServlet";
	
	public static enum ActionType implements IXmlActionType{
		MANAGE_WORKFLOW, REMOVE_WORKFLOW, GET_WORKFLOWS, GET_WORKFLOWS_WITH_PAG, GET_WORKFLOW, GET_PARAMETERS, INIT_WORKFLOW,
		RUN_WORKFLOW, GET_WORKFLOW_RUNS, GET_WORKFLOW_RUN, GET_WORKFLOW_PROGRESS, STOP_WORKFLOW, DUPLICATE_WORKFLOW;

		@Override
		public Level getLevel() {
			return null;
		}
	}
	
}
