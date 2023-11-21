package bpm.freemetrics.api.organisation.application;

import java.util.List;

public class TypeTerritoireManager {
	private TypeTerritoireDAO dao;

	public TypeTerritoireManager() {
		super();
	}


	public void setDao(TypeTerritoireDAO d) {
		this.dao = d;
	}

	public TypeTerritoireDAO getDao() {
		return dao;
	}

	public List<TypeTerritoire> getTypeTerritoires() {
		return dao.findAll();
	}

	public TypeTerritoire getTypeTerritoireById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTypeTerritoire(TypeTerritoire d) throws Exception{

		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This TypeTerritoire already exists");
		}
	}

	public boolean delTypeTerritoire(TypeTerritoire d) {
		dao.delete(d);		 
		return dao.findByPrimaryKey(d.getId()) == null;
	}

	public boolean updateTypeTerritoire(TypeTerritoire d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null ){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This TypeTerritoire doesnt exists");
		}
	}

	public TypeTerritoire getTypeTerritoireByName(String name) {
		return dao.findForName(name);
	}

}
