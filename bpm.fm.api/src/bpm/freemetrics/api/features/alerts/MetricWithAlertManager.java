package bpm.freemetrics.api.features.alerts;

import java.util.List;

public class MetricWithAlertManager {
	private MetricWithAlertDAO dao;

	public MetricWithAlertManager() {
		super();
	}

	public void setDao(MetricWithAlertDAO d) {
		this.dao = d;
	}

	public MetricWithAlertDAO getDao() {
		return dao;
	}

	public List<MetricWithAlert> getMetricWithAlerts() {
		return dao.findAll();
	}

	public MetricWithAlert getMetricWithAlertById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addMetricWithAlert(MetricWithAlert d) throws Exception{

		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This MetricWithAlert name is already used");
		}
	}

	public void delMetricWithAlert(MetricWithAlert d) {
		dao.delete(d);
	}

	public boolean updateMetricWithAlert(MetricWithAlert d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This MetricWithAlert doesnt exists");
		}
	}

	public List<MetricWithAlert> getAlertsForMetricId(int metricID) {
		return dao.getAlertsForMetricId(metricID);
	}

	public MetricWithAlert getMetricWithAlertById(int appID, int metricID,
			int alertID) {
		return dao.getMetricWithAlertByIds(appID, metricID,alertID);
	}

	public List<MetricWithAlert> getAlertsForMetricAndAppId(int metricId,
			int appId) {
		return dao.getAlertsForMetricAndAppId(metricId,appId);
	}

	public List<MetricWithAlert> getMetricWithAlertForAlertId(int alId) {
		return dao.getMetricWithAlertForAlertId(alId);
	}

}
