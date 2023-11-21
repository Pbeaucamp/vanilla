package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;




public class DashboardRelationManager {
	private DashboardRelationDAO dao;
	
	public DashboardRelationManager() {
		super();
	}
	
	
	public void setDao(DashboardRelationDAO d) {
		this.dao = d;
	}

	public DashboardRelationDAO getDao() {
		return dao;
	}

	public List<DashboardRelation> getDashboardRelations() {
		return dao.findAll();
	}
	
	public DashboardRelation getDashboardRelationById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addDashboardRelation(DashboardRelation d) throws Exception{
		
		boolean exist = false;
		
		for (DashboardRelation tmp : dao.findAll()) {
			if(tmp.getChild_Dash_Id() == d.getChild_Dash_Id() && tmp.getParent_Dash_Id() == d.getParent_Dash_Id()){
				exist = true;
				break;
			}
		}
		
		if (!exist){
			return dao.save(d);
		}
		else{
			throw new Exception("This DashboardRelation already existe");
		}
		 
	}

	public void delDashboardRelation(DashboardRelation d) {
		dao.delete(d);
	}


}
