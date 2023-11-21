package bpm.vanilla.platform.core.runtime.dao.logs;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VanillaLogsDAO extends HibernateDaoSupport {

	public void update(VanillaLogs d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<VanillaLogs> findAll() {
		return getHibernateTemplate().find("from VanillaLogs");
	}

	public int save(VanillaLogs d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	@SuppressWarnings("unchecked")
	public VanillaLogs findByPrimaryKey(int key) {
		List<VanillaLogs> c = getHibernateTemplate().find("from VanillaLogs d where d.id=" + key);
		if (c != null && c.size() > 0) {
			return c.get(0);
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<VanillaLogs> getListVanillaLogs(int itemId, String type) {
		List<VanillaLogs> c = getHibernateTemplate().find("from VanillaLogs where directoryItemId=" + itemId + " and objectType = '" + type + "'");
		return c;
	}

}
