package bpm.vanilla.platform.core.runtime.dao.security;


import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class RoleDAO extends HibernateDaoSupport {

	public Collection findAll() {
		return getHibernateTemplate().find("from Role");
	}

	public List<Role> findByPrimaryKey(int key) {
		return (List<Role>) getHibernateTemplate().find("from Role where id=" + key);
	}
	
	public Role findByName(String name) {
		List<Role> l = (List<Role>) getHibernateTemplate().find("from Role where name='" + name + "'");
		
		if (l.size()>0){
			return l.get(0);
		}
		
		return null;
	}
	
	public int save(Role d) {
		Integer id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(Role d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Role d) {
		getHibernateTemplate().update(d);
	}

	public List<Role> getForAppById(int id, String type) {
		return (List<Role>) getHibernateTemplate().find("from Role where id=" + id + " and type='"  + type + "'");
	}
	
	public List<Role> getForApp(String type) {
		return (List<Role>) getHibernateTemplate().find("from Role where type='"  + type + "'");
	}
}
