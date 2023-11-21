package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.ISchedulerManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobDAO;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobInstanceDAO;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobManager;
import bpm.vanilla.platform.core.runtime.dao.scheduler.JobThreadManager;

public class SchedulerManager extends AbstractVanillaManager implements ISchedulerManager {

	private Logger logger = Logger.getLogger(this.getClass());

	private JobDAO jobDao;
	private JobInstanceDAO instanceDao;

	private JobThreadManager jobThread;

	private Calendar calendar;
	
	private IVanillaAPI vanillaApi;

	@Override
	public void init() throws Exception {
		boolean useUTCTime = false;
		try {
			useUTCTime = Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SCHEDULER_USE_UTC_TIME));
		} catch (Exception e) {
			logger.warn("The property " + VanillaConfiguration.P_SCHEDULER_USE_UTC_TIME + " is not available in vanilla.properties. We use server time to schedul jobs.");
			useUTCTime = false;
		}

		if (useUTCTime) {
			calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		}
		else {
			calendar = Calendar.getInstance();
		}

		jobDao = getDao().getJobDAO();
		instanceDao = getDao().getJobInstanceDAO();

		jobThread = new JobThreadManager(this, calendar);
		jobThread.start();
	}

	@Override
	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}
	
	@Override
	public void desactivate(ComponentContext ctx) {
		this.jobDao = null;
		this.instanceDao = null;
		
		jobThread.interrupt();
		jobThread = null;
		
		super.desactivate(ctx);
	}

	@Override
	public void addJob(Job job) {
		jobDao.save(job);
		jobThread.reloadJobs();
	}

	@Override
	public void editJob(Job job) throws Exception {
		jobDao.edit(job);
		jobThread.reloadJobs();
	}

	@Override
	public void delJob(Job job) {
		List<JobInstance> jobRuns = instanceDao.getJobHistoric(job.getId());
		instanceDao.delete(jobRuns);

		jobDao.delete(job);
		jobThread.reloadJobs();
	}

	// public List<JobRun> getJobsReports() {
	// return runDao.findAll();
	// }

	@Override
	public void runNowJob(Job job) throws Exception {
		JobManager jobManager = new JobManager(this);
		jobManager.execute(job);
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public List<JobInstance> getJobHistoric(Job job) throws Exception {
		return instanceDao.getJobHistoric(job.getId());
	}

	// public void addJobRun(JobInstance instance) {
	// runDao.save(report);
	// }

	@Override
	public List<Job> getJobs(Integer repositoryId) throws Exception {
		List<Job> jobsToReturn = new ArrayList<Job>();

		List<Job> jobs = jobDao.getJobs();
		if (jobs != null) {
			for (Job job : jobs) {
				if (repositoryId != null && job.getRepositoryId().equals(repositoryId)) {
					job.setInstances(getJobHistoric(job));
					jobsToReturn.add(job);
					
					//find the repository item
					try {
						int itemId = job.getDetail().getItemId();
						RepositoryItem item = getRepositoryApi(repositoryId).getRepositoryService().getDirectoryItem(itemId);
						job.setItem(item);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if (repositoryId == null) {
					job.setInstances(getJobHistoric(job));
					jobsToReturn.add(job);
				}
			}
		}
		return jobsToReturn;
	}

	public void saveJobInstance(JobInstance instance) {
		instanceDao.save(instance);
	}
	
	public IVanillaAPI getVanillaApi() {
		if(vanillaApi == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaUrl = config.getVanillaServerUrl();
			String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			vanillaApi = new RemoteVanillaPlatform(vanillaUrl, login, password);
		}
		return vanillaApi; 
	}

	public IRepositoryApi getRepositoryApi(int repository) {
		Repository repo = new Repository();
		repo.setId(repository);
		IRepositoryApi api = new RemoteRepositoryApi(new BaseRepositoryContext(getVanillaApi().getVanillaContext(), 
				new Group(), 
				repo));
		return api;
	}
}
