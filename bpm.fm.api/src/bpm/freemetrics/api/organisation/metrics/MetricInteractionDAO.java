package bpm.freemetrics.api.organisation.metrics;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetricInteractionDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<MetricInteraction> findAll() {
		return getHibernateTemplate().find("from MetricInteraction");
	}


	@SuppressWarnings("unchecked")
	public MetricInteraction findByPrimaryKey(int key) {
		List<MetricInteraction> c = getHibernateTemplate().find("from MetricInteraction d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(MetricInteraction d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from MetricInteraction");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(MetricInteraction d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(MetricInteraction d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<MetricInteraction> findForName(String name){
		return getHibernateTemplate().find("from MetricInteraction where name='" + name.replace("'", "''") + "'");
	}

	@SuppressWarnings("unchecked")
	public MetricInteraction getMetricInteractionForTopId(int compt_ID) {
		List<MetricInteraction> c = getHibernateTemplate().find("from MetricInteraction d where d.miMetricTopId=" +  compt_ID);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<MetricInteraction> getMetricInteractionForDownId(int compt_ID) {
		return  getHibernateTemplate().find("from MetricInteraction d where d.miMetricDownId=" +  compt_ID);
	}
	
	@SuppressWarnings("unchecked")
	public MetricInteraction getMetricInteractionForAppId_TopAndDownId(
			int aid, int tid, int did) {
		List<MetricInteraction> c = getHibernateTemplate().find("from MetricInteraction d where " +
				"d.miMetricTopId=" +tid+" AND d.miMetricDownId = "+did+" AND d.miApplicationId = "+aid);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public List<MetricInteraction> getMetricInteractionsForMetrId(int mid) {
		return  getHibernateTemplate().find("from MetricInteraction d where d.miMetricDownId=" +  mid +" OR d.miMetricTopId=" +mid);
	}

}
