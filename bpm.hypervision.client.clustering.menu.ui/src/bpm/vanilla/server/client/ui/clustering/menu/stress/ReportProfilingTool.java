package bpm.vanilla.server.client.ui.clustering.menu.stress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class ReportProfilingTool {

	/*
	 * context
	 */
	public static final String P_REPORT_LOGIN = "vanilla.context.login"; //$NON-NLS-1$
	public static final String P_REPORT_PASSWORD = "vanilla.context.password"; //$NON-NLS-1$
	public static final String P_REPORT_GROUP_ID = "vanilla.context.groupId"; //$NON-NLS-1$
	public static final String P_REPORT_GROUP_NAME = "vanilla.context.groupName"; //$NON-NLS-1$
	public static final String P_CLUSTER_RUNTIME_URL = "vanilla.context.vanillaRuntimeUrl"; //$NON-NLS-1$

	/*
	 * object identifier
	 */
	public static final String P_REPORT_REPOSITORY_ID = "report.repositoryId"; //$NON-NLS-1$
	public static final String P_REPORT_DIRECTORY_ITEM_ID = "report.directoryItemId"; //$NON-NLS-1$

	/*
	 * object options
	 */
	public static final String P_REPORT_OUTPUT_FORMAT = "report.outputFormat"; //$NON-NLS-1$
	public static final String P_REPORT_PRIORITY = "report.taskPriority"; //$NON-NLS-1$

	/*
	 * profiling options
	 */
	public static final String P_PROFIING_NUMBER_OF_RUN = "profiling.runNumber"; //$NON-NLS-1$

	/*
	 * server config options
	 */
	public static final String P_RUNTIME_SIMULTANOUS_TASKS = "vanilla.runtime.configuration.maxRunningTasks"; //$NON-NLS-1$
	public static final String P_RUNTIME_SIMULTANOUS_REPOSITORY_CONNECTION = "vanilla.runtime.configuration.maxRepositoryConnection"; //$NON-NLS-1$

	public List<ProfilingResult> stress(IProgressMonitor monitor, ServerType type, IRuntimeConfig config, int numberOfRun, String url, String login, String password, 
			List<ServerConfigurationProps> configs) throws Exception {

		monitor.beginTask(Messages.ReportProfilingTool_14, IProgressMonitor.UNKNOWN);
		/*
		 * connect to server
		 */
		IVanillaServerManager remote = null;
		monitor.subTask(Messages.ReportProfilingTool_15);
		try {
			remote = createRemote(type, url, login, password);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(Messages.ReportProfilingTool_16 + ex.getMessage());
		}

		monitor.subTask(Messages.ReportProfilingTool_19);

		ServerConfigInfo initialCOnfiguration = null;

		List<ProfilingResult> results = new ArrayList<ProfilingResult>();

		try {
			initialCOnfiguration = remote.getServerConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		monitor.worked(1);

		monitor.subTask(Messages.ReportProfilingTool_20);

		for (ServerConfigurationProps conf : configs) {
			/*
			 * configure Server
			 */
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			monitor.subTask(Messages.ReportProfilingTool_21 + numberOfRun + Messages.ReportProfilingTool_22 + conf.numberRepConnection + "}");//$NON-NLS-1$
			try {
				ServerConfigInfo cf = conf.update(remote.getServerConfig());

				remote.stopServer();

				remote.resetServerConfig(cf);

				remote.startServer();
				
				User user = Activator.getDefault().getUser();
				
				/*
				 * profiling
				 */
				ProfilingResult r = launchProfiling(remote, config, numberOfRun, conf, user, conf.numberTasks);
				r.dump();
				results.add(r);

			} catch (Exception ex) {
				System.err.println(Messages.ReportProfilingTool_29 + ex.getMessage());
				ex.printStackTrace();
			}
			monitor.worked(1);
		}

		/*
		 * restore config
		 */
		monitor.subTask(Messages.ReportProfilingTool_31);
		try {
			remote.stopServer();
			remote.resetServerConfig(initialCOnfiguration);
			remote.startServer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		monitor.worked(1);
		return results;
	}

	public void stop() {
		if (taskProfilers != null) {
			for (TaskRunner r : taskProfilers) {
				try {
					r.interrupt();
				} catch (Throwable tt) {
					try {
						r.interrupt();
					} catch (Throwable t) {

					}
				}
			}
			taskProfilers.clear();
		}
	}

	private IVanillaServerManager createRemote(ServerType type, String url, String login, String password) {
		IVanillaServerManager remote = null;
		if (type == ServerType.GATEWAY) {
			remote = new RemoteGatewayComponent(url, login, password);
		}
		else if (type == ServerType.REPORTING) {
			remote = new RemoteReportRuntime(url, login, password);
		}
		return remote;
	}

	private List<TaskRunner> taskProfilers;

	private ProfilingResult launchProfiling(IVanillaServerManager remote, IRuntimeConfig config, int numberOfRun, ServerConfigurationProps conf, User user, int simultaneousRun) {
		/*
		 * launchTasks
		 */
		taskProfilers = new ArrayList<TaskRunner>();
		
		for (int i = 0; i < numberOfRun; i++) {
			taskProfilers.add(new TaskRunner(remote, config, i, user));
		}

		System.out.println(Messages.ReportProfilingTool_43);

		int i = 1;
		for (TaskRunner r : taskProfilers) {
			r.start();
			
			if(i % simultaneousRun == 0) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) { }
			}
			i++;
		}

		/*
		 * wait end Tasks
		 */

		for (TaskRunner r : taskProfilers) {
			try {
				r.join();
			} catch (Exception ex) {
				System.out.println(Messages.ReportProfilingTool_44 + ex.getMessage());
				ex.printStackTrace();
			}
			if (r.hasProfilingSucceed()) {
				System.out.println(r.getIdentification() + Messages.ReportProfilingTool_45);
			}
			else {
				System.out.println(r.getIdentification() + Messages.ReportProfilingTool_46);
			}
		}

		/*
		 * generate ResultFile
		 */
		if (conf == null) {
			new File("reporting.csv"); //$NON-NLS-1$
		}
		else {
			new File(conf.toString() + "reporting.csv"); //$NON-NLS-1$
		}
		return generateResultFile(taskProfilers, conf);
	}

	private ProfilingResult generateResultFile(List<TaskRunner> taskProfilers, ServerConfigurationProps conf) {

		Object[] values = new Object[] { new ArrayList<Long>(), new ArrayList<Long>(), new ArrayList<Long>() };

		for (TaskRunner t : taskProfilers) {
			try {
				((List) values[0]).add(t.getSubmissionTime());
			} catch (Exception ex) {
			}

			try {
				long l = t.getTaskInfo().getStoppedDate().getTime() - t.getTaskInfo().getStartedDate().getTime();
				((List) values[1]).add(l);
			} catch (Exception ex) {
			}

			try {
				long l = t.getTaskInfo().getStartedDate().getTime() - t.getTaskInfo().getCreationDate().getTime();
				((List) values[2]).add(l);
			} catch (Exception ex) {
			}
		}

		/*
		 * compute results
		 */
		double[] val = new double[] { 0, 0, 0 };
		for (int i = 0; i < 3; i++) {

			for (Long l : (List<Long>) values[i]) {
				val[i] += l;
			}

			val[i] = val[i] / ((List<Long>) values[i]).size();
		}

		return new ProfilingResult(val[0], val[1], val[2], conf.numberTasks, conf.numberRepConnection);
	}
}
