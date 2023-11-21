package bpm.freemetrics.api.organisation.relations.appl_metric;

import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValuesManager;

public class Assoc_Application_MetricManager {
	private Assoc_Application_MetricDAO dao;

	public Assoc_Application_MetricManager() {
		super();
	}


	public void setDao(Assoc_Application_MetricDAO d) {
		this.dao = d;
	}

	public Assoc_Application_MetricDAO getDao() {
		return dao;
	}

	public List<Assoc_Application_Metric> getAssoc_Territoire_Metrics() {
		return dao.findAll();
	}

	public Assoc_Application_Metric getAssoc_Territoire_MetricById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addAssoc_Territoire_Metric(Assoc_Application_Metric d) throws Exception{
//		if(getAssocForMetrIdAndAppId(d.getMetr_ID(), d.getApp_ID()) != null || getAssoc_Territoire_MetricByName(d.getName()) != null){
//			throw new Exception("This Assoc_Application_Metric already exist");
//		}else{
			StringBuilder name = new StringBuilder();
			name.append(d.getMetric().getName());
			for(Application a : d.getApplications()) {
				name.append("_"+a.getName());
			}
			d.setName(name.toString());
			return dao.save(d);
//		}
	}

	public boolean delAssocTerritoireMetric(Assoc_Application_Metric d, MetricValuesManager vmgr) {
		dao.delete(d);
		vmgr.deleteValueByAssocId(d.getId()); 
		return true;
	}

	public void updateAssoc_Territoire_Metric(Assoc_Application_Metric d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This association doesnt exists");
		}
	}


	public Assoc_Application_Metric getAssoc_Territoire_MetricByName(String name) {
		return !dao.findForName(name).isEmpty() ? dao.findForName(name).get(0) : null;
	}


	public List<Integer> getApplicationIdsForMetricId(int id) {
		return dao.getApplicationIdsForMetricId(id);
	}

	public List<Assoc_Application_Metric> getAssoc_Application_MetricByAppId(
			int applId) {
		return dao.getAssoc_Application_MetricByAppId(applId);
	}

	public List<Assoc_Application_Metric> getAssoc_Application_MetricByMetrId(
			int metricId) {
		return dao.getAssoc_Application_MetricByMetrId(metricId);
	}

//	private List<Integer> getMetricsIdsForApplicationId(int id) {
//		return dao.getMetricsIdsForApplicationId(id);
//	}
	
	public List<Metric> getMetricsForApplicationId(int id) {
		return dao.getMetricsForApplicationId(id);
	}


	public boolean isExistAssociation(int metrId, int appId) {
		return getAssocIdForMetrIdAndAppId(metrId,appId) > 0;
	}

	public int getAssocIdForMetrIdAndAppId(int metrId, int appId) {
		return dao.getAssocIdForMetrIdAndAppId(metrId, appId);
	}

	public void deleteAssocForAppId(int applId, MetricValuesManager vmgr) {

		for (Assoc_Application_Metric d  : getAssoc_Application_MetricByAppId(applId)) {
			delAssocTerritoireMetric(d, vmgr);
		}
	}

	public void deleteAssocForMetrId(int metrId, MetricValuesManager vmgr) {
		for (Assoc_Application_Metric d  : getAssoc_Application_MetricByMetrId(metrId)) {
			delAssocTerritoireMetric(d, vmgr);
		}
	}


	public Assoc_Application_Metric getAssocForMetrIdAndAppId(int metrId, int appId) {
		return dao.getAssocForMetrIdAndAppId( metrId, appId) ;
	}


	public Assoc_Application_Metric getAssocForId(int assocId) {
		return dao.getAssocForId(assocId);
	}


	public List<Assoc_Application_Metric> getAssociationsIdForMetricsApplications(List<Integer> metricsId, List<Integer> applicationsId) {
		return dao.getAssociationsIdForMetricsApplications(metricsId, applicationsId);
	}


	public Assoc_Application_Metric getAssoMetricAppByMetricAndAppIds(int appId, int metId) {
		return dao.getAssoMetricAppByMetricAndAppIds(appId, metId);
	}
	
	public List<Assoc_Application_Metric> getAssoMetricByMetricsIds(List<Integer> metricsIds) {
		return dao.getAssoMetricAppByMetricIds(metricsIds);
	}
}
