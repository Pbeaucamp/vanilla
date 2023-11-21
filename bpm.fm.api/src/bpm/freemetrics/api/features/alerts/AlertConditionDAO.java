package bpm.freemetrics.api.features.alerts;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class AlertConditionDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<AlertCondition> findAll() {
		return getHibernateTemplate().find("from AlertCondition");
	}

	@SuppressWarnings("unchecked")
	public AlertCondition findByPrimaryKey(int key) {
		List<AlertCondition> c = getHibernateTemplate().find("from AlertCondition d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(AlertCondition d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from AlertCondition");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(AlertCondition d) {
		getHibernateTemplate().delete(d);
	}

	public void update(AlertCondition d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public AlertCondition getAlertConditionForMWAId(int mwalId) {
		List<AlertCondition> c = getHibernateTemplate().find("from AlertCondition d where d.acMetricswithalertId=" +  mwalId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}	

}
