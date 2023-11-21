package bpm.freemetrics.api.features.alerts;

import java.util.List;

public class AlertManager {
	private AlertDAO dao;

	public AlertManager() {
		super();
	}

	public void setDao(AlertDAO d) {
		this.dao = d;
	}

	public AlertDAO getDao() {
		return dao;
	}

	public List<Alert> getAlerts() {
		return dao.findAll();
	}

	public Alert getAlertById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addAlert(Alert d) throws Exception{

		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Alert name is already used");
		}

	}

	public boolean delAlert(Alert d) {
		return dao.delete(d);
	}

	public void updateAlert(Alert d) throws Exception {

		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
		}else{
			throw new Exception("This Alert doesnt exists");
		}

	}

	public List<Alert> getAlertForUserId(int userId) {
		return dao.getAlertForUserId(userId);
	}

	public List<Alert> getAlertsForActionId(int actionId) {
		return dao.getAlertsForActionId(actionId);

	}

	public List<Alert> getAlertForGroupId(int gpId) {
		return dao.getAlertForGroupId(gpId);
	}

}
