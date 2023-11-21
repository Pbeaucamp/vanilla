package bpm.vanilla.platform.core.runtime.dao.scheduler;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance.Result;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.components.SchedulerManager;
import bpm.vanilla.platform.core.runtime.tools.MailHelper;

public class JobManager {

	private SchedulerManager schedulerManager;

	public JobManager(SchedulerManager schedulerManager) {
		this.schedulerManager = schedulerManager;
	}

	public void execute(Job job) {
		new JobRunner(job).start();
	}

	private class JobRunner extends Thread {

		private Job job;

		public JobRunner(Job job) {
			this.job = job;
		}

		public void run() {
			JobInstance instance = new JobInstance(job.getId());
			instance.setResult(Result.RUNNING);

			JobDetail jobDetails = job.getDetail();

			String vanillaRuntimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			String rootUser = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String rootPass = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			IVanillaContext ctx = new BaseVanillaContext(vanillaRuntimeUrl, rootUser, rootPass);

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);

			try {
				try {
					job.setNeedToBeLaunch(false);
					schedulerManager.editJob(job);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Unable to update the job '" + job.getName() + "' in the database.");
				}

				if (jobDetails == null) {
					throw new Exception("This job does not contains all the necessary informations to be launch.");
				}
				User user = vanillaApi.getVanillaSecurityManager().getUserById(jobDetails.getUserId());
				Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(jobDetails.getRepositoryId());
				Group group = vanillaApi.getVanillaSecurityManager().getGroupById(jobDetails.getGroupId());

				IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(ctx, group, repository));
				RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(jobDetails.getItemId());

				IVanillaContext userVanillaCtx = new BaseVanillaContext(vanillaRuntimeUrl, user.getLogin(), user.getPassword());

				if (item != null) {
					if (jobDetails.getParameters() != null && !jobDetails.getParameters().isEmpty()) {
						Logger.getLogger(getClass()).debug("Launching job with the following parameters");
						for (VanillaGroupParameter grParam : jobDetails.getParameters()) {
							if (grParam.getParameters() != null && !grParam.getParameters().isEmpty()) {
								for (VanillaParameter param : grParam.getParameters()) {
									Logger.getLogger(getClass()).debug(param.getName() + " with values ");
									for(String value : param.getValues().keySet()) {
										Logger.getLogger(getClass()).debug(" ==> " + param.getValues().get(value));
									}
								}
							}
						}
					}

					if (item.getType() == IRepositoryApi.GTW_TYPE) {
						launchGateway(userVanillaCtx, jobDetails, user);
					}
					else if (item.getType() == IRepositoryApi.BIW_TYPE) {
						launchWorkflow(userVanillaCtx, jobDetails);
					}
					else if (item.getType() == IRepositoryApi.FWR_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)) {
						launchReport(vanillaApi, userVanillaCtx, jobDetails, user);
					}
					else {
						throw new Exception("The type of the item is not supported by the Process Manager.");
					}

					instance.setEndDate(new Date());
					instance.setResult(Result.SUCCESS);
					instance.setMessage("No errors");

					schedulerManager.saveJobInstance(instance);
				}
				else {
					throw new Exception("Unable to get the item with the id = " + jobDetails.getItemId());
				}
			} catch (Exception ex) {
				ex.printStackTrace();

				instance.setEndDate(new Date());
				instance.setResult(Result.ERROR);
				instance.setMessage(ex.getMessage());

				schedulerManager.saveJobInstance(instance);
				return;
			}
		}

		private void launchGateway(IVanillaContext userVanillaCtx, JobDetail jobDetails, User user) throws Exception {
			try {
				GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration(new ObjectIdentifier(jobDetails.getRepositoryId(), jobDetails.getItemId()), jobDetails.getParameters(), jobDetails.getGroupId());

				RemoteGatewayComponent remote = new RemoteGatewayComponent(userVanillaCtx);

				Logger.getLogger(getClass()).debug("Launching Gateway...");
				GatewayRuntimeState state = remote.runGateway(conf, user);
				Logger.getLogger(getClass()).debug("Gateway terminated.");

				if (state != null && (state.getState() != ActivityState.ENDED || state.getState() == ActivityState.FAILED)) {
					throw new Exception(state.getFailureCause());
				}

				if (state.getState() != ActivityState.ENDED) {
				}
			} catch (Exception ex) {
				throw new Exception("An error occured during the launch of the gateway.\n " + ex.getMessage());
			}
		}

		private void launchWorkflow(IVanillaContext userVanillaCtx, JobDetail jobDetails) throws Exception {
			try {
				IRuntimeConfig conf = new RuntimeConfiguration(jobDetails.getGroupId(), new ObjectIdentifier(jobDetails.getRepositoryId(), jobDetails.getItemId()), jobDetails.getParameters());

				RemoteWorkflowComponent remote = new RemoteWorkflowComponent(userVanillaCtx);

				Logger.getLogger(getClass()).debug("Launching Workflow...");
				IRunIdentifier wkfId = remote.startWorkflow(conf);
				if (wkfId != null) {
					TaskInfo taskInfos = remote.getTasksInfo(wkfId);
					if (taskInfos != null && taskInfos.getResult() == ActivityResult.FAILED) {
						throw new Exception(taskInfos.getFailureCause());
					}
				}
				Logger.getLogger(getClass()).debug("Workflow terminated.");
			} catch (Exception ex) {
				throw new Exception("An error occured during the launch of the workflow.\n " + ex.getMessage());
			}
		}

		private void launchReport(IVanillaAPI vanillaApi, IVanillaContext userVanillaCtx, JobDetail jobDetails, User user) throws Exception {
			try {
				ReportRuntimeConfig conf = new ReportRuntimeConfig(new ObjectIdentifier(jobDetails.getRepositoryId(), jobDetails.getItemId()), jobDetails.getParameters(), jobDetails.getGroupId());
				conf.setOutputFormat(jobDetails.getFormat());

				RemoteReportRuntime remote = new RemoteReportRuntime(userVanillaCtx);

				Logger.getLogger(getClass()).debug("Launching Report...");
				InputStream report = remote.runReport(conf, user);
				
				if (jobDetails.getSubscribers() != null && !jobDetails.getSubscribers().isEmpty()) {
					HashMap<String, InputStream> attachements = new HashMap<String, InputStream>();
					attachements.put("Report_" + new Object().hashCode() + "." + jobDetails.getFormat(), report);
					
					for (int userId : jobDetails.getSubscribers()) {
						User subscriber = vanillaApi.getVanillaSecurityManager().getUserById(userId);
						if (subscriber != null && subscriber.getBusinessMail() != null && !subscriber.getBusinessMail().isEmpty()) {
							IMailConfig config = new MailConfig(subscriber.getBusinessMail(), "no-reply@bpm-conseil.com", jobDetails.getContent(), jobDetails.getSubject(), false);
							if(attachements != null){
								try {
									MailHelper.sendEmail(config, attachements);
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
							else {
								try {
									MailHelper.sendEmail(config, new HashMap<String, InputStream>());
								} catch (Throwable e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				
				Logger.getLogger(getClass()).debug("Report terminated.");
			} catch (Exception ex) {
				throw new Exception("An error occured during the launch of the report.\n " + ex.getMessage());
			}
		}
	}
}
