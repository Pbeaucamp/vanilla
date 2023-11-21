package bpm.freemetrics.api.features.infos;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class PropertiesDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Property> findAll() {
		return getHibernateTemplate().find("from Property");
	}
	
	
	@SuppressWarnings("unchecked")
	public Property findByPrimaryKey(int key) {
		List<Property> c = getHibernateTemplate().find("from Property d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Property findForName(String name){
		List<Property> c = getHibernateTemplate().find("from Property d where d.name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	public int save(Property d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		
		return id;
	}

	public void delete(Property d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Property d) {
		getHibernateTemplate().update(d);
	}	

}
