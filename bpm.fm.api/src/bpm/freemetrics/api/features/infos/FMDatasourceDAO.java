package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FMDatasourceDAO extends HibernateDaoSupport {
	@SuppressWarnings("unchecked")
	public List<FMDatasource> findAll() {
		return getHibernateTemplate().find("from FMDatasource");
	}
	
	
	@SuppressWarnings("unchecked")
	public FMDatasource findByPrimaryKey(int key) {
		List<FMDatasource> c = getHibernateTemplate().find("from FMDatasource d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(FMDatasource d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FMDatasource");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(FMDatasource d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId())== null;
	}
	public void update(FMDatasource d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public FMDatasource findForName(String name){
		List<FMDatasource> c = getHibernateTemplate().find("from FMDatasource where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


}
