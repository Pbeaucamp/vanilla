package bpm.vanillahub.runtime.run;

import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.utils.SchedulerUtils;

public class SchedulerThreadManager extends Thread {

	private static final int DEFAULT_TIME_CHECK = 180000;
	private static final int DEFAULT_TIME_START = 60000;
	private static final int DEFAULT_RELAUNCH_TIME = 60000;

	private int startTime;
	private int checkTime;
	private int relaunchTime;

	private IVanillaLogger logger;
	private WorkflowDao workflowManager;
	private FileManager fileManager;
	private ResourceDao resourceManager;
	private WorkflowProgressManager workflowProgress;

	public SchedulerThreadManager(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager, WorkflowProgressManager workflowProgress) {
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.fileManager = fileManager;
		this.resourceManager = resourceManager;
		this.workflowProgress = workflowProgress;

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		try {
			this.startTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_TIME_START));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_TIME_START + " is not available in tsbn_application.properties. We use default value " + DEFAULT_TIME_START);
			this.startTime = DEFAULT_TIME_START;
		}

		try {
			this.checkTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_TIME_CHECK));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_TIME_CHECK + " is not available in tsbn_application.properties. We use default value " + DEFAULT_TIME_CHECK);
			this.checkTime = DEFAULT_TIME_CHECK;
		}

		try {
			this.relaunchTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_RELAUNCH_TIME));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_RELAUNCH_TIME + " is not available in tsbn_application.properties. We use default value " + DEFAULT_RELAUNCH_TIME);
			this.relaunchTime = DEFAULT_RELAUNCH_TIME;
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(startTime);
			logger.info("Checking previous workflows.");
			checkPreviousWorkflows(checkTime);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		boolean running = true;
		while (running) {
			try {
				Thread.sleep(checkTime);
				checkWorkflows(checkTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void checkPreviousWorkflows(int checkTime) throws Exception {
		Date currentDate = new Date();
		List<Workflow> workflows = workflowManager.getWorkflows(true);

		for (Workflow workflow : workflows) {

			if (!workflow.isValid() || !workflow.isScheduleDefine() || !workflow.getSchedule().isOn()) {
				continue;
			}

			WorkflowInstance instance = workflow.getLastRun();
			Date beforeExecution = SchedulerUtils.getPreviousExecution(currentDate, workflow.getSchedule(), checkTime);

			if (beforeExecution == null || instance != null && instance.getStartDate().after(beforeExecution)) {
				continue;
			}

			long diff = currentDate.getTime() - beforeExecution.getTime();
			if (diff < relaunchTime) {
				logger.info("Execute workflow " + workflow.getName());
				new SchedulerManager(logger, workflowManager, fileManager, resourceManager, workflowProgress).execute(workflow);
			}
		}
	}

	private void checkWorkflows(int checkTime) throws Exception {
		Date currentDate = new Date();
		List<Workflow> workflows = workflowManager.getWorkflows(true);

		for (Workflow workflow : workflows) {

			if (!workflow.isValid() || !workflow.isScheduleDefine() || !workflow.getSchedule().isOn()) {
				continue;
			}

			Date previousExecution = SchedulerUtils.getPreviousExecution(currentDate, workflow.getSchedule(), checkTime);
			Date stopDate = workflow.getSchedule().getStopDate();
			if (previousExecution == null || stopDate != null && previousExecution.after(stopDate)) {
				continue;
			}

			long diff = currentDate.getTime() - previousExecution.getTime();
			if (diff < checkTime) {
				logger.info("Execute workflow " + workflow.getName());
				new SchedulerManager(logger, workflowManager, fileManager, resourceManager, workflowProgress).execute(workflow);
			}
		}
	}
}
