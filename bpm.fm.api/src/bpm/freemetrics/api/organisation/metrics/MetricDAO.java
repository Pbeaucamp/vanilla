package bpm.freemetrics.api.organisation.metrics;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MetricDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Metric> findAll() {
		return getHibernateTemplate().find("from Metric");
	}

	@SuppressWarnings("unchecked")
	public Metric findByPrimaryKey(int key) {
		getHibernateTemplate().flush();
		List<Metric> c = getHibernateTemplate().find("from Metric d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Metric d) {
		return (Integer)getHibernateTemplate().save(d);
	}

	public void delete(Metric d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Metric d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public Metric findForName(String name){
		List<Metric> c = getHibernateTemplate().find("from Metric where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsForOwnerId(int userid) {
		return getHibernateTemplate().find("from Metric d where d.mdOwnerId=" +  userid);
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsForTypeId(int typeId) {
		return getHibernateTemplate().find("from Metric d where d.mdTypeId=" +  typeId);
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsForGroupId(int grpid) {
		return getHibernateTemplate().find("from Metric d where d.mdGroupId=" +  grpid);
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getCompteurs() {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur= '1'");
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getIndicateurs() {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur= '0'");
	}

	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsForType(String calculationType) {
		return getHibernateTemplate().find("from Metric where mdCalculationType='" + calculationType.replace("'", "''") + "' AND mdGlIsCompteur= '1'");
	}
	
	@SuppressWarnings("unchecked")
	public List<Metric> getMetricsByThemeIdAndPeriode(int themeId, String periode) {
		return getHibernateTemplate().find("from Metric d where d.mdCalculationTimeFrame = '" + periode + "' AND mdGlThemeId = " + themeId);
	}
	
	public List<Metric> getCompteursByThemeAndPeriode(int themeId, String periode) {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur = '1' AND d.mdCalculationTimeFrame = '" + periode + "' AND mdGlThemeId = " + themeId);
	}
	
	public List<Metric> getIndicateursByThemeAndPeriode(int themeId, String periode) {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur = '0' AND d.mdCalculationTimeFrame = '" + periode + "' AND mdGlThemeId = " + themeId);
	}

	public List<Metric> getCompteursByObservatoires(String observatoire) {
		String query = "";
		
		query = "FROM Metric m WHERE m.mdGlIsCompteur = '1' AND m.mdGlThemeId IN (SELECT ot.themeId FROM ObservatoiresThemes ot WHERE ot.obsId = (SELECT o.id FROM Observatoire o WHERE o.name = '" + observatoire + "'))";
		
		return getHibernateTemplate().find(query);
	}

	public List<Metric> getIndicateursByObservatoires(String observatoire) {
		String query = "";
		
		query = "FROM Metric m WHERE m.mdGlIsCompteur = '0' AND m.mdGlThemeId IN (SELECT ot.themeId FROM ObservatoiresThemes ot WHERE ot.obsId = (SELECT o.id FROM Observatoire o WHERE o.name = '" + observatoire + "'))";
		
		return getHibernateTemplate().find(query);
	}

	public List<Metric> getNotAssociatedIndicateur(Metric compteur) {
		String query = "From Metric m ";
		query += "WHERE m.mdGlIsCompteur = '0' AND m.id NOT IN ";
		query += "(SELECT indic_ID FROM AssocCompteurIndicateur WHERE compt_ID=" + compteur.getId() + ")";
		return getHibernateTemplate().find(query);
	}

	public List<Metric> getAssciatedIndicateur(int compteurId) {
		String query = "From Metric m ";
		query += "WHERE m.mdGlIsCompteur = '0' AND m.id IN ";
		query += "(SELECT indic_ID FROM AssocCompteurIndicateur WHERE compt_ID=" + compteurId + ")";
		return getHibernateTemplate().find(query);
	}

	public List<Metric> getNotAssociatedCompteur(Metric indicateur, int observatoireId) {
		String query = "From Metric m ";
		query += "WHERE m.mdGlIsCompteur = '1' AND m.id NOT IN ";
		query += "(SELECT compt_ID FROM AssocCompteurIndicateur WHERE indic_ID =" + indicateur.getId() + ")";
		query += " AND m.mdGlThemeId IN (SELECT ot.themeId FROM ObservatoiresThemes ot WHERE ot.obsId = " + observatoireId + ")";
		return getHibernateTemplate().find(query);
	}

	public List<Metric> getAssciatedCompteur(int metricId) {
		String query = "From Metric m ";
		query += "WHERE m.mdGlIsCompteur = '1' AND m.id IN ";
		query += "(SELECT compt_ID FROM AssocCompteurIndicateur WHERE indic_ID=" + metricId + ")";
		return getHibernateTemplate().find(query);
	}

	public Metric getMetricByIdAssoId(Integer mvGlAssocId) {
		String query = "From Metric m ";
		query += "WHERE m.id IN ";
		query += "(SELECT metr_ID FROM Assoc_Application_Metric WHERE id=" + mvGlAssocId + ")";
		List<Metric> metrics = getHibernateTemplate().find(query);
		if (metrics == null || metrics.isEmpty()) {
			throw new NullPointerException();
		}
		else {
			return metrics.get(0);
		}
	}

	public List<Metric> getMetricsByTheme(String t) {
		String query = "From Metric m ";
		query += "WHERE m.mdGlThemeId IN ";
		query += "(SELECT id FROM Theme WHERE name='" + t + "')";
		return getHibernateTemplate().find(query);
			
	}
	
	public List<Metric> getMetricsBySubThemeId(int subThemeId) {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur = '1' AND d.mdGlSubThemeId = " + subThemeId + "");
	}

	public List<Metric> getMetricsByThemeId(int themeId) {
		return getHibernateTemplate().find("from Metric d where d.mdGlIsCompteur = '1' AND d.mdGlThemeId = " + themeId + "");
	}

	public List<MetricComponent> getSubMetrics(int metricId) {
		Object res = getHibernateTemplate().find("from MetricComponent d where d.parentId = " + metricId);
		if(res != null && res instanceof List) {
			return (List<MetricComponent>) res;
		}
		return new ArrayList<MetricComponent>();
	}
	
	
	
}
