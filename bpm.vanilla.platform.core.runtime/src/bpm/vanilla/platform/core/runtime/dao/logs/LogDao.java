package bpm.vanilla.platform.core.runtime.dao.logs;


import java.util.List;

import bpm.vanilla.platform.core.beans.LogBean;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class LogDao extends HibernateDaoSupport{

	
	public long save(LogBean log){
		return (Long)getHibernateTemplate().save(log);
	}
	
	public void delete(LogBean log){
		getHibernateTemplate().delete(log);
	}
	
	public void delete(int id){
		List l = getHibernateTemplate().find("from LogBean where id=" + id);
		
		if (!l.isEmpty()){
			getHibernateTemplate().delete(l.get(0));
		}
		
	}
	
	public List<LogBean> getAll(String runtimeInstanceServerUrl){
		return getHibernateTemplate().find("from LogBean where runtimeInstanceUrl='" + runtimeInstanceServerUrl + "'");
	}
	
	
	public List<LogBean> getForRunningInstanceId(int logInstanceId){
		return getHibernateTemplate().find("from LogBean where runningInstanceId=" + logInstanceId);
	}
	
	
}
