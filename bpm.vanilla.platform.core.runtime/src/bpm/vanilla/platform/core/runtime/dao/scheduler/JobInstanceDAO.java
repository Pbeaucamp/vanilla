package bpm.vanilla.platform.core.runtime.dao.scheduler;

import java.util.List;

import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class JobInstanceDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<JobInstance> findAll() {
		return (List<JobInstance>)getHibernateTemplate().find("from JobInstance");
	}

	@SuppressWarnings("unchecked")
	public JobInstance findById(int id) {
		List<JobInstance> l = getHibernateTemplate().find("from JobInstance where id=" + id);
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<JobInstance> getJobHistoric(int jobId) {
		return getHibernateTemplate().find("from JobInstance where jobId=" + jobId);
	}

	public int save(JobInstance n) {
		return (Integer) getHibernateTemplate().save(n);
	}

	public void delete(JobInstance n) {
		getHibernateTemplate().delete(n);
	}

	public void update(JobInstance run) {
		getHibernateTemplate().update(run);

	}

	public void delete(List<JobInstance> jobRuns) {
		if(jobRuns != null) {
			for(JobInstance jobRun : jobRuns) {
				delete(jobRun);
			}
		}
	}
}
