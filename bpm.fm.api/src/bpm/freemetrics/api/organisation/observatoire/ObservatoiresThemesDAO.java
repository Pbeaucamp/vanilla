package bpm.freemetrics.api.organisation.observatoire;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ObservatoiresThemesDAO extends HibernateDaoSupport {

	public List<ObservatoiresThemes> findByObservatoireId(int observatoireId) {
		return getHibernateTemplate().find("from ObservatoiresThemes t where t.obsId = (select o.id from Observatoire o where o.id = "+observatoireId + ")");
	}
	
	public List<ObservatoiresThemes> findByThemeId(int id) {
		return getHibernateTemplate().find("from ObservatoiresThemes where themeId=" + id);
	}
	
	public int add(ObservatoiresThemes ot) {
		return (Integer) getHibernateTemplate().save(ot);
	}
	
	public void delete(ObservatoiresThemes ot) {
		getHibernateTemplate().delete(ot);
	}
	
	public List<ObservatoiresThemes> findAll() {
		return getHibernateTemplate().find("from ObservatoiresThemes");
	}
	
	public ObservatoiresThemes findByPrimaryKey(Integer key) {
		List<ObservatoiresThemes> l = getHibernateTemplate().find("from ObservatoiresThemes where assoId=" + key);
		if (!l.isEmpty()) {
			return l.get(0);
		}
		else {
			return null;
		}
	}

	public List<ObservatoiresThemes> getByThemeAndObservatoire(int themeId, int observatoireId){
		return getHibernateTemplate().find("from ObservatoiresThemes where themeId=" + themeId + " and obsId = " + observatoireId);
	}
	
	public void remove(int themeId, int obsId) {
		for (ObservatoiresThemes ot : getByThemeAndObservatoire(themeId, obsId)) {
			delete(ot);
		}
		
	}
	
}
