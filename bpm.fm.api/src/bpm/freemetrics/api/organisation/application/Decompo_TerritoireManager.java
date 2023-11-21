package bpm.freemetrics.api.organisation.application;

import java.util.ArrayList;
import java.util.List;


public class Decompo_TerritoireManager {
	private Decompo_TerritoireDAO dao;

	public Decompo_TerritoireManager() {
		super();
	}

	public void setDao(Decompo_TerritoireDAO d) {
		this.dao = d;
	}

	public Decompo_TerritoireDAO getDao() {
		return dao;
	}

	public List<Decompo_Territoire> getDecompo_Territoires() {
		return dao.findAll();
	}

	public Decompo_Territoire getDecompo_TerritoireById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addDecompo_Territoire(Decompo_Territoire d) throws Exception{
		if (getDecompo_TerritoireForParentAndChildId(d.getParent_Assoc_Terr_TypDecTerr_ID(), d.getChild_App_ID()) == null ){
			return dao.save(d);
		}else{
			throw new Exception("This Decompo_Territoire parent / child already exists");
		}
	}

	public void delDecompo_Territoire(Decompo_Territoire d) {
		dao.delete(d);
	}

	public void updateDecompo_Territoire(Decompo_Territoire d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This decomposition territoire doesnt exists");
		}
	}

	public boolean delDecompo_Territoire(int parentId, int childId) {

		Decompo_Territoire assoc = getDecompo_TerritoireForParentAndChildId(parentId,childId);
		if(assoc != null){
			return dao.delete(assoc);
		}
		return false;
	}

	public Decompo_Territoire getDecompo_TerritoireForParentAndChildId(int parentId, int childId) {
		return dao.getDecompo_TerritoireForParentAndChildId(parentId, childId);
	}

	public List<Integer> getChildsIdForAssoc_Terr_TypDecTerId(int parentAssocTerrTypDecTerrId) {
		List<Integer> res = new ArrayList<Integer>();

		for (Decompo_Territoire dec : dao.getForParentId(parentAssocTerrTypDecTerrId)) {

			if(dec.getParent_Assoc_Terr_TypDecTerr_ID() == parentAssocTerrTypDecTerrId){
				res.add(dec.getChild_App_ID());
			}
		}

		return res;
	}

	public boolean delDecompo_TerritoireById(int decId) {
		Decompo_Territoire assoc = dao.findByPrimaryKey(decId);
		if(assoc != null){
			return dao.delete(assoc);
		}
		return false;
	}

	public List<Decompo_Territoire> getByChild_App_ID(int applId) {
		return dao.getForChildAppId(applId);
	}
}
