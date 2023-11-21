package bpm.freemetrics.api.organisation.application;

import java.util.List;

public class AssocApplicationAssocAppMetricManager {

	private AssocApplicationAssocAppMetricDAO dao;
	
	public AssocApplicationAssocAppMetricManager(){}

	public void setDao(AssocApplicationAssocAppMetricDAO dao) {
		this.dao = dao;
	}

	public AssocApplicationAssocAppMetricDAO getDao() {
		return dao;
	}
	
	public List<AssocApplicationAssocAppMetric> findAll() {
		return dao.findAll();
	}
	
	public AssocApplicationAssocAppMetric findById(int id) {
		return dao.findByPrimaryKey(id);
	}
	
	public void delete(AssocApplicationAssocAppMetric a) {
		dao.delete(a);
	}
	
	public int save(AssocApplicationAssocAppMetric a) {
		return dao.save(a);
	}
	
	public void update(AssocApplicationAssocAppMetric a) {
		dao.update(a);
	}

	public List<AssocApplicationAssocAppMetric> getByAssoId(int id) {
		return dao.getByAssoId(id);
	}

	public void deleteForAppId(int id) {
		dao.deleteForAppId(id);
	}
	
	public List<AssocApplicationAssocAppMetric> findByAssoId(int id) {
		return dao.findByAssoId(id);
	}

	public List<AssocApplicationAssocAppMetric> getByApplicationId(int appId) {
		return dao.getByApplicationId(appId);
	}
}
