package bpm.freemetrics.api.organisation.application;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class TypeTerritoireDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<TypeTerritoire> findAll() {
		return getHibernateTemplate().find("from TypeTerritoire");
	}
	
	@SuppressWarnings("unchecked")
	public TypeTerritoire findByPrimaryKey(int key) {
		List<TypeTerritoire> c = getHibernateTemplate().find("from TypeTerritoire t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(TypeTerritoire d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from TypeTerritoire");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(TypeTerritoire d) {
		getHibernateTemplate().delete(d);
	}
	public void update(TypeTerritoire d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public TypeTerritoire findForName(String name){
		List<TypeTerritoire> c = getHibernateTemplate().find("from TypeTerritoire where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

}
