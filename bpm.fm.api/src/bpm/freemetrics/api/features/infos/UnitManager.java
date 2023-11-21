package bpm.freemetrics.api.features.infos;

import java.util.List;

public class UnitManager {
	private UnitDAO dao;
	
	public UnitManager() {
		super();
	}
	
	public void setDao(UnitDAO d) {
		this.dao = d;
	}

	public UnitDAO getDao() {
		return dao;
	}

	public List<Unit> getUnits() {
		return dao.findAll();
	}
	
	public Unit getUnitById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addUnit(Unit d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Unit name is already used");
		}
		 
	}

	public boolean delUnit(Unit d) {
		dao.delete(d);
		
		return dao.findByPrimaryKey(d.getId()) == null;
	}

	public boolean updateUnit(Unit d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This Unit doesnt exists");
		}
	}

	public Unit getUnitByName(String unitName) {		
		return  dao.findForName(unitName);
	}
	
}
