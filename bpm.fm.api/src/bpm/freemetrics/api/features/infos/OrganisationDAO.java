package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class OrganisationDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public List<Organisation> findAll() {
		return getHibernateTemplate().find("from Organisation");
	}
	
	
	@SuppressWarnings("unchecked")
	public Organisation findByPrimaryKey(int key) {
		List<Organisation> c = getHibernateTemplate().find("from Organisation d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(Organisation d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Organisation");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(Organisation d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId())== null;
	}
	public void update(Organisation d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public Organisation findForName(String name){
		List<Organisation> c = getHibernateTemplate().find("from Organisation where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


}
