package bpm.freemetrics.api.features.alerts;

import java.util.List;

public class AlertConditionManager {
	private AlertConditionDAO dao;

	public AlertConditionManager() {
		super();
	}

	public void setDao(AlertConditionDAO d) {
		this.dao = d;
	}

	public AlertConditionDAO getDao() {
		return dao;
	}

	public List<AlertCondition> getAlertConditions() {
		return dao.findAll();
	}

	public AlertCondition getAlertConditionById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public void addAlertCondition(AlertCondition d) throws Exception{
		dao.save(d);
	}

	public void delAlertCondition(AlertCondition d) {
		dao.delete(d);
	}

	public boolean updateAlertCondition(AlertCondition d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This AlertCondition doesnt exists");
		}
	}

	public AlertCondition getAlertConditionForMWAId(int mwalId) {

		return dao.getAlertConditionForMWAId(mwalId);
	}

}
