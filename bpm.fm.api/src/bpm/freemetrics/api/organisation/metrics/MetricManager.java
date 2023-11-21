package bpm.freemetrics.api.organisation.metrics;

import java.util.ArrayList;
import java.util.List;

import bpm.freemetrics.api.organisation.observatoire.ObservatoireManager;

public class MetricManager {
	private MetricDAO dao;

	public MetricManager() {
		super();
	}

	public void setDao(MetricDAO d) {
		this.dao = d;
	}

	public MetricDAO getDao() {
		return dao;
	}

	public List<Metric> getMetrics() {
		return dao.findAll();
	}

	public Metric getMetricById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addMetric(Metric d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Metric name is already used");
		}		 
	}

	public void delMetric(Metric d) {
		dao.delete(d);
	}

	public boolean updateMetric(Metric d) throws Exception {

		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
			
			for(Metric m : d.getChildren()) {
				MetricComponent component = new MetricComponent();
				component.setChildId(m.getId());
				component.setParentId(d.getId());
				dao.getHibernateTemplate().save(component);
			}
			
			d.setChildren(getChildren(d.getId()));
			
			return true;
		}
		else{
			throw new Exception("This Metric doesnt exists");
		}

		
		
	}

	public List<Metric> getMetricsForGroupId(int grpid) {
		return dao.getMetricsForGroupId(grpid);
	}

	public List<Metric> getMetricsForTypeId(int typeId) {
		return dao.getMetricsForTypeId(typeId);
	}

	public List<Metric> getMetricsForOwnerId(int userid) {
		return dao.getMetricsForOwnerId(userid);
	}

	public boolean deleteMetricById(int metid) {
		Metric metric = getMetricById(metid);
		if(metric != null ){
			delMetric(metric);
		}

		return getMetricById(metid) == null;
	}

	public Metric getMetricByName(String name) {

		return dao.findForName(name);
	}

	public List<Metric> getCompteurs() {
		return dao.getCompteurs();
	}

	public List<Metric> getIndicateurs() {
		return dao.getIndicateurs();
	}

	public List<Metric> getMetricsForType(String calculationType) {
		return dao.getMetricsForType(calculationType);
	}

	public List<Metric> getMetricsByThemeIdAndPeriode(int themeId, String periode) {
		return dao.getMetricsByThemeIdAndPeriode(themeId,periode);
	}

	public List<Metric> getCompteursByObservatoires(String observatoire) {
		return dao.getCompteursByObservatoires(observatoire);
	}

	public List<Metric> getIndicateursByObservatoires(String observatoire) {
		return dao.getIndicateursByObservatoires(observatoire);
	}

	public List<Metric> getNotAssociatedIndicateur(Metric metric) {
		return dao.getNotAssociatedIndicateur(metric);
	}

	public List<Metric> getAssciatedIndicateur(int metricId) {
		return dao.getAssciatedIndicateur(metricId);
	}

	public List<Metric> getNotAssociatedCompteur(Metric metric, int observatoireId) {
		return dao.getNotAssociatedCompteur(metric, observatoireId);
	}

	public List<Metric> getAssciatedCompteur(int metricId) {
		return dao.getAssciatedCompteur(metricId);
	}

	public List<Metric> getCompteursByThemeIdAndPeriode(int themeId, String periode) {
		return dao.getCompteursByThemeAndPeriode(themeId, periode);
	}

	public List<Metric> getIndicateursByThemeIdAndPeriode(int themeId, String periode) {
		return dao.getIndicateursByThemeAndPeriode(themeId, periode);
	}

	public Metric getMetricByIdAssoId(Integer mvGlAssocId) {
		return dao.getMetricByIdAssoId(mvGlAssocId);
	}

	public List<Metric> getMetricsByTheme(String t) {
		return dao.getMetricsByTheme(t);
	}
	
	public List<Metric> getMetricsBySubThemeId(int subThemeId) {
		return dao.getMetricsBySubThemeId(subThemeId);
	}

	public List<Metric> getMetricsByThemeId(int themeId) {
		return dao.getMetricsByThemeId(themeId);
	}

	private List<MetricComponent> getSubMetrics(int metricId) {
		return dao.getSubMetrics(metricId);
	}
	
	public List<Metric> getChildren(int metricId) {
		List<Metric> res = new ArrayList<Metric>();
		for(MetricComponent comp : getSubMetrics(metricId)) {
			res.add(dao.findByPrimaryKey(comp.getChildId()));
		}
		
		return res;
	}

	public void deleteChild(Metric m, int metricId) {
		List<MetricComponent> lst = dao.getHibernateTemplate().find("From MetricComponent c where c.childId = " + m.getId() + " and c.parentId = " + metricId);
		MetricComponent comp = lst.get(0);
		dao.getHibernateTemplate().delete(comp);
	}

}
