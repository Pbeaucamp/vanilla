package bpm.freemetrics.api.organisation.metrics;

import java.util.Date;
import java.util.List;

public class MetricValueListManager {
	private MetricValueListDAO dao;

	public MetricValueListManager() {
		super();
	}

	public void setDao(MetricValueListDAO d) {
		this.dao = d;
	}

	public MetricValueListDAO getDao() {
		return dao;
	}

	public List<MetricValueList> getMetricValueLists() {
		return dao.findAll();
	}

	public MetricValueList getMetricValueListById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addMetricValueList(MetricValueList d) throws Exception{
		int valId = 0;

		if (dao.findByPrimaryKey(d.getId()) == null){
			valId = dao.save(d);
		}else{
			throw new Exception("This MetricValueList is already used");
		}
		return valId;

	}

	public boolean delMetricValueList(MetricValueList d) {
		return dao.delete(d);
	}

	public boolean updateMetricValueList(MetricValueList d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This MetricValueList doesnt exists");
		}

	}

	public List<MetricValueList> getMetricValueListForAppIdAndMetrId(int appId ,int metrId) {

		return  dao.getMetricValueListForAppIdAndMetrId(appId , metrId);
	}

	public boolean deleteById(int valueID) {

		dao.delete(getMetricValueListById(valueID));

		return getMetricValueListById(valueID) == null;
	}

	public List<MetricValueList> getValueListForMetricId(int metricId) {

		return dao.getValueListForMetricId(metricId);
	}

	public int getCloserValueListIdByAppAndMetrId(int applicationId,
			int metricId, Date now) {
		int nearestId = 0;
		
		if(now == null) now = new Date();
			
		List<MetricValueList> mVall = dao.getMetricValueListForAppIdAndMetrId(applicationId, metricId);

		for (MetricValueList tarPeriod : mVall) {

			if(tarPeriod.getDateFrom() != null && tarPeriod.getDateFrom().before(now)){

				if(nearestId > 0){
					MetricValueList previous = dao.findByPrimaryKey(nearestId);
					if(previous != null){
						if(previous.getDateFrom() != null && tarPeriod.getDateFrom().after(previous.getDateFrom())){

							if(previous.getDateTo() != null && previous.getDateTo().after(now)){

								if(tarPeriod.getDateTo() != null && previous.getDateTo().after(tarPeriod.getDateTo())){
									nearestId = tarPeriod.getId();
								}else if(tarPeriod.getDateTo() == null){
									nearestId = tarPeriod.getId();
								}
							}else if(tarPeriod.getDateTo() != null && previous.getDateTo() == null && tarPeriod.getDateTo().after(now)){
								nearestId = tarPeriod.getId();
							}else{
								nearestId = tarPeriod.getId();
							}

						}else {
							nearestId = previous.getId();
						}
					}
				}else{
					nearestId = tarPeriod.getId();
				}
			}else{
				nearestId = tarPeriod.getId();
			}
		}
		return nearestId;
	}

}
