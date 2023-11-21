package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.List;

import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserRepDAO extends HibernateDaoSupport {

	public List<UserRep> findAll() {
		return getHibernateTemplate().find("from UserRep");
	}

	public UserRep findByPrimaryKey(int key) {
		List c = getHibernateTemplate().find("from UserRep d where d.id=" + key);
		if(c != null && c.size() > 0) {
			return (UserRep) c.get(0);
		}
		else {
			return null;
		}
	}

	public void save(UserRep d) {
		getHibernateTemplate().save(d);
	}

	public void delete(UserRep d) {
		getHibernateTemplate().delete(d);
	}

	public void update(UserRep d) {
		getHibernateTemplate().update(d);
	}

	public List<UserRep> findByUserId(int userId) {
		return getHibernateTemplate().find("from UserRep d where d.userId=" + userId + " order by d.repositoryId asc");
	}

	public List<UserRep> findByRepositoryId(int repositoryId) {
		return getHibernateTemplate().find("from UserRep d where d.repositoryId=" + repositoryId);
	}

	public List<UserRep> findIdUserRep(int repId, int userId) {
		return getHibernateTemplate().find("from UserRep d where d.repositoryId=" + repId + " and d.userId=" + userId);
	}

	public UserRep findByUserRep(int userId, int repositoryId) {
		List<UserRep> userRep = findIdUserRep(repositoryId, userId);
		if (userRep.size() > 0) {
			return userRep.get(0);
		}
		else {
			return null;
		}
	}
}
