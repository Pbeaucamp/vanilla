package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DashboardDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> findAll() {
		return getHibernateTemplate().find("from Dashboard");
	}
	
	
	@SuppressWarnings("unchecked")
	public Dashboard findByPrimaryKey(int key) {
		List<Dashboard> c = getHibernateTemplate().find("from Dashboard d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	public void save(Dashboard d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
	}

	public void delete(Dashboard d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Dashboard d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public List<Dashboard> findForName(String name){
		return getHibernateTemplate().find("from Dashboard where name='" + name + "'");
	}

}
