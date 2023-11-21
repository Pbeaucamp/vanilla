package bpm.freemetrics.api.organisation.relations.appl_metric;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class Assoc_Application_MetricDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Assoc_Application_Metric> findAll() {
		return getHibernateTemplate().find("from Assoc_Application_Metric");
	}

	@SuppressWarnings("unchecked")
	public Assoc_Application_Metric findByPrimaryKey(int key) {
		List<Assoc_Application_Metric> c = getHibernateTemplate().find("from Assoc_Application_Metric t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Assoc_Application_Metric d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Assoc_Application_Metric");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		
		return id;
	}

	public boolean delete(Assoc_Application_Metric d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(Assoc_Application_Metric d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<Assoc_Application_Metric> findForName(String name){
		return getHibernateTemplate().find("from Assoc_Application_Metric where name='" + name.replace("'", "''") + "'");
	}

	@SuppressWarnings("unchecked")
	public int getAssocIdForMetrIdAndAppId(int metrId, int appId) {

		List<Assoc_Application_Metric> c = getHibernateTemplate().find("from Assoc_Application_Metric t where t.app_ID="+appId+" AND t.metr_ID = "+metrId);
		if (c != null && c.size() > 0){
			return c.get(0).getId();
		}else{
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public Assoc_Application_Metric getAssocForMetrIdAndAppId(int metrId,int appId) {

		List<Assoc_Application_Metric> c = getHibernateTemplate().find("from Assoc_Application_Metric t where t.app_ID="+appId+" AND t.metr_ID = "+metrId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getMetricsIdsForApplicationId(int id) {
		List<Integer> res = new ArrayList<Integer>();

		List<Assoc_Application_Metric> c = getHibernateTemplate().find("from Assoc_Application_Metric t where t.app_ID="+id);
		for(Assoc_Application_Metric assoc : c){
			res.add(assoc.getMetr_ID());
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	public List<Assoc_Application_Metric> getAssoc_Application_MetricByMetrId(int metricId) {
		return getHibernateTemplate().find("from Assoc_Application_Metric t where t.metr_ID = "+metricId);
	}

	@SuppressWarnings("unchecked")
	public List<Assoc_Application_Metric> getAssoc_Application_MetricByAppId(int applId) {
		return getHibernateTemplate().find("from Assoc_Application_Metric t where t.app_ID="+applId);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getApplicationIdsForMetricId(int id) {
		List<Integer> res = new ArrayList<Integer>();
		List<Assoc_Application_Metric> c = getHibernateTemplate().find("from Assoc_Application_Metric t where t.metr_ID = "+id);
		for(Assoc_Application_Metric assoc : c){
			res.add(assoc.getApp_ID());
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsForApplicationId(int applId) {
		String query = "SELECT m FROM Metric m, Assoc_Application_Metric t WHERE m.id = t.metr_ID AND  t.app_ID="+applId;
		
		return getHibernateTemplate().find(query);
	}

	public Assoc_Application_Metric getAssocForId(int assocId) {
		List<Assoc_Application_Metric> l = getHibernateTemplate().find("from Assoc_Application_Metric t WHERE t.id=" + assocId);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}

	public List<Assoc_Application_Metric> getAssociationsIdForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId) {
		if(metricsId.size() != 0 && applicationsId.size() != 0) {
			String paramsMet = "";
			int first = metricsId.get(0);
			for(int id : metricsId) {
				if(id == first) {
					paramsMet += "'" + id + "'";
				}
				else {
					paramsMet += ",'" + id + "'";
				}
			}	
			
			String paramsApp = "";
			int firstApp = applicationsId.get(0);
			for(int id : applicationsId) {
				if(id == firstApp) {
					paramsApp += "'" + id + "'";
				}
				else {
					paramsApp += ",'" + id + "'";
				}
			}	
			return getHibernateTemplate().find("from Assoc_Application_Metric a where a.metr_ID in (" + paramsMet + ") AND a.app_ID in (" + paramsApp + ")");	
		}
		return new ArrayList<Assoc_Application_Metric>();
	}

	public Assoc_Application_Metric getAssoMetricAppByMetricAndAppIds(int appId, int metId) {
		List<Assoc_Application_Metric> assos = getHibernateTemplate().find("from Assoc_Application_Metric a where a.metr_ID = " + metId + " AND a.app_ID = " + appId + "");
		if(assos != null && assos.size() != 0) {
			return assos.get(0);
		}
		return null;
	}

	public List<Assoc_Application_Metric> getAssoMetricAppByMetricIds(List<Integer> metricsIds) {
		if(metricsIds.size() != 0) {
			String paramsMet = "";
			int first = metricsIds.get(0);
			for(int id : metricsIds) {
				if(id == first) {
					paramsMet += "'" + id + "'";
				}
				else {
					paramsMet += ",'" + id + "'";
				}
			}	
			return getHibernateTemplate().find("from Assoc_Application_Metric a where a.metr_ID in (" + paramsMet + ")");// AND a.app_ID in (" + paramsApp + ")");	
		}
		return new ArrayList<Assoc_Application_Metric>();
	}
}
