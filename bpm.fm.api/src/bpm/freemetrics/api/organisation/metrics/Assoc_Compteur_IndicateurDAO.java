package bpm.freemetrics.api.organisation.metrics;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class Assoc_Compteur_IndicateurDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<AssocCompteurIndicateur> findAll() {
		return getHibernateTemplate().find("from AssocCompteurIndicateur");
	}
	
	@SuppressWarnings("unchecked")
	public AssocCompteurIndicateur findByPrimaryKey(int key) {
		List<AssocCompteurIndicateur> c = getHibernateTemplate().find("from AssocCompteurIndicateur t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(AssocCompteurIndicateur d) {
		return (Integer)getHibernateTemplate().save(d);
	}

	public void delete(AssocCompteurIndicateur d) {
		List<AssocCompteurIndicateur> c = getHibernateTemplate().find("from AssocCompteurIndicateur t where t.compt_ID=" +  d.getCompt_ID() +" AND t.indic_ID=" + d.getIndic_ID());
		if (c != null && c.size() > 0){
			getHibernateTemplate().delete(c.get(0));
		}
		
	}
	public void update(AssocCompteurIndicateur d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public AssocCompteurIndicateur getAssoc_Compteur_IndicateurByComptId(int comptId) {
		List<AssocCompteurIndicateur> c = getHibernateTemplate().find("from AssocCompteurIndicateur t where t.compt_ID=" +  comptId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<AssocCompteurIndicateur> getAssocCompteurIndicateurByComptId(int comptId) {
		return getHibernateTemplate().find("from AssocCompteurIndicateur t where t.compt_ID=" +  comptId);
	}

	@SuppressWarnings("unchecked")
	public List<AssocCompteurIndicateur> getAssoc_Compteur_IndicateurByIndicId(
			int indId) {
		return getHibernateTemplate().find("from AssocCompteurIndicateur t where t.indic_ID=" +  indId);
	}
	

}
