package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class LogDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Log> findAll() {
		return getHibernateTemplate().find("from Log");
	}
	
	
	@SuppressWarnings("unchecked")
	public Log findByPrimaryKey(int key) {
		List<Log> c = getHibernateTemplate().find("from Log d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	public int save(Log d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		
		return id;
	}

	public void delete(Log d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Log d) {
		getHibernateTemplate().update(d);
	}	

}
