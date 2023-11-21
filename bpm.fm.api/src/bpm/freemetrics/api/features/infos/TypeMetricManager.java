package bpm.freemetrics.api.features.infos;

import java.util.List;

public class TypeMetricManager {
	private TypeMetricDAO dao;

	public TypeMetricManager() {
		super();
	}

	public void setDao(TypeMetricDAO d) {
		this.dao = d;
	}

	public TypeMetricDAO getDao() {
		return dao;
	}

	public List<TypeMetric> getTypeMetrics() {
		return dao.findAll();
	}

	public TypeMetric getTypeMetricById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTypeMetric(TypeMetric d) throws Exception{
		if ( dao.findForName(d.getName())== null){
			return dao.save(d);
		}else{
			throw new Exception("This TypeMetric name is already used");
		}
	}

	public boolean delTypeMetric(TypeMetric d) {
		return dao.delete(d);
	}

	public boolean updateTypeMetric(TypeMetric d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This TypeMetric doesnt exists");
		}
	}

	public TypeMetric getTypeMetricByName(String typeMetricName) {		
		return  dao.findForName(typeMetricName);
	}

}
