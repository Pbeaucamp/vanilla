package bpm.freemetrics.api.features.alerts;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetricWithAlertDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<MetricWithAlert> findAll() {
		return getHibernateTemplate().find("from MetricWithAlert");
	}


	@SuppressWarnings("unchecked")
	public MetricWithAlert findByPrimaryKey(int key) {
		List<MetricWithAlert> c = getHibernateTemplate().find("from MetricWithAlert d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(MetricWithAlert d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from MetricWithAlert");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(MetricWithAlert d) {
		getHibernateTemplate().delete(d);
	}
	public void update(MetricWithAlert d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<MetricWithAlert> getAlertsForMetricAndAppId(int metricId,
			int appId) {
		return getHibernateTemplate().find("from MetricWithAlert d where d.maApplicationId= "+appId+" AND d.maMetricsId="+ metricId);
	}


	@SuppressWarnings("unchecked")
	public MetricWithAlert getMetricWithAlertByIds(int appID, int metricID,
			int alertID) {

		List<MetricWithAlert> c = getHibernateTemplate().find("from MetricWithAlert d where d.maAlertId=" +  
				alertID+" AND d.maApplicationId= "+appID+" AND d.maMetricsId="+ metricID);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<MetricWithAlert> getMetricWithAlertForAlertId(int alId) {
		
		return getHibernateTemplate().find("from MetricWithAlert d where d.maAlertId= "+alId);
	}	

	
	@SuppressWarnings("unchecked")
	public MetricWithAlert findForName(String name){
		List<MetricWithAlert> c = getHibernateTemplate().find("from MetricWithAlert where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<MetricWithAlert> getAlertsForMetricId(int metricID) {
		return getHibernateTemplate().find("from MetricWithAlert d where d.maMetricsId= "+metricID);
	}
}
