package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class TypeMetricDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<TypeMetric> findAll() {
		return getHibernateTemplate().find("from TypeMetric");
	}
	
	
	@SuppressWarnings("unchecked")
	public TypeMetric findByPrimaryKey(int key) {
		List<TypeMetric> c = getHibernateTemplate().find("from TypeMetric d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public TypeMetric findForName(String name){
		List<TypeMetric> c = getHibernateTemplate().find("from TypeMetric d where d.name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(TypeMetric d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from TypeMetric");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		
		return id;
	}

	public boolean delete(TypeMetric d) {
		getHibernateTemplate().delete(d);		
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(TypeMetric d) {
		getHibernateTemplate().update(d);
	}	

}
