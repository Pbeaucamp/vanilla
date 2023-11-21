package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class DashboardRelationDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<DashboardRelation> findAll() {
		return getHibernateTemplate().find("from DashboardRelation");
	}
	
	@SuppressWarnings("unchecked")
	public DashboardRelation findByPrimaryKey(int key) {
		List<DashboardRelation> c = getHibernateTemplate().find("from DashboardRelation t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	public int save(DashboardRelation d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(DashboardRelation d) {
		getHibernateTemplate().delete(d);
	}	

}
