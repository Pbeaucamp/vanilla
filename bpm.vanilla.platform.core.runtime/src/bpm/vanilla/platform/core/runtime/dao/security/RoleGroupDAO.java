package bpm.vanilla.platform.core.runtime.dao.security;


import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class RoleGroupDAO extends HibernateDaoSupport {

	public Collection findAll() {
		return getHibernateTemplate().find("from RoleGroup");
	}

	public void save(RoleGroup d) {
		getHibernateTemplate().save(d);
	}

	public void delete(RoleGroup d) {
		getHibernateTemplate().delete(d);
	}
	public void update(RoleGroup d) {
		getHibernateTemplate().update(d);
	}

	public List<RoleGroup> getForGroupId(int groupId){
		return getHibernateTemplate().find("from RoleGroup where groupId=" + groupId);
	}

	public List<RoleGroup> getForRoleId(int roleId) {
		return getHibernateTemplate().find("from RoleGroup where roleId=" + roleId);
	}
	
	public RoleGroup findByRoleGroup(int roleId, int groupId){
		List<RoleGroup> rolegroup = getHibernateTemplate().find("from RoleGroup where roleId=" + roleId + " and groupId=" + groupId);
		if (rolegroup.size() > 0){
			return rolegroup.get(0);
		}
		else 
			return null;
	}
}
