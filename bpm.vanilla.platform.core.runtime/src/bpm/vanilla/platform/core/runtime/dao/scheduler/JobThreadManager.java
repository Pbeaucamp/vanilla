package bpm.vanilla.platform.core.runtime.dao.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.runtime.components.SchedulerManager;
import bpm.vanilla.platform.core.utils.SchedulerUtils;

public class JobThreadManager extends Thread {

	private Logger logger = Logger.getLogger(this.getClass());

	private static final int DEFAULT_TIME_CHECK = 180000;
	private static final int DEFAULT_TIME_START = 60000;
	private static final int DEFAULT_RELAUNCH_TIME = 60000;

	private int startTime;
	private int checkTime;
	private int relaunchTime;

	private SchedulerManager schedulerManager;

	private boolean reload = true;
	private boolean launchJobs = true;
	private List<Job> jobs;

	private Calendar calendar;

	public JobThreadManager(SchedulerManager schedulerManager, Calendar calendar) {
		this.schedulerManager = schedulerManager;
		this.calendar = calendar;

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		try {
			this.startTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_TIME_START));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_TIME_START + " is not available in vanilla.properties. We use default value " + DEFAULT_TIME_START);
			this.startTime = DEFAULT_TIME_START;
		}

		try {
			this.checkTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_TIME_CHECK));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_TIME_CHECK + " is not available in vanilla.properties. We use default value " + DEFAULT_TIME_CHECK);
			this.checkTime = DEFAULT_TIME_CHECK;
		}

		try {
			this.relaunchTime = Integer.parseInt(config.getProperty(VanillaConfiguration.P_SCHEDULER_RELAUNCH_TIME));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_RELAUNCH_TIME + " is not available in vanilla.properties. We use default value " + DEFAULT_RELAUNCH_TIME);
			this.relaunchTime = DEFAULT_RELAUNCH_TIME;
		}
	}

	public void reloadJobs() {
		this.reload = true;
	}

	@Override
	public void run() {
		doSleep(startTime);

		checkPreviousJobs(checkTime);

		boolean running = true;
		while (running && launchJobs) {
			checkJobs(checkTime);

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

	private List<Job> getJobs() {
//		logger.debug("Getting jobs to run.");
		if (reload) {
			try {
				List<Job> jobs = schedulerManager.getJobs(null);

				if (jobs != null) {
					logger.debug("Found " + jobs.size() + " jobs.");
				}
				else {
					logger.debug("Found 0 jobs.");
				}
				launchJobs = true;
				reload = false;
				return jobs != null ? jobs : new ArrayList<Job>();
			} catch (Exception e) {
				e.printStackTrace();
				launchJobs = false;
				logger.error("Unable to get the jobs. We disable the launch of future jobs. Please check with an administrator.");

				return new ArrayList<Job>();
			}
		}
		else {
//			logger.debug("No need to reload jobs.");
			return jobs;
		}
	}

	private void checkPreviousJobs(int checkTime) {
		logger.debug("Checking previous jobs.");
		this.jobs = getJobs();
		if (jobs != null) {
			for (Job job : jobs) {
				logger.debug("Manage job '" + job.getName() + "'.");

				if (!job.isOn()) {
					continue;
				}

				JobInstance lastRun = job.getLastRun();
				Date beforeExecution = SchedulerUtils.getPreviousExecution(calendar.getTime(), job.getDetail(), checkTime);
				Date stopDate = job.getStopDate();

				if (beforeExecution == null) {
					logger.debug("No previous date found for this job.");
					continue;
				}
				else {
					logger.debug("Found previous date = " + beforeExecution);
				}

				if (lastRun != null && lastRun.getStartDate().after(beforeExecution)) {
					logger.debug("Last run (Start Date) = " + lastRun.getStartDate() + ". We don't launch the job.");
					continue;
				}
				else if (lastRun != null) {
					logger.debug("Last run (Start Date) = " + lastRun.getStartDate() + ". We check if we launch the job.");
				}
				else {
					logger.debug("No previous run.");
				}

				if (stopDate != null && beforeExecution.after(stopDate)) {
					logger.debug("Stop date = " + stopDate + ". We don't launch the job.");
					continue;
				}

				long diff = calendar.getTime().getTime() - beforeExecution.getTime();
				if (diff < relaunchTime) {
					logger.info("Execute workflow " + job.getName());
					launchJob(job);
				}
				else {
					editJob(job);
				}
			}
		}
	}

	private void checkJobs(int checkTime) {
		calendar.setTime(new Date());
		this.jobs = getJobs();
		if (jobs != null) {
			for (Job job : jobs) {

				if (!job.isOn()) {
					continue;
				}

				Date previousExecution = SchedulerUtils.getPreviousExecution(calendar.getTime(), job.getDetail(), checkTime);
				Date stopDate = job.getStopDate();
				if (previousExecution == null || stopDate != null && previousExecution.after(stopDate)) {
					continue;
				}

				long diff = calendar.getTime().getTime() - previousExecution.getTime();
				if (diff < checkTime) {
					logger.debug("Actual time = " + calendar.getTime().getTime() + ". Previous date = " + previousExecution.getTime() + ". Diff (" + diff + ") is lower than + (" + checkTime + "). Launching job!");
					logger.info("Execute workflow " + job.getName());
					launchJob(job);
				}
			}
		}
	}

	private void launchJob(Job job) {
		logger.info("Execute job " + job.getName());
		try {
			new JobManager(schedulerManager).execute(job);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to execute the job " + job.getName());
		}
	}

	private void editJob(Job job) {
		logger.info("Edit job " + job.getName() + ". Set NeedToBeLaunch to true.");
		try {
			job.setNeedToBeLaunch(true);
			schedulerManager.editJob(job);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unable to edit the job " + job.getName());
		}
	}
}
