package bpm.freemetrics.api.organisation.metrics;

import java.util.List;

public class MetricInteractionManager {
	private MetricInteractionDAO dao;

	public MetricInteractionManager() {
		super();
	}

	public void setDao(MetricInteractionDAO d) {
		this.dao = d;
	}

	public MetricInteractionDAO getDao() {
		return dao;
	}

	public List<MetricInteraction> getMetricInteractions() {
		return dao.findAll();
	}

	public MetricInteraction getMetricInteractionById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addMetricInteraction(MetricInteraction d) throws Exception{

		if (dao.getMetricInteractionForAppId_TopAndDownId(d.getMiApplicationId(),d.getMiMetricTopId(),d.getMiMetricDownId()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This MetricInteraction is already exists");
		}

	}

	public boolean delMetricInteraction(MetricInteraction d) {
		return dao.delete(d);
	}

	public void updateMetricInteraction(MetricInteraction d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This MetricInteraction doesnt exists");
		}
	}

	public MetricInteraction getMetricInteractionForTopId(int compt_ID) {
		return dao.getMetricInteractionForTopId(compt_ID);

	}

	public boolean deleteMetricInteractionForTopId(int metrId) {
		MetricInteraction d = dao.getMetricInteractionForTopId(metrId);
		if(d != null){
			dao.delete(d);

			return dao.getMetricInteractionForTopId(metrId)==null;
		}else{
			return true;
		}
	}

	public boolean deleteMetricInteractionForDownId(int metrId) {
		List<MetricInteraction> inters = dao.getMetricInteractionForDownId(metrId);
		for (MetricInteraction d  : inters) {
			dao.delete(d);
		}

		inters = dao.getMetricInteractionForDownId(metrId);

		return inters == null || inters.isEmpty();
	}

	public List<MetricInteraction> getMetricInteractionsForMetrId(int mid) {
		return dao.getMetricInteractionsForMetrId(mid);
	}
}
