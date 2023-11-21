package bpm.vanilla.repository.beans.generated;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GenerateObjectDAO extends HibernateDaoSupport {

	public int save(GeneratedObject d) {
		List<GeneratedObject> l = (List<GeneratedObject>) getHibernateTemplate().find("from GeneratedObject where groupId=" + d.getGroupId() + " AND directoryItemId=" + d.getDirectoryItemId());
		if (l == null || l.isEmpty()) {
			d.setId((Integer) getHibernateTemplate().save(d));
			return d.getId();
		}
		else {
			for (GeneratedObject g : l) {
				delete(g);
			}
			d.setId((Integer) getHibernateTemplate().save(d));
			return d.getId();
		}

	}

	public void delete(GeneratedObject d) {
		getHibernateTemplate().delete(d);
	}

	public void update(GeneratedObject d) {
		getHibernateTemplate().update(d);
	}

	public List<GeneratedObject> getForGroupId(int groupId) {
		return (List<GeneratedObject>) getHibernateTemplate().find("from GeneratedObject where groupId=" + groupId);

	}

	public GeneratedObject getForItemAndGroup(int directoryItemId, Integer groupId) {
		List<GeneratedObject> l = (List<GeneratedObject>) getHibernateTemplate().find("from GeneratedObject where groupId=" + groupId + " AND directoryItemId=" + directoryItemId);

		if (l.isEmpty()) {
			return null;
		}
		else {
			return l.get(0);
		}

	}
}
