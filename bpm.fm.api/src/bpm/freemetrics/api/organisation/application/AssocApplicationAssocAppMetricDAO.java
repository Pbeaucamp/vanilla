package bpm.freemetrics.api.organisation.application;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class AssocApplicationAssocAppMetricDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public List<AssocApplicationAssocAppMetric> findAll() {
		return getHibernateTemplate().find("from AssocApplicationAssocAppMetric");
	}


	@SuppressWarnings("unchecked")
	public AssocApplicationAssocAppMetric findByPrimaryKey(int key) {
		List<AssocApplicationAssocAppMetric> c = getHibernateTemplate().find("from AssocApplicationAssocAppMetric d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	public int save(AssocApplicationAssocAppMetric d) {
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(AssocApplicationAssocAppMetric d) {
		getHibernateTemplate().delete(d);
	}
	public void update(AssocApplicationAssocAppMetric d) {
		getHibernateTemplate().update(d);
	}


	public List<AssocApplicationAssocAppMetric> getByAssoId(int id) {
		return getHibernateTemplate().find("from AssocApplicationAssocAppMetric d where d.assocId=" +  id);
	}


	public void deleteForAppId(int id) {
		List<AssocApplicationAssocAppMetric> assos = getHibernateTemplate().find("from AssocApplicationAssocAppMetric d where d.applicationId=" +  id);
		if(assos != null && assos.size() > 0) {
			for(AssocApplicationAssocAppMetric asso : assos) {
				delete(asso);
			}
		}
	}


	public List<AssocApplicationAssocAppMetric> findByAssoId(int id) {
		return getHibernateTemplate().find("from AssocApplicationAssocAppMetric d where d.assocId=" +  id);
	}


	public List<AssocApplicationAssocAppMetric> getByApplicationId(int appId) {
		return getHibernateTemplate().find("from AssocApplicationAssocAppMetric d where d.applicationId=" +  appId);
	}
}
