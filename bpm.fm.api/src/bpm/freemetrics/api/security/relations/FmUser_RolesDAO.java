package bpm.freemetrics.api.security.relations;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmUser_RolesDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<FmUser_Roles> findAll() {
		return getHibernateTemplate().find("from FmUser_Roles");
	}
	
	@SuppressWarnings("unchecked")
	public FmUser_Roles findByPrimaryKey(int key) {
		List<FmUser_Roles> c = getHibernateTemplate().find("from FmUser_Roles d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(FmUser_Roles d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FmUser_Roles");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(FmUser_Roles d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getId()) == null;
	}
	
	public void update(FmUser_Roles d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<FmUser_Roles> getByRoleId(int roleId) {
		return getHibernateTemplate().find("from FmUser_Roles d where d.roleId=" +  roleId);
		
	}

	@SuppressWarnings("unchecked")
	public FmUser_Roles getByUserAndRoleId(int userId, int roleId) {
		List<FmUser_Roles> c = getHibernateTemplate().find("from FmUser_Roles d where d.roleId=" + roleId+" AND d.userId = "+userId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<FmUser_Roles> getByUserId(int userId) {
		return getHibernateTemplate().find("from FmUser_Roles d where d.userId=" +  userId);
		
	}

}
