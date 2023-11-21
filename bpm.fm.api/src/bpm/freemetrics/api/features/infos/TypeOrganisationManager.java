package bpm.freemetrics.api.features.infos;

import java.util.List;

public class TypeOrganisationManager {
	private TypeOrganisationDAO dao;
	
	public TypeOrganisationManager() {
		super();
	}
	
	
	public void setDao(TypeOrganisationDAO d) {
		this.dao = d;
	}

	public TypeOrganisationDAO getDao() {
		return dao;
	}

	public List<TypeOrganisation> getTypeOrganisations() {
		return dao.findAll();
	}
	
	public TypeOrganisation getTypeOrganisationById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTypeOrganisation(TypeOrganisation d) throws Exception{
		
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This TypeOrganisation already exists");
		}
		 
	}

	public boolean delTypeOrganisation(TypeOrganisation d) {
		dao.delete(d);
		return dao.findByPrimaryKey(d.getId())== null;
	}

	public boolean updateTypeOrganisation(TypeOrganisation d) throws Exception {
		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This TypeOrganisation doesnt exists");
		}
	}


	public TypeOrganisation getTypeOrganisationByName(String name) {
		return dao.findForName(name);
	}

}
