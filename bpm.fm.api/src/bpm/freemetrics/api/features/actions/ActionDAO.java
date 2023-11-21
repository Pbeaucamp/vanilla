package bpm.freemetrics.api.features.actions;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ActionDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Action> findAll() {
		return getHibernateTemplate().find("from Action");
	}
	
	
	@SuppressWarnings("unchecked")
	public Action findByPrimaryKey(int key) {
		List<Action> c = getHibernateTemplate().find("from Action d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(Action d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Action");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(Action d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Action d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public Action getActionByName(String name) {
		List<Action> c = getHibernateTemplate().find("from Action d where d.name='"+  name.replace("'", "''") +"' ");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}	

}
