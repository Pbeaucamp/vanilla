package bpm.freemetrics.api.features.infos;

import java.util.List;

public class FMDatasourceManager {
	private FMDatasourceDAO dao;
	
	public FMDatasourceManager() {
		super();
	}
		
	public void setDao(FMDatasourceDAO d) {
		this.dao = d;
	}

	public FMDatasourceDAO getDao() {
		return dao;
	}

	public List<FMDatasource> getFMDatasources() {
		return dao.findAll();
	}
	
	public FMDatasource getFMDatasourceById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addFMDatasource(FMDatasource d) throws Exception{
		
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This FMDatasource already exists");
		}
		 
	}

	public boolean delFMDatasource(FMDatasource d) {
		return dao.delete(d);
	}

	public boolean updateFMDatasource(FMDatasource d) throws Exception {
		
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This FMDatasource doesnt exists");
		}
	}


	public FMDatasource getFMDatasourceByName(String name) {
		return dao.findForName(name);
	}

}
