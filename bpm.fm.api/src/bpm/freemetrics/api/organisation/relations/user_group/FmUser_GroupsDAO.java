package bpm.freemetrics.api.organisation.relations.user_group;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmUser_GroupsDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<FmUser_Groups> findAll() {
		return getHibernateTemplate().find("from FmUser_Groups");
	}
	
	@SuppressWarnings("unchecked")
	public FmUser_Groups findByPrimaryKey(int key) {
		List<FmUser_Groups> c = getHibernateTemplate().find("from FmUser_Groups d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(FmUser_Groups d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FmUser_Groups");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		
		return id;
	}

	public void delete(FmUser_Groups d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(FmUser_Groups d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public List<FmUser_Groups> findForName(String name){
		return getHibernateTemplate().find("from FmUser_Groups where name='" + name.replace("'", "''") + "'");
	}

	@SuppressWarnings("unchecked")
	public List<FmUser_Groups> getByUserId(int id) {
		return getHibernateTemplate().find("from FmUser_Groups d where d.userId=" +  id);
	}

	@SuppressWarnings("unchecked")
	public List<FmUser_Groups> getByGroupId(int id) {
		return getHibernateTemplate().find("from FmUser_Groups d where d.groupId=" +  id);
	}

	@SuppressWarnings("unchecked")
	public FmUser_Groups getByUserAndGroupId(int userId, int groupId) {
		List<FmUser_Groups> c = getHibernateTemplate().find("from FmUser_Groups d where d.userId=" +userId +" AND d.groupId="+groupId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}



}
