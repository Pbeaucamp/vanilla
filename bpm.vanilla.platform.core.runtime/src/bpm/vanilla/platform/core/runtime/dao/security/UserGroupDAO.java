package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.List;

import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserGroupDAO extends HibernateDaoSupport {

	public List findAll() {
		return getHibernateTemplate().find("from UserGroup");
	}

	public UserGroup findByPrimaryKey(long key) {
		return (UserGroup) getHibernateTemplate().load(UserGroup.class, new Long(key));
	}

	public void save(UserGroup d) {
		getHibernateTemplate().save(d);
	}

	public void delete(UserGroup d) {
		getHibernateTemplate().delete(d);
	}

	public void update(UserGroup d) {
		getHibernateTemplate().update(d);
	}

	public List<UserGroup> find4User(Integer userId) {
		return getHibernateTemplate().find("from UserGroup where userId=" + userId);
	}

	public UserGroup findByUserGroup(int userId, int groupId) {
		List<UserGroup> usergroup = getHibernateTemplate().find("from UserGroup where userId=" + userId + " and groupId=" + groupId);
		if(usergroup.size() > 0) {
			return usergroup.get(0);
		}
		else {
			return null;
		}
	}

	public List<UserGroup> find4Group(Integer groupId) {
		return getHibernateTemplate().find("from UserGroup where groupId=" + groupId);
	}
}
