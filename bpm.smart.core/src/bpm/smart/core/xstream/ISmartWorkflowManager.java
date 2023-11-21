package bpm.smart.core.xstream;

import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.workflow.commons.beans.IWorkflowManager;

public interface ISmartWorkflowManager extends IWorkflowManager, IResourceManager {
	
	public static final String SMART_WORKFLOW_MANAGER_URL = "/airWorkflowManager";
	
	public static enum ActionType implements IXmlActionType {
		GET_WORKFLOWS(Level.DEBUG), GET_WORKFLOW(Level.DEBUG), MANAGE_WORKFLOW(Level.INFO), DELETE_WORKFLOW(Level.INFO), GET_WORKLFOW_PARAMETERS(Level.DEBUG), 
		RUN_WORKFLOW(Level.INFO), GET_WORKFLOW_PROGRESS(Level.DEBUG), GET_WORKFLOW_BY_ID(Level.DEBUG), GET_WORKFLOW_RUNNING(Level.DEBUG), 
		MANAGE_RESOURCE(Level.INFO), REMOVE_RESOURCE(Level.INFO), GET_RESOURCES(Level.DEBUG), VALID_SCRIPT(Level.DEBUG), RUN_INCOMPLETE_WORKFLOW(Level.DEBUG), GET_WORKFLOW_RUNS(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

}
