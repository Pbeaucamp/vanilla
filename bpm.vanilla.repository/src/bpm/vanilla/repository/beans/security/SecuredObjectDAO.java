package bpm.vanilla.repository.beans.security;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.repository.SecuredObject;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecuredObjectDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public Collection<SecuredObject> findAll() {
		return getHibernateTemplate().find("from SecuredObject");
	}

	public SecuredObject findByPrimaryKey(long key) {
		return (SecuredObject) getHibernateTemplate().load(SecuredObject.class, new Long(key));
	}

	public SecuredObject findByDirectoryItemId(long key) {
		return (SecuredObject) getHibernateTemplate().find("from SecuredObject where directoryItemId=" + key);
	}

	public void save(SecuredObject d) {
		getHibernateTemplate().save(d);
	}

	public void delete(SecuredObject d) {
		getHibernateTemplate().delete(d);
	}

	public void update(SecuredObject d) {
		getHibernateTemplate().update(d);
	}

	/**
	 * bad method naming use getForItemAndGroup instead
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public List<SecuredObject> getSecuredObject4UserDirectory(int id, Integer groupId) {
		return getHibernateTemplate().find("from SecuredObject where directoryItemId=" + id + " AND groupId=" + groupId);

	}

	@SuppressWarnings("unchecked")
	public List<SecuredObject> getFor(int id) {
		return getHibernateTemplate().find("from SecuredObject where directoryItemId=" + id);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredObject> getForGroup(int groupId) {
		return getHibernateTemplate().find("from SecuredObject where groupId=" + groupId);
	}

	@SuppressWarnings("unchecked")
	public SecuredObject getForItemAndGroup(int objectId, int groupId) {
		List<SecuredObject> l = getHibernateTemplate().find("from SecuredObject where directoryItemId=" + objectId + " AND groupId=" + groupId);
		if (l.isEmpty()) {
			return null;
		}
		return (SecuredObject) l.get(0);
	}

}
