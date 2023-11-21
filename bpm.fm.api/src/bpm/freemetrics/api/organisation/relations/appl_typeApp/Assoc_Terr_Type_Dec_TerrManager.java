package bpm.freemetrics.api.organisation.relations.appl_typeApp;

import java.util.ArrayList;
import java.util.List;

public class Assoc_Terr_Type_Dec_TerrManager {
	private Assoc_Terr_Type_Dec_TerrDAO dao;

	public Assoc_Terr_Type_Dec_TerrManager() {
		super();
	}

	public void setDao(Assoc_Terr_Type_Dec_TerrDAO d) {
		this.dao = d;
	}

	public Assoc_Terr_Type_Dec_TerrDAO getDao() {
		return dao;
	}

	public List<Assoc_Terr_Type_Dec_Terr> getAssoc_Terr_Type_Dec_Terrs() {
		return dao.findAll();
	}

	public Assoc_Terr_Type_Dec_Terr getAssoc_Terr_Type_Dec_TerrById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addAssoc_Terr_TypeDecom(Assoc_Terr_Type_Dec_Terr d) throws Exception{

		if ( dao.getByChildAndParentId(d.getChild_TypeDecTerr_ID(), d.getParent_App_ID()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Assoc_Terr_Type_Dec_Terr parent / child already exist");
		}
	}

	public void delAssoc_Terr_Type_Dec_Terr(Assoc_Terr_Type_Dec_Terr d) {
		dao.delete(d);
	}

	public void updateAssoc_Terr_Type_Dec_Terr(Assoc_Terr_Type_Dec_Terr d) throws Exception {

		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
		}
		else{
			throw new Exception("This decomposition territoire doesnt exists");
		}
	}

	public boolean delAssoc_Terr_Type_Dec_Terr(int parentId, int childId) {

		Assoc_Terr_Type_Dec_Terr dec = dao.getByChildAndParentId(childId, parentId);

		if(dec != null){

			if(dec.getChild_TypeDecTerr_ID()== childId && dec .getParent_App_ID() == parentId){
				dao.delete(dec);
			}
		}
		return dao.getByChildAndParentId(childId, parentId) == null;
	}

	public List<Integer> getChildsIdForAppId(int parentAppId) {
		List<Integer> res = new ArrayList<Integer>();

		List<Assoc_Terr_Type_Dec_Terr> assoc = dao.getChildsIdForAppId(parentAppId);

		if(assoc != null && !assoc.isEmpty()){
			for (Assoc_Terr_Type_Dec_Terr dec : assoc) {
				if(dec.getParent_App_ID() == parentAppId){
					res.add(dec.getChild_TypeDecTerr_ID());
				}
			}
		}

		return res;
	}

	public boolean delAssoc_Terr_Type_Dec_TerrById(int assocId) {
		boolean  res = false;

		Assoc_Terr_Type_Dec_Terr dec = dao.findByPrimaryKey(assocId);

		if(dec != null){
			res  = dao.delete(dec);
		}

		return res;
	}

	public List<Assoc_Terr_Type_Dec_Terr> getByParent_App_ID(int applId) {
		return dao.getChildsIdForAppId(applId);
	}

	public List<Assoc_Terr_Type_Dec_Terr> getByChildTypeDecomTerrId(int typDecId) {
		return dao.getByChildTypeDecomTerrId(typDecId);
	}
}
