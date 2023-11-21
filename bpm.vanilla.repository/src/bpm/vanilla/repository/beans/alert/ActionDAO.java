package bpm.vanilla.repository.beans.alert;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ActionDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public Collection<Action> findAll() {
		return getHibernateTemplate().find("from Action");
	}

	public Action findByPrimaryKey(int key) throws Exception {
		return (Action) getHibernateTemplate().find("from Action where id=" + key);
	}

	@SuppressWarnings("unchecked")
	public Action findByAlertId(int alertId) {
		List l = (List<Action>) getHibernateTemplate().find("from Action where alertId=" + alertId);
		if (l.isEmpty()) {
			return null;
		}
		return (Action) l.get(0);
	}

	public int save(Action d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Action d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Action d) {
		getHibernateTemplate().update(d);
	}

}
