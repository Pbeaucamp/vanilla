package bpm.vanilla.server.gateway.server.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.TransformationLog;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.components.GatewayComponent.ActionType;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.commons.server.tasks.GatewayState;
import bpm.vanilla.server.gateway.server.tasks.TaskGateway;

public class RunnableGateway extends Thread {

	private HashMap<RuntimeStep, LogListener> steps;
	private Logger logger;

	private TaskGateway task;
	private GatewayState taskSate;

	private boolean shouldRun = true;

	private Integer runInstanceId;

	// Used to restore the adefault connections to use for the server in case
	// where alternate COnnections are used
	private DocumentGateway gatewayModel;

	public RunnableGateway(TaskGateway task, GatewayState taskSate, HashMap<RuntimeStep, LogListener> steps, Logger logger, DocumentGateway gatewayModel) {
		this.taskSate = taskSate;
		this.task = task;
		this.steps = steps;
		this.logger = logger;
		this.gatewayModel = gatewayModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		super.run();

		/*
		 * init steps
		 */
		logger.info("Task " + task.getId() + " initing GatewaySteps");
		for (RuntimeStep step : steps.keySet()) {
			try {
				step.init(task);
			} catch (Exception e) {
				e.printStackTrace();
				
				String errorMessage = "Error while initing step " + step.getTransformation().getName() + " : " + e.getMessage();
				
				shouldRun = false;
				logger.error("Task " + task.getId() + " error while initing step " + step.getTransformation().getName(), e);
				taskSate.setFailed(errorMessage);
				shouldRun = false;

				for (RuntimeStep r : steps.keySet()) {
					try {
						r.releaseResources();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {

						r.interrupt();
					} catch (Exception ex) {
						ex.printStackTrace();
						try {
							r.interrupt();
						} catch (Exception ex2) {
							ex2.printStackTrace();
						}
					}

				}

				/*
				 * stop the Task
				 */
				taskSate.setStopped();

				for (RuntimeStep tr : steps.keySet()) {

					LogListener listener = steps.get(tr);
					if (listener != null) {
						tr.removeLogListeners(listener);
						listener.releaseResources();
					}

				}

				StringBuffer buf = new StringBuffer();
				for (RuntimeStep tr : steps.keySet()) {
					for (TransformationLog log : tr.getLogs()) {
						if (log.priority == Level.ERROR_INT) {
							buf.append(log.message + "\n");
						}
					}
				}

				String stepFailedError = buf.toString();
				if (stepFailedError != null && !stepFailedError.isEmpty()) {
					taskSate.setFailed(stepFailedError);
				}

				/*
				 * we release the alternateConnection from DataBaseServers
				 * attached the this GatewayTask
				 */
				for (Server s : ResourceManager.getInstance().getServers(DataBaseServer.class)) {
					((DataBaseServer) s).removeOverridenConnection(task);
				}
				flushLogsIntoFile();
				try {
					task.stopTask();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				log(bpm.vanilla.platform.core.beans.VanillaLogs.Level.ERROR, VanillaComponentType.COMPONENT_GATEWAY, 
						ActionType.RUN_GATEWAY, task.getGroupId(), task.getObjectIdentifier().getRepositoryId(), 
						task.getObjectIdentifier().getDirectoryItemId(), errorMessage, 0);
				
				return;
			}
		}
		logger.info("Task " + task.getId() + " GatewaySteps inited");

		/*
		 * starting steps
		 */
		logger.info("Task " + task.getId() + " starting GatewaySteps");
		for (RuntimeStep step : steps.keySet()) {
			step.start();
		}
		logger.info("Task " + task.getId() + " GatewaySteps started");

		/*
		 * main loop
		 */
		taskSate.setStarted();
		while (shouldRun && stepsRunning()) {
			try {
				sleep(2000);
			} catch (InterruptedException e) {

				if (stepsRunning()) {
					shouldRun = false;
				}
				else {
					interrupt();
					shouldRun = false;
					logger.warn("Task " + task.getId() + " auto-interrupted");
				}
			}
		}

		/*
		 * release Resources
		 */
		logger.info("Task " + task.getId() + " releasing resources");
		for (RuntimeStep tr : steps.keySet()) {
			tr.releaseResources();

		}
		for (Server server : gatewayModel.getResourceManager().getServers()) {
			try {
				server.disconnect();
			} catch (ServerException e) {
				logger.error("Error while closing server connection " + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.info("Task " + task.getId() + " released resource");

		taskSate.setStopped();

		for (RuntimeStep tr : steps.keySet()) {

			LogListener listener = steps.get(tr);
			if (listener != null) {
				tr.removeLogListeners(listener);
				listener.releaseResources();
			}

		}

		boolean failed = false;
		StringBuffer buf = new StringBuffer();
		for (RuntimeStep tr : steps.keySet()) {
			for (TransformationLog log : tr.getLogs()) {
				if (log.priority == Level.ERROR_INT) {
					failed = true;
					buf.append(log.message + "\n");
				}
			}
		}

		if (failed) {
			taskSate.setFailed(buf.toString());
		}
		else {
			taskSate.setSucceed();
		}

		/*
		 * we release the alternateConnection from DataBaseServers attached the
		 * this GatewayTask
		 */
		for (Server s : ResourceManager.getInstance().getServers(DataBaseServer.class)) {
			((DataBaseServer) s).removeOverridenConnection(task);
		}

		flushLogsIntoFile();
		try {
			task.stopTask();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("##############################################################");
		logger.info("End ETL " + task.getItemName());
		logger.info("##############################################################");
		

		if (failed) {
			log(bpm.vanilla.platform.core.beans.VanillaLogs.Level.ERROR, VanillaComponentType.COMPONENT_GATEWAY, 
					ActionType.RUN_GATEWAY, task.getGroupId(), task.getObjectIdentifier().getRepositoryId(), 
					task.getObjectIdentifier().getDirectoryItemId(), buf.toString(), 0);
		}
		else {
			log(bpm.vanilla.platform.core.beans.VanillaLogs.Level.INFO, VanillaComponentType.COMPONENT_GATEWAY, 
					ActionType.RUN_GATEWAY, task.getGroupId(), task.getObjectIdentifier().getRepositoryId(), 
					task.getObjectIdentifier().getDirectoryItemId(), "FINISH", 0);
		}
	}
	
	public void log(bpm.vanilla.platform.core.beans.VanillaLogs.Level level, String componentId, ActionType actionType, int groupId, int repId, int itemId, String message, long delay) {
		IVanillaAPI vanillaApi = getRootVanillaApi();
		
		VanillaLogs log = new VanillaLogs(level, componentId, actionType.toString(), new Date(), 0, groupId, repId, itemId, "", message, delay);
		try {
			vanillaApi.getVanillaLoggingManager().addVanillaLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void flushLogsIntoFile() {
		FactoryRunnableGateway.restoreDefaultConnections(task, gatewayModel);

		/*
		 * Flush execution details in database
		 */
		Integer instanceId = null;
		try {
			GatewayRuntimeState m = getStepsInfosMessage();

			IRepositoryApi repositoryApi = getRootRepositoryApi(task.getObjectIdentifier().getRepositoryId());

			logger.info("Task " + task.getId() + " save Gateway resume in database.");

			ItemInstance instance = new ItemInstance();
			instance.setItemId(task.getObjectIdentifier().getDirectoryItemId());
			instance.setGroupId(task.getGroupId());
			instance.setItemType(IRepositoryApi.GTW_TYPE);
			instance.setResult(task.getTaskState().getTaskResult());
			instance.setDuration(task.getTaskState().getDuration());
			instance.setRunDate(new Date());
			instance.setState(m);

			instanceId = repositoryApi.getAdminService().addItemInstance(instance);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		synchronized (steps) {
			logger.info("Task " + task.getId() + " clearing state");
			for (RuntimeStep r : steps.keySet()) {
				r.removeLogListeners(steps.get(r));
			}
			steps.clear();

			steps = null;
			runInstanceId = instanceId;
			logger.info("Task " + task.getId() + " cleared state");
		}
		Runtime.getRuntime().gc();
	}

	private IVanillaAPI getRootVanillaApi() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);

		return new RemoteVanillaPlatform(new BaseVanillaContext(url, login, password));
	}

	private IRepositoryApi getRootRepositoryApi(int repositoryId) {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);

		Repository repository = new Repository();
		repository.setId(repositoryId);

		Group grp = new Group();
		grp.setId(-1);

		return new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, password), grp, repository));
	}

	public void stopTask() {
		shouldRun = false;
	}

	private boolean stepsRunning() {
		for (RuntimeStep step : steps.keySet()) {
			if (step.isAlive()) {
				return true;
			}
		}

		return false;
	}

	private synchronized GatewayRuntimeState getStepsInfosMessageFromHashMap() {

		GatewayRuntimeState runtimeState = new GatewayRuntimeState();
		runtimeState.setStoppedDate(taskSate.getStoppedDate());
		runtimeState.setName(gatewayModel.getName());
		runtimeState.setStartedDate(taskSate.getStartedDate());
		runtimeState.setFailureCause(taskSate.getFailingCause());

		if (taskSate.hasFailed()) {
			runtimeState.setState(ActivityState.FAILED);
		}
		else {
			runtimeState.setState(taskSate.getTaskState());
		}

		for (RuntimeStep step : steps.keySet()) {

			int countErrors = 0;
			int countWarnings = 0;

			StringBuffer bufLog = new StringBuffer();

			Iterator<TransformationLog> iter = step.getLogs().iterator();
			while(iter.hasNext()){
//			for (TransformationLog log : step.getLogs()) {
				TransformationLog log = iter.next();

				if (log.priority == Level.ERROR_INT) {
					countErrors++;
					bufLog.append("{ERROR:" + log.message + "}");
				}
				else if (log.priority == Level.WARN_INT) {
					countWarnings++;
					bufLog.append("{WARN:" + log.message + "}");
				}
				else if (log.priority == Level.DEBUG_INT) {
					bufLog.append("{DEBUG:" + log.message + "}");
				}
				else {
					bufLog.append("{INFO:" + log.message + "}");
				}
			}

			String stepName = step.getName();
			long bufferedRows = step.getStatsBufferedRows();
			long processedRows = step.getStatsProcessedRows();
			long readedRows = step.getStatsReadedRows();
			long duration = step.getDuration() != null ? step.getDuration() : 0;
			Date startTime = step.getStartTime();
			Date stopTime = step.getStopTime();
			String state = step.getTransformationStateName();

			StepInfos info = new StepInfos(stepName, bufferedRows, processedRows, readedRows, countErrors, countWarnings, duration, startTime, stopTime, state, bufLog.toString());
			runtimeState.addStepInfo(info);
		}

		return runtimeState;
	}

	public GatewayRuntimeState getStepsInfosMessage() throws Exception {

		if (runInstanceId != null) {
			try {
				IRepositoryApi repositoryApi = getRootRepositoryApi(task.getObjectIdentifier().getRepositoryId());

				ItemInstance instance = repositoryApi.getAdminService().getItemInstance(runInstanceId);
				return (GatewayRuntimeState) instance.getState();
			} catch (Exception ex) {
				logger.error("Task " + task.getId() + " getting resume from database with id " + runInstanceId + " failed\n" + ex.getMessage(), ex);
				ex.printStackTrace();
				throw new Exception("Unable to extract runtime resume from database with id " + runInstanceId + ":\n" + ex.getMessage());
			}
		}
		else {
			return getStepsInfosMessageFromHashMap();
		}
	}
}
