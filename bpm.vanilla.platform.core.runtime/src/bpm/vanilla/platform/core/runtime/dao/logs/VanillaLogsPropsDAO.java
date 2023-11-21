package bpm.vanilla.platform.core.runtime.dao.logs;


import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogsProps;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VanillaLogsPropsDAO extends HibernateDaoSupport {
	
	public void update(VanillaLogsProps d) {
		getHibernateTemplate().update(d);
	}
	
	public List<VanillaLogsProps> findAll() {
		return getHibernateTemplate().find("from VanillaLogsProps");
	}

	public int save(VanillaLogsProps d) {
		return (Integer)getHibernateTemplate().save(d);
	}
	
	public List<VanillaLogsProps> findByVanillaLogId(int key) {
		List<VanillaLogsProps> c = getHibernateTemplate().find("from VanillaLogsProps d where d.vlogId=" +  key);
		return c;
	}
	
	public VanillaLogsProps findByPrimaryKey(int key) {
		List<VanillaLogsProps> c = getHibernateTemplate().find("from VanillaLogsProps d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
}
