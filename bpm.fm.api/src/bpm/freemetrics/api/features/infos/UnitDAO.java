package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UnitDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Unit> findAll() {
		return getHibernateTemplate().find("from Unit");
	}
	
	
	@SuppressWarnings("unchecked")
	public Unit findByPrimaryKey(int key) {
		List<Unit> c = getHibernateTemplate().find("from Unit d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Unit findForName(String name){
		List<Unit> c = getHibernateTemplate().find("from Unit d where d.name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	public int save(Unit d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Unit");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(Unit d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Unit d) {
		getHibernateTemplate().update(d);
	}	

}
