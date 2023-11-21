package bpm.freemetrics.api.organisation.relations.dash_metric;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmDashboard_MetricDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<FmDashboard_Metric> findAll() {
		return getHibernateTemplate().find("from FmDashboard_Metric");
	}
	
	@SuppressWarnings("unchecked")
	public FmDashboard_Metric findByPrimaryKey(int key) {
		List<FmDashboard_Metric> c = getHibernateTemplate().find("from FmDashboard_Metric t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	public void save(FmDashboard_Metric d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
	}

	public void delete(FmDashboard_Metric d) {
		getHibernateTemplate().delete(d);
	}
	public void update(FmDashboard_Metric d) {
		getHibernateTemplate().update(d);
	}

}
