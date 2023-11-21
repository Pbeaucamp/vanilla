package bpm.freemetrics.api.features.favorites;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserPreferenceDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<UserPreference> findAll() {
		return getHibernateTemplate().find("from UserPreference");
	}

	@SuppressWarnings("unchecked")
	public UserPreference findByPrimaryKey(int key) {
		List<UserPreference> c = getHibernateTemplate().find("from UserPreference d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(UserPreference d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from UserPreference");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(UserPreference d) {
		getHibernateTemplate().delete(d);
	}
	public void update(UserPreference d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<UserPreference> getUserPreferenceByUserId(int userId) {
		return getHibernateTemplate().find("from UserPreference d where d.mpUserId=" +  userId);
	}	

}
