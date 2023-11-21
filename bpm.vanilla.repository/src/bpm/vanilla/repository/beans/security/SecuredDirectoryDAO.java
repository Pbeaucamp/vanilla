package bpm.vanilla.repository.beans.security;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.repository.SecuredDirectory;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecuredDirectoryDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public Collection<SecuredDirectory> findAll() {
		return getHibernateTemplate().find("from SecuredDirectory");
	}

	public SecuredDirectory findByPrimaryKey(long key) {
		return (SecuredDirectory) getHibernateTemplate().load(SecuredDirectory.class, new Long(key));
	}

	public void save(SecuredDirectory d) {
		getHibernateTemplate().save(d);
	}

	public void delete(SecuredDirectory d) {
		getHibernateTemplate().delete(d);
	}

	public void update(SecuredDirectory d) {
		getHibernateTemplate().update(d);
	}

	/**
	 * return securedDirectory for userId, directoryId, groupId
	 * 
	 * @param userId
	 * @param directoryId
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SecuredDirectory> getSecuredDirectory4UserDirectory(Integer directoryId, Integer groupId) {
		return getHibernateTemplate().find("from SecuredDirectory where directoryId=" + directoryId + " AND groupId=" + groupId);
	}

	@SuppressWarnings("unchecked")
	public SecuredDirectory getSecuredDirectory(Integer directoryId, Integer groupId) {
		List<SecuredDirectory> l = getHibernateTemplate().find("from SecuredDirectory where directoryId=" + directoryId + " AND groupId=" + groupId);
		if (l.isEmpty()) {
			return null;
		}
		return (SecuredDirectory) l.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredDirectory> getFor(int directoryId) {
		return getHibernateTemplate().find("from SecuredDirectory where directoryId=" + directoryId);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredDirectory> getForGroup(int groupId) {
		return getHibernateTemplate().find("from SecuredDirectory where groupId=" + groupId);
	}

}
