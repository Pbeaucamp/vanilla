package bpm.freemetrics.api.organisation.group;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class TypeCollectiviteDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<TypeCollectivite> findAll() {
		return getHibernateTemplate().find("from TypeCollectivite");
	}
	
	@SuppressWarnings("unchecked")
	public TypeCollectivite findByPrimaryKey(int key) {
		List<TypeCollectivite> c = getHibernateTemplate().find("from TypeCollectivite t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(TypeCollectivite d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from TypeCollectivite");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(TypeCollectivite d) {
		getHibernateTemplate().delete(d);
	}
	public void update(TypeCollectivite d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public TypeCollectivite findForName(String name){
		List<TypeCollectivite> c = getHibernateTemplate().find("from TypeCollectivite where name='" + name + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}


}
