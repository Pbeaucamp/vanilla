package bpm.vanilla.platform.core.runtime.dao.preferences;


import java.util.List;

import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class OpenPreferenceDAO extends HibernateDaoSupport {

	public List<OpenPreference> findAll() {
		return getHibernateTemplate().find("from OpenPreference");
	}

	public OpenPreference findByPrimaryKey(int key) {
		List<OpenPreference> c = getHibernateTemplate().find("from OpenPreference d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (OpenPreference)c.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<OpenPreference> findOpenPreferencesByUserId(int userId) {
		return getHibernateTemplate().find("from OpenPreference d where d.userId=" + userId);
	}
	

	public int save(OpenPreference d) {
		return (Integer)getHibernateTemplate().save(d);
	}

	public void delete(OpenPreference d) {
		List l = findForNameAndForUserId(d.getItemName(), d.getUserId());
		if (!l.isEmpty()) {
			OpenPreference o = (OpenPreference) l.get(0);
			OpenPreference up =  findByPrimaryKey(o.getId());
			up.setUserId(0);
			getHibernateTemplate().update(up);
		}
			
	}
	
	private List findForNameAndForUserId(String itemName, int userId) {
		return getHibernateTemplate().find("from OpenPreference d where d.itemName='" + itemName + "' AND userId=" + userId);
	}

	public void update(OpenPreference d) {
		getHibernateTemplate().update(d);
	}
	
	public List<OpenPreference> findForName(String name){
		return getHibernateTemplate().find("from OpenPreference d where d.itemName='" + name + "'");
	}
}

