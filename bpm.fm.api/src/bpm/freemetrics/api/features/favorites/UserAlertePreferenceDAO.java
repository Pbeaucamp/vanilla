package bpm.freemetrics.api.features.favorites;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserAlertePreferenceDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<UserAlertePreference> findAll() {
		return getHibernateTemplate().find("from UserAlertePreference");
	}


	@SuppressWarnings("unchecked")
	public UserAlertePreference findByPrimaryKey(int key) {
		List<UserAlertePreference> c = getHibernateTemplate().find("from UserAlertePreference d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(UserAlertePreference d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from UserAlertePreference");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(UserAlertePreference d) {
		getHibernateTemplate().delete(d);
	}
	public void update(UserAlertePreference d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<UserAlertePreference> getUserAlertePreferenceByUserId(int userId) {
		return getHibernateTemplate().find("from UserAlertePreference d where d.mpaUserId=" + userId);
	}	

}
