package bpm.freemetrics.api.organisation.metrics;

import java.util.ArrayList;
import java.util.List;

public class Assoc_Compteur_IndicateurManager {
	private Assoc_Compteur_IndicateurDAO dao;

	public Assoc_Compteur_IndicateurManager() {
		super();
	}


	public void setDao(Assoc_Compteur_IndicateurDAO d) {
		this.dao = d;
	}

	public Assoc_Compteur_IndicateurDAO getDao() {
		return dao;
	}

	public List<AssocCompteurIndicateur> getAssoc_Compteur_Indicateurs() {
		return dao.findAll();
	}

	public AssocCompteurIndicateur getAssoc_Compteur_IndicateurById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addAssoc_Compteur_Indicateur(AssocCompteurIndicateur d, boolean allowMultiAsso) throws Exception{
		List<AssocCompteurIndicateur> c = dao.getAssoc_Compteur_IndicateurByIndicId(d.getIndic_ID());

		boolean exist = false;

		for (AssocCompteurIndicateur assoc : c) {

			if(assoc.getCompt_ID() == d.getCompt_ID() && assoc.getIndic_ID() == d.getIndic_ID()){
				exist = true;
				break;
			}
		}

		if (!exist){
			if (allowMultiAsso) {
				return dao.save(d);
			}
			else if(getAssoc_Compteur_IndicateurByComptId(d.getCompt_ID()) == null){
				return dao.save(d);
			}else{
				throw new Exception("Le compteur est déja associé à un indicateur");
			}
		}else{
			throw new Exception("Cette association existe déjà");
		}
		
	}

	public void delAssoc_Compteur_Indicateur(AssocCompteurIndicateur d) {
		dao.delete(d);
	}

	public boolean updateAssoc_Compteur_Indicateur(AssocCompteurIndicateur d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			
			return true;
		}
		else{
			throw new Exception("Cette association n'existe pas");
		}

	}

	public List<AssocCompteurIndicateur> getAssoc_Compteur_IndicateurByIndicId(
			int indId) {
		return dao.getAssoc_Compteur_IndicateurByIndicId(indId);
	}


	public AssocCompteurIndicateur getAssoc_Compteur_IndicateurByComptId(int comptId) {
		return dao.getAssoc_Compteur_IndicateurByComptId(comptId);
	}


	public List<Integer> deleteAssocForIndId(int indId) {

		List<Integer> res = new ArrayList<Integer>();
		for (AssocCompteurIndicateur d  : getAssoc_Compteur_IndicateurByIndicId(indId)) {
			delAssoc_Compteur_Indicateur(d);
			res.add(d.getId());
		}
		return res;
	}

	public boolean deleteAssocForCompId(int comptId) {

		AssocCompteurIndicateur d = getAssoc_Compteur_IndicateurByComptId(comptId);
		if(d != null){
			delAssoc_Compteur_Indicateur(d);
		}
		return dao.getAssoc_Compteur_IndicateurByComptId(comptId) == null;
	}


	public void deleteAssocForCompId(AssocCompteurIndicateur assoc) {
		dao.delete(assoc);
	}

}
