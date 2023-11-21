package bpm.freemetrics.api.features.infos;

import java.util.List;

public class OrganisationManager {
	private OrganisationDAO dao;
	
	public OrganisationManager() {
		super();
	}
		
	public void setDao(OrganisationDAO d) {
		this.dao = d;
	}

	public OrganisationDAO getDao() {
		return dao;
	}

	public List<Organisation> getOrganisations() {
		return dao.findAll();
	}
	
	public Organisation getOrganisationById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addOrganisation(Organisation d) throws Exception{
		
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Organisation already exists");
		}
		 
	}

	public boolean delOrganisation(Organisation d) {
		return dao.delete(d);
	}

	public boolean updateOrganisation(Organisation d) throws Exception {
		
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This Organisation doesnt exists");
		}
	}


	public Organisation getOrganisationByName(String name) {
		return dao.findForName(name);
	}

}
