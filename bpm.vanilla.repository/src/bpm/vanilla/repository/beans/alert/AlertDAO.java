package bpm.vanilla.repository.beans.alert;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class AlertDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Alert> getAlertsWhitoutEvent() {
		List<Alert> alertsTmp = (List<Alert>) getHibernateTemplate().find("from Alert");
		List<Alert> alerts = new ArrayList<Alert>();
		if (alertsTmp != null) {
			for (Alert alert : alertsTmp) {
				if (alert.getEventModel() == null || alert.getEventModel().isEmpty()) {
					alerts.add(alert);
				}
			}
		}
		return alerts;
	}
	
	@SuppressWarnings("unchecked")
	public Alert findByPrimaryKey(int key) throws Exception {
		List<Alert> alerts = (List<Alert>) getHibernateTemplate().find("from Alert where id=" + key);
		if (alerts == null || alerts.isEmpty()) {
			throw new Exception("No alert with id " + key + " has been found");
		}
		return alerts.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<Alert> findByEventId(int eventId) {
//		List<Alert> alerts = (List<Alert>) getHibernateTemplate().find("from Alert where eventId=" + eventId);
//		if (alerts == null) {
//			return new ArrayList<Alert>();
//		}
//		return alerts;
		//Not used anymore
		return null;
	}

	public int save(Alert d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Alert d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Alert d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<Alert> findAll() {
		return (List<Alert>) getHibernateTemplate().find("from Alert");
	}
	
	@SuppressWarnings("unchecked")
	public List<Alert> findByTypeEvent(TypeEvent typeEvent) {
		List<Alert> l = (List<Alert>) getHibernateTemplate().find("from Alert where typeEvent='" + typeEvent +"'");
		if (l.isEmpty()) {
			return null;
		}
		return l;
	}
}
