package bpm.freemetrics.api.features.favorites;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserActionDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<UserAction> findAll() {
		return getHibernateTemplate().find("from UserAction");
	}
	
	
	@SuppressWarnings("unchecked")
	public UserAction findByPrimaryKey(int key) {
		List<UserAction> c = getHibernateTemplate().find("from UserAction d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(UserAction d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from UserAction");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(UserAction d) {
		getHibernateTemplate().delete(d);
	}
	public void update(UserAction d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<UserAction> getUserActionByUserId(int userId) {
		return getHibernateTemplate().find("from UserAction d where d.moUserId="+userId);
	}	

}
