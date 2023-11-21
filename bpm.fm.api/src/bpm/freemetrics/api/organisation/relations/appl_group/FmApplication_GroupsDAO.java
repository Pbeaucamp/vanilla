package bpm.freemetrics.api.organisation.relations.appl_group;

import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmApplication_GroupsDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<FmApplication_Groups> findAll() {
		return getHibernateTemplate().find("from FmApplication_Groups");
	}

	@SuppressWarnings("unchecked")
	public FmApplication_Groups findByPrimaryKey(int key) {
		List<FmApplication_Groups> c = getHibernateTemplate().find("from FmApplication_Groups d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(FmApplication_Groups d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FmApplication_Groups");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		
		return id;
	}

	public void delete(FmApplication_Groups d) {
		getHibernateTemplate().delete(d);
	}

	public void update(FmApplication_Groups d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<FmApplication_Groups> findForName(String name){
		return getHibernateTemplate().find("from FmApplication_Groups where name='" + name.replace("'", "''") + "'");
	}

	@SuppressWarnings("unchecked")
	public FmApplication_Groups getByApplicationId(int id) {
		List<FmApplication_Groups> c = getHibernateTemplate().find("from FmApplication_Groups d where d.applicationId=" +  id);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<FmApplication_Groups> getByGroupId(int id) {
		return getHibernateTemplate().find("from FmApplication_Groups d where d.groupId=" +  id);
	}

	@SuppressWarnings("unchecked")
	public List<Application> getApplicationForGroup(int gpId) {
		String query = "SELECT m FROM Application m, FmApplication_Groups t WHERE m.id = t.applicationId AND  t.groupId="+gpId;

		return getHibernateTemplate().find(query);
	}

	@SuppressWarnings("unchecked")
	public Group getGroupForApplication(int appId) {
		String query = "SELECT m FROM Group m, FmApplication_Groups t WHERE m.id = t.groupId AND  t.applicationId="+appId;

		List<Group> c = getHibernateTemplate().find(query);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

}
