package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface ISchedulerManager {
	public static final String SERVLET_SCHEDULER = "/vanilla40/SchedulerServlet";
	
	public static enum ActionType implements IXmlActionType{
		ADD_JOB(Level.INFO), DELETE_JOB(Level.INFO), EDIT_JOB(Level.INFO), RUN_NOW_JOB(Level.DEBUG), GET_JOB_HISTORIC(Level.DEBUG), GET_JOBS(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addJob(Job job) throws Exception;
	public void delJob(Job job) throws Exception;
	public void editJob(Job job) throws Exception;	
	public void runNowJob(Job job) throws Exception;
	
	public List<Job> getJobs(Integer repositoryId) throws Exception;
	
	public List<JobInstance> getJobHistoric(Job job) throws Exception;
}
