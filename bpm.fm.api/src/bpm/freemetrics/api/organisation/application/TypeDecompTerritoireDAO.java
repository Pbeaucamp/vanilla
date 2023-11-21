package bpm.freemetrics.api.organisation.application;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class TypeDecompTerritoireDAO extends HibernateDaoSupport {

	
	@SuppressWarnings("unchecked")
	public List<TypeDecompTerritoire> findAll() {
		return getHibernateTemplate().find("from TypeDecompTerritoire where name <> '' ");
	}
	
	@SuppressWarnings("unchecked")
	public TypeDecompTerritoire findByPrimaryKey(int key) {
		getHibernateTemplate().flush();
		List<TypeDecompTerritoire> c = getHibernateTemplate().find("from TypeDecompTerritoire t where name <> '' AND t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public int save(TypeDecompTerritoire d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from TypeDecompTerritoire");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(TypeDecompTerritoire d) {
		getHibernateTemplate().delete(d);

		return findByPrimaryKey(d.getId())== null;
	}
	public void update(TypeDecompTerritoire d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public TypeDecompTerritoire findForName(String name){
		List<TypeDecompTerritoire> c = getHibernateTemplate().find("from TypeDecompTerritoire where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


}
