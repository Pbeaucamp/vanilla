package bpm.vanilla.repository.beans.datasprovider;

import java.util.List;

import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DatasProviderDAO extends HibernateDaoSupport {

	public List<DatasProvider> findAll() {
		return getHibernateTemplate().find("from DatasProvider");
	}

	public DatasProvider findById(int id) {
		List<DatasProvider> c = getHibernateTemplate().find("from DatasProvider d where d.id=" + id);
		if (c != null && c.size() > 0) {
			return (DatasProvider) c.get(0);
		}
		else {
			return null;
		}
	}

	public int save(DatasProvider d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(DatasProvider d) {
		getHibernateTemplate().delete(d);
	}

	public void update(DatasProvider d) {
		getHibernateTemplate().update(d);
	}
}
