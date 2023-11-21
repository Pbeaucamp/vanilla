package bpm.freemetrics.api.organisation.relations.dash_metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;




public class FmDashboard_MetricManager {
	private FmDashboard_MetricDAO dao;

	public FmDashboard_MetricManager() {
		super();
	}


	public void setDao(FmDashboard_MetricDAO d) {
		this.dao = d;
	}

	public FmDashboard_MetricDAO getDao() {
		return dao;
	}

	public List<FmDashboard_Metric> getFmDashboard_Metric() {
		return dao.findAll();
	}

	public FmDashboard_Metric getFmDashboard_MetricById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public void addFmDashboard_Metric(FmDashboard_Metric d) throws Exception{
		Collection c = dao.findAll();
		Iterator it = c.iterator();
		boolean exist = false;

		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			FmDashboard_Metric assoc = (FmDashboard_Metric) iterator.next();

			if(assoc.getDmApplicationId() == d.getDmApplicationId() 
					&& assoc.getDmMetricsId() == d.getDmMetricsId()
					&& assoc.getDmDashboardId() == d.getDmDashboardId()){
				exist = true;
				break;
			}
		}
		
		if(!exist){
			while(it.hasNext()){
				if (((FmDashboard_Metric)it.next()).getId() == d.getId()){
					exist = true;
					break;
				}
			}
		}else{
			throw new Exception("This Assoc_Application_Metric already exist");
		}

		if (!exist){
			dao.save(d);
		}
		else{
			throw new Exception("This Assoc_Application_Metric name already exist");
		}

	}

	public void delFmDashboard_Metric(FmDashboard_Metric d) {
		dao.delete(d);
	}

	public void updateFmDashboard_Metric(FmDashboard_Metric d) throws Exception {
		Collection c = dao.findAll();
		Iterator it = c.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (((FmDashboard_Metric)it.next()).getId() == d.getId()){
				i++;
			}
		}
		if (i<=1){
			dao.update(d);
		}
		else{
			throw new Exception("This group/collectivity doesnt exists");
		}

	}

	public List<Integer> getApplicationIdsForMetricId(int id) {
		
		List<Integer> appIds = new ArrayList<Integer>();
		Collection c = dao.findAll();
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			FmDashboard_Metric ass = (FmDashboard_Metric) iterator.next();
			if (ass.getDmMetricsId() == id ){
				appIds.add(ass.getDmApplicationId());
			}
		}
			return appIds;
	}
	
	public List<Integer> getMetricIdsForAppAndDash(int appId, int dashId){
		
		List<Integer> metrIds = new ArrayList<Integer>();
		Collection c = dao.findAll();
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			FmDashboard_Metric ass = (FmDashboard_Metric) iterator.next();
			if (ass.getDmApplicationId() == appId &&  ass.getDmDashboardId() == dashId){
				metrIds.add(ass.getDmMetricsId());
			}
		}
			return metrIds;
	}

}
