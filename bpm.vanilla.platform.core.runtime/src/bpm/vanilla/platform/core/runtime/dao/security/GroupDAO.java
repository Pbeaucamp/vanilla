package bpm.vanilla.platform.core.runtime.dao.security;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GroupDAO extends HibernateDaoSupport {


	public List<Group> findAll() {
		return getHibernateTemplate().find("from Group");
	}

	public List<Group> findByPrimaryKey(Integer key) {
		return (List<Group>) getHibernateTemplate().find("from Group where id=" + key);
	}

	public int save(Group d) {
		return (Integer)getHibernateTemplate().save(d);
	}

	public void delete(Group d) {

		getHibernateTemplate().delete(d);
	}

	public void update(Group d) {
		getHibernateTemplate().update(d);
	}

	public Group findByName(String groupName) {
		List<Group> l = (List<Group>) getHibernateTemplate().find("from Group where name='" + groupName + "'");
		
		if (l.size()>0){
			return l.get(0);
		}
		
		return null;
	}

	public List<Group> getGroups(int begin, int step) {
		String sql = "from Group g order by g.id asc";
		Session	session = getHibernateTemplate().getSessionFactory().openSession();
				
		Query hql = session.createQuery(sql);
		
		hql.setFirstResult(begin);
		hql.setMaxResults(step);
		hql.setFetchSize(step);
		
		List l = hql.list();
		
		session.close();
		return l;
	}

	public List<Group> getGroupsChildOf(Group group) {
		if (group == null){
			return new ArrayList<Group>();
		}
		return getHibernateTemplate().find("from Group where parentId=" + group.getId());
	}

	public int getGroupsNumber() {
		return (Integer)getHibernateTemplate().find("select count(*) from Group").get(0);
	}
}
