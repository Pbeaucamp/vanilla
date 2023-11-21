package bpm.freemetrics.api.organisation.application;

import java.util.List;

public class TypeDecompTerritoireManager {
	private TypeDecompTerritoireDAO dao;
	
	public TypeDecompTerritoireManager() {
		super();
	}
	
	
	public void setDao(TypeDecompTerritoireDAO d) {
		this.dao = d;
	}

	public TypeDecompTerritoireDAO getDao() {
		return dao;
	}

	public List<TypeDecompTerritoire> getTypeDecompTerritoires() {
		return dao.findAll();
	}
	
	public TypeDecompTerritoire getTypeDecompTerritoireById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTypeDecompTerritoire(TypeDecompTerritoire d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This TypeDecompTerritoire already exists");
		}
		 
	}

	public boolean delTypeDecompTerritoire(TypeDecompTerritoire d) {
		return dao.delete(d);
	}

	public boolean updateTypeDecompTerritoire(TypeDecompTerritoire d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This TypeDecompTerritoire doesnt exists");
		}
	}

	public TypeDecompTerritoire getTypeDecompTerritoireByName(String name) {
		return dao.findForName(name);
	}

}
