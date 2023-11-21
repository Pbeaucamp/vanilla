package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DashboardManager {
	private DashboardDAO dao;
	
	public DashboardManager() {
		super();
	}
	
	public void setDao(DashboardDAO d) {
		this.dao = d;
	}

	public DashboardDAO getDao() {
		return dao;
	}

	public List<Dashboard> getDashboards() {
		return dao.findAll();
	}
	
	public Dashboard getDashboardById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public void addDashboard(Dashboard d) throws Exception{
		Collection c = dao.findAll();
		Iterator it = c.iterator();
		boolean exist = false;
		while(it.hasNext()){
			if (((Dashboard)it.next()).getName().equals(d.getName())){
				exist = true;
				break;
			}
		}
		if (!exist){
			dao.save(d);
		}
		else{
			throw new Exception("This Dashboardname/collectivity is already used");
		}
		 
	}

	public void delDashboard(Dashboard d) {
		dao.delete(d);
	}

	public void updateDashboard(Dashboard d) throws Exception {
		Collection c = dao.findAll();
		Iterator it = c.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (((Dashboard)it.next()).getName().equals(d.getName())){
				i++;
			}
		}
		if (i<=1){
			dao.update(d);
		}
		else{
			throw new Exception("This Dashboard/collectivity doesnt exists");
		}
		
	}
	
}
