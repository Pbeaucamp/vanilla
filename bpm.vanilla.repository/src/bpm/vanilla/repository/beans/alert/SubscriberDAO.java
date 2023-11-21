package bpm.vanilla.repository.beans.alert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SubscriberDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public Collection<Subscriber> findAll() {
		return getHibernateTemplate().find("from Subscriber");
	}

	public Subscriber findByPrimaryKey(int key) throws Exception {
		return (Subscriber) getHibernateTemplate().find("from Subscriber where id=" + key);
	}

	@SuppressWarnings("unchecked")
	public List<Subscriber> findByAlertId(int alertId) {
		List<Subscriber> l = (List<Subscriber>) getHibernateTemplate().find("from Subscriber where alertId=" + alertId);
		if (l.isEmpty()) {
			return null;
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	public Subscriber findByAlertAndUserId(int alertId, int userId) {
		List<Subscriber> l = (List<Subscriber>) getHibernateTemplate().find("from Subscriber where alertId=" + alertId + " AND userId=" + userId);
		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Subscriber> findByGroupId(int groupId) {
		List<Subscriber> l = (List<Subscriber>) getHibernateTemplate().find("from Subscriber where groupId=" + groupId);
		if (l != null) {
			return l;
		}
		return new ArrayList<Subscriber>();
	}

	public int save(Subscriber d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Subscriber d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Subscriber d) {
		getHibernateTemplate().update(d);
	}

}
