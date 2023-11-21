package bpm.freemetrics.api.features.alerts;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class AlertDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Alert> findAll() {
		return getHibernateTemplate().find("from Alert");
	}


	@SuppressWarnings("unchecked")
	public Alert findByPrimaryKey(int key) {
		List<Alert> c = getHibernateTemplate().find("from Alert d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Alert d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Alert");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(Alert d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(Alert d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<Alert> getAlertForUserId(int userId) {
		return getHibernateTemplate().find("from Alert d where d.alUserId=" +  userId);
	}


	@SuppressWarnings("unchecked")
	public List<Alert> getAlertsForActionId(int actionId) {
		return getHibernateTemplate().find("from Alert d where d.alActionId=" +  actionId);
	}

	@SuppressWarnings("unchecked")
	public Alert findForName(String name){
		List<Alert> c = getHibernateTemplate().find("from Alert where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<Alert> getAlertForGroupId(int gpId) {
		return getHibernateTemplate().find("from Alert d where d.alGroupId=" +  gpId);
	}

}
