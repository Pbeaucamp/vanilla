package bpm.freemetrics.api.organisation.metrics;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetricValueListDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<MetricValueList> findAll() {
		return getHibernateTemplate().find("FROM MetricValueList");
	}
	
	
	@SuppressWarnings("unchecked")
	public MetricValueList findByPrimaryKey(int key) {
		List<MetricValueList> c = getHibernateTemplate().find("FROM MetricValueList d WHERE d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	public int save(MetricValueList d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public boolean delete(MetricValueList d) {
		getHibernateTemplate().delete(d);
		
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(MetricValueList d) {
		getHibernateTemplate().update(d);
	}

	public List<MetricValueList> getMetricValueListForAppIdAndMetrId(int appId,
			int metrId) {
		
		return getHibernateTemplate().find("FROM MetricValueList d WHERE d.applicationId="+ appId+" AND d.metricId="+metrId);
	}


	public List<MetricValueList> getValueListForMetricId(int metricId) {
		
		return getHibernateTemplate().find("FROM MetricValueList d WHERE d.metricId=" +  metricId);
	}	

}
