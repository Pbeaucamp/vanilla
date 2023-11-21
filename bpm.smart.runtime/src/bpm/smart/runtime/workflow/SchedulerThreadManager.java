package bpm.smart.runtime.workflow;

import java.util.Date;
import java.util.List;

import bpm.smart.runtime.SmartManagerComponent;
import bpm.smart.runtime.SmartManagerService;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
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

	private SmartManagerComponent smartManagerComponent;
	private IVanillaLogger logger;
	private WorkflowManager workflowManager;
	private IVanillaAPI vanillaApi;
	private SmartManagerService component;

	public SchedulerThreadManager(SmartManagerComponent smartManagerComponent, IVanillaLogger logger, WorkflowManager workflowManager, IVanillaAPI vanillaApi) {
		this.smartManagerComponent = smartManagerComponent;
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.vanillaApi = vanillaApi;

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

	private void initManagerService() throws Exception {
		this.component = new SmartManagerService(smartManagerComponent, vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaApi.getVanillaContext().getLogin()));
	}

	@Override
	public void run() {
		doSleep(startTime);

		boolean running = true;
		try {
			initManagerService();
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}

		if (running) {
			logger.info("Checking previous workflows.");
			try {
				checkPreviousWorkflows(checkTime);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		while (running) {
			try {
				checkWorkflows(checkTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			running = doSleep(checkTime);
		}
	}

	private boolean doSleep(int time) {
//		logger.debug("Waiting " + time + " ms");

		try {
			Thread.sleep(time);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void checkPreviousWorkflows(int checkTime) throws Exception {
		Date currentDate = new Date();
		List<Workflow> workflows = component.getWorkflows();

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
		//		new SchedulerManager(logger, workflowManager, fileManager, resourceManager, workflowProgress).execute(workflow);
				ResourceManager resourceManager = component.initResourceManager();
				workflowManager.runWorkflow(workflow, workflow.getSchedule().getParameters(), component.getLocale(), component, resourceManager);
			}
		}
	}

	private void checkWorkflows(int checkTime) throws Exception {
		Date currentDate = new Date();
		List<Workflow> workflows = component.getWorkflows();

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
//				new SchedulerManager(logger, workflowManager, fileManager, resourceManager, workflowProgress).execute(workflow);
				ResourceManager resourceManager = component.initResourceManager();
				workflowManager.runWorkflow(workflow, workflow.getSchedule().getParameters(), component.getLocale(), component, resourceManager);
			}
		}
	}
}
