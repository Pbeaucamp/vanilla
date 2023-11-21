package bpm.vanilla.repository.beans.impact;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.workplace.core.datasource.AbstractDatasource;

public class RelationalImpactDAO extends HibernateDaoSupport {

	public List<AbstractDatasource> getAllDatasources() {
		return getHibernateTemplate().find("from AbstractDatasource");
	}
	
	public List<AbstractDatasource> getDatasourcesByItemId(int itemId) {
		return getHibernateTemplate().find("from AbstractDatasource where itemId = " + itemId);
	}
	
	public AbstractDatasource getDatasourceById(int id) {
		return (AbstractDatasource) getHibernateTemplate().find("from AbstractDatasource where dbId = " + id).get(0);
	}
	
	public void deleteByItemId(int itemId) {
		getHibernateTemplate().deleteAll(getDatasourcesByItemId(itemId));
	}
	
}
