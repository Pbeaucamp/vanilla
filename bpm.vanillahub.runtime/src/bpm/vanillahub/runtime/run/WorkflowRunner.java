package bpm.vanillahub.runtime.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.Workflow;

public class WorkflowRunner extends Thread {

	private IVanillaLogger logger;
	private WorkflowDao workflowManager;
	private FileManager fileManager;
	private ResourceDao resourceManager;
	private WorkflowProgressManager progressManager;

	private Workflow workflow;
	private String uuid;
	private Locale locale;
	private String launcher;
	private List<Parameter> parameters;
	private List<ListOfValues> lovs;
	private Integer userId;

	public WorkflowRunner(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager, Workflow workflow, WorkflowProgressManager progressManager) throws Exception {
		this(logger, workflowManager, fileManager, resourceManager, workflow, progressManager, null, null, null, null, null, null);
	}

	public WorkflowRunner(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager, Workflow workflow, WorkflowProgressManager progressManager,
			String uuid, Locale locale, String launcher, List<Parameter> parameters, List<ListOfValues> lovs, Integer userId) throws Exception {
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.fileManager = fileManager;
		this.resourceManager = resourceManager;
		this.workflow = workflow;
		this.progressManager = progressManager;
		
		this.locale = locale;
		this.launcher = launcher;
		this.parameters = parameters;
		this.lovs = lovs;
		this.userId = userId;

		// WorkflowProgress previousRun =
		// progressManager.getRunningWorkflow(workflow.getId());
		// if (previousRun != null && previousRun.getInstance() != null &&
		// !previousRun.getInstance().isFinish()) {
		// throw new
		// Exception("This workflow is already running. You cannot launch the same workflow twice at the same time.");
		// }
		// else {
		this.uuid = uuid != null ? uuid : UUID.randomUUID().toString();

		WorkflowProgress workflowProgress = new WorkflowProgress();
		progressManager.addWorkflow(workflow.getId(), this.uuid, workflowProgress);
		// }
	}

	public void run() {
		try {
			int userId = this.userId != null ? this.userId : workflow.getSchedule().getUserId();
			if (locale == null) {
				locale = new Locale("en_EN");
				if (userId > 0) {
					User user = resourceManager.getUser(userId);
					if (user.getLocale() != null) {
						locale = new Locale(user.getLocale());
					}
				}
			}
			
			launcher = launcher != null ? launcher : (workflow.getAuthorName() != null ? workflow.getAuthorName() : "System");
			parameters = parameters != null ? parameters : new ArrayList<Parameter>();//TODO: ARNIA - Disable for now for Arnia - workflow.getSchedule().getParameters();
			lovs = lovs != null ? lovs : resourceManager.getListOfValues();

			LaunchManager manager = new LaunchManager(logger, workflowManager, fileManager, resourceManager);
			manager.run(progressManager, locale, workflow, uuid, launcher, parameters, lovs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}