package bpm.freemetrics.api.security;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmRoleDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<FmRole> findAll() {
		return getHibernateTemplate().find("from FmRole");
	}
	
	
	@SuppressWarnings("unchecked")
	public FmRole findByPrimaryKey(int key) {
		List<FmRole> c = getHibernateTemplate().find("from FmRole d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(FmRole d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FmRole");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(FmRole d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(FmRole d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public FmRole findForName(String name){
		List<FmRole> c  = getHibernateTemplate().find("from FmRole where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}


}
