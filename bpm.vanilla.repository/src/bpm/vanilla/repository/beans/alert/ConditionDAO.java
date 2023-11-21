package bpm.vanilla.repository.beans.alert;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ConditionDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public Collection<Condition> findAll() {	
		return getHibernateTemplate().find("from Condition");
	}

	@SuppressWarnings("unchecked")
	public List<Condition> findByPrimaryKey(int key) throws Exception {
		return (List<Condition>) getHibernateTemplate().find("from Condition where id=" + key);
	}
	
	@SuppressWarnings("unchecked")
	public List<Condition> findByAlertId(int alertId) {
		return (List<Condition>) getHibernateTemplate().find("from Condition where alertId=" + alertId);
	}

	public int save(Condition d) {
		return (Integer)getHibernateTemplate().save(d);
	}

	public void delete(Condition d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Condition d) {
		getHibernateTemplate().update(d);
	}

}
