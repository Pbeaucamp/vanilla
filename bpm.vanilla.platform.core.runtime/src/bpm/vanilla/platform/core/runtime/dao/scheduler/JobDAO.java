package bpm.vanilla.platform.core.runtime.dao.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobDetail;
import bpm.vanilla.platform.core.utils.SchedulerUtils;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import com.thoughtworks.xstream.XStream;

public class JobDAO extends HibernateDaoSupport {

//	private Calendar calendar;
	
	public JobDAO() {
	}

	private void buildJob(Job job) {
		if (job.getDetailXml() != null && !job.getDetailXml().isEmpty()) {
			job.setDetail((JobDetail) new XStream().fromXML(job.getDetailXml()));
			
			Date nextExecution = SchedulerUtils.getNextExecution(new Date(), job.getDetail());
			job.setNextExecution(nextExecution);
		}
	}

	private void buildModel(Job job) {
		if (job.getDetail() != null) {
			job.setDetailXml(new XStream().toXML(job.getDetail()));
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Job> getJobs() {
		List<Job> jobs = (List<Job>) getHibernateTemplate().find("from Job");
		if (jobs != null && !jobs.isEmpty()) {
			for (Job job : jobs) {
				buildJob(job);
			}
			return jobs;
		}
		else {
			return new ArrayList<Job>();
		}
	}

	public void save(Job job) {
		buildModel(job);
		getHibernateTemplate().save(job);
	}

	public void delete(Job job) {
		getHibernateTemplate().delete(job);
	}

	public void edit(Job job) {
		buildModel(job);
		getHibernateTemplate().update(job);
	}

//	@SuppressWarnings("unchecked")
//	public List<Job> getJobsToRun() {
//		List<Job> jobsToRun = new ArrayList<Job>();
//		calendar.setTime(new Date());
//		List<Job> jobs = (List<Job>) getHibernateTemplate().find("from Job where lastStatus <>" + Job.RUNNING + " and paused=false");
//		if (jobs != null && !jobs.isEmpty()) {
//			for (Job job : jobs) {
//				if(job.getBeginDate().before(calendar.getTime()) && (job.getEndDate() == null || job.getEndDate().after(calendar.getTime()))) {
//					buildJob(job);
//					jobsToRun.add(job);
//				}
//			}
//			return jobsToRun;
//		}
//		else {
//			return jobsToRun;
//		}
//	}
//
//	public void setCalendar(Calendar calendar) {
//		this.calendar = calendar;
//	}
}
