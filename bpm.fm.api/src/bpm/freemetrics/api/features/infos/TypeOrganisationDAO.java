package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class TypeOrganisationDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<TypeOrganisation> findAll() {
		return getHibernateTemplate().find("from TypeOrganisation");
	}
	
	@SuppressWarnings("unchecked")
	public TypeOrganisation findByPrimaryKey(int key) {
		List<TypeOrganisation> c = getHibernateTemplate().find("from TypeOrganisation t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(TypeOrganisation d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from TypeOrganisation");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(TypeOrganisation d) {
		getHibernateTemplate().delete(d);
	}
	public void update(TypeOrganisation d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public TypeOrganisation findForName(String name){
		List<TypeOrganisation> c =getHibernateTemplate().find("from TypeOrganisation where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}


}
