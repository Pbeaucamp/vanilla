package bpm.freemetrics.api.organisation.metrics;

import java.util.Date;
import java.util.List;

public class MetricValuesManager {
	private MetricValuesDAO dao;

	public MetricValuesManager() {
		super();
	}

	public void setDao(MetricValuesDAO d) {
		this.dao = d;
	}

	public MetricValuesDAO getDao() {
		return dao;
	}

	public List<MetricValues> getMetricValuess() {
		return dao.findAll();
	}

	public MetricValues getMetricValuesById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addMetricValues(MetricValues d) throws Exception{

		int valId = 0;
		if (dao.findByPrimaryKey(d.getId()) == null){

			MetricValues prevVal = dao.getPreviousMetricsValueFor(d);

			d.setMvPreviousMetricsValueId(prevVal != null ? prevVal.getId() : 0);

//			int dx = 0;
//
//			if(prevVal != null && prevVal.getMvValue() != null && d.getMvValue() != null)
//				dx = Float.compare(d.getMvValue().floatValue(),prevVal.getMvValue().floatValue());
//
//			String mvTrendDate = "STABL";
//
//			if(dx > 0)
//				mvTrendDate = "UP";
//			else if(dx < 0)
//				mvTrendDate = "DOWN";
//
//			d.setMvTrendDate(mvTrendDate);

			valId = dao.save(d);

		}else{
			throw new Exception("This MetricValues is already exists");
		}

		return valId;

	}

	public void delMetricValues(MetricValues d) {
		dao.delete(d);
	}

	public void updateMetricValues(MetricValues d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This MetricValues doesnt exists");
		}

	}

	public List<MetricValues> getMetricValuesByAssocId(int id) {
		return dao.getMetricValuesByAssocId(id) ;
	}

	public boolean deleteById(int valueID) {

		dao.delete(getMetricValuesById(valueID));

		return getMetricValuesById(valueID) == null;
	}

	public MetricValues getLastValueForAssocId(Integer assocId) {
		return dao.getLastValueForAssocId(assocId);
	}

	public void deleteValueByAssocId(int assid) {
		for (MetricValues d : getMetricValuesByAssocId(assid)) {
			dao.delete(d);
		}
	}

	public List<MetricValues> getValuesForAssocIdAndDates(int id, Date dateFrom, Date dateTo) {
		return dao.getValuesForAssocIdAndDates(id, dateFrom, dateTo);
	}

	public MetricValues getPreviousValue(MetricValues val) {
		return dao.getPreviousMetricsValueFor(val);
	}

	public List<MetricValues> getValuesForAssocId(List<Integer> assoIds) {
		return dao.getValuesForAssocId(assoIds);
	}

	public void deleteValues(List<MetricValues> deleteValues) {
		dao.deleteValues(deleteValues);
	}

	public void insertValues(List<MetricValues> insertValues) {
		dao.insertValues(insertValues);	
	}

	public List<MetricValues> getMetricsValuesForAssoIdPeriodeDate(List<Integer> assoIds, String periode, Date date) {
		return dao.getMetricsValuesForAssoIdPeriodeDate(assoIds,periode,date);
	}

	public Date getPreviousDateByAssoId(int assoId) {
		return dao.getPreviousDateByAssoId(assoId);
	}

	public MetricValues getLastObjectifFor(int objeAssocId) {
		return dao.getLastObjectifFor(objeAssocId);
	}

	public List<MetricValues> getMetricValuesByAssocId(int id, Date startDate, Date endDate, String datePattern) {
		return dao.getMetricValuesByAssocId(id, startDate, endDate, datePattern);
	}

}
