package bpm.vanillahub.runtime.run;

import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.Workflow;

public class SchedulerManager {

	private IVanillaLogger logger;
	private WorkflowDao workflowManager;
	private FileManager fileManager;
	private ResourceDao resourceManager;
	private WorkflowProgressManager progressManager;

	public SchedulerManager(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager, WorkflowProgressManager progressManager) {
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.fileManager = fileManager;
		this.resourceManager = resourceManager;
		this.progressManager = progressManager;
	}

	public void execute(Workflow workflow) throws Exception {
		new WorkflowRunner(logger, workflowManager, fileManager, resourceManager, workflow, progressManager).start();
	}
}
