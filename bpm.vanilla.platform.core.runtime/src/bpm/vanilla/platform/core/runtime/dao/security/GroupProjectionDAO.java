package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.List;

import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GroupProjectionDAO extends HibernateDaoSupport {

	public List<GroupProjection> findAll() {
		return getHibernateTemplate().find("from GroupProjection");
	}

	public GroupProjection findById(int id) {
		return (GroupProjection) getHibernateTemplate().find("from GroupProjection where id = " + id).get(0);
	}

	public List<GroupProjection> findByFasdId(int fasdId) {
		return getHibernateTemplate().find("from GroupProjection where fasdId = " + fasdId);
	}

	public List<GroupProjection> findByGroupId(int groupId) {
		return getHibernateTemplate().find("from GroupProjection where groupId = " + groupId);
	}

	public GroupProjection findByGroupIdFasdId(int groupId, int fasdId) {
		List lst = getHibernateTemplate().find("from GroupProjection where fasdId = " + fasdId + " And groupId = " + groupId);
		if(lst != null && lst.size() > 0) {
			return (GroupProjection) lst.get(0);
		}
		return null;
	}

	public int save(GroupProjection gp) {
		getHibernateTemplate().save(gp);
		return gp.getId();
	}

	public void delete(GroupProjection gp) {
		getHibernateTemplate().delete(gp);
	}

	public void update(GroupProjection gp) {
		getHibernateTemplate().update(gp);
	}

}
