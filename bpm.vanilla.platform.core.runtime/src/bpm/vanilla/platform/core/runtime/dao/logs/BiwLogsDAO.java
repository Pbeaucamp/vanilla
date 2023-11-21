package bpm.vanilla.platform.core.runtime.dao.logs;


import java.util.List;

import bpm.vanilla.platform.core.beans.BiwLogs;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;



public class BiwLogsDAO extends HibernateDaoSupport {
	
	public void update(BiwLogs d) {
		getHibernateTemplate().update(d);
	}
	
	public List<BiwLogs> findAll() {
		return getHibernateTemplate().find("from BiwLogs");
	}

	public int save(BiwLogs d) {
		return (Integer)getHibernateTemplate().save(d);
	}
	public BiwLogs findByPrimaryKey(int key) {
		List<BiwLogs> c = getHibernateTemplate().find("from BiwLogs d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
}
