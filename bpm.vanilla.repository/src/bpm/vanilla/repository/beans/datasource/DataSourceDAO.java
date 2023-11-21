package bpm.vanilla.repository.beans.datasource;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DataSourceDAO extends HibernateDaoSupport {

	public List<DataSource> getAll() {
		return getHibernateTemplate().find("from DataSource");
	}

	public DataSource getById(int id) {
		List<DataSource> l = getHibernateTemplate().find("from DataSource where id=" + id);

		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	public void update(DataSource ds) {
		getHibernateTemplate().update(ds);
	}

	public void delete(DataSource ds) {
		getHibernateTemplate().delete(ds);
	}

	public int add(DataSource ds) {
		return (Integer) getHibernateTemplate().save(ds);
	}

	public List<DataSource> getByType(String extensionId) {
		List<DataSource> datasources = getHibernateTemplate().find("from DataSource");
		List<DataSource> filtered = new ArrayList<DataSource>();
		for(DataSource ds : datasources) {
			if(ds.getOdaExtensionId().equals(extensionId)) {
				filtered.add(ds);
			}
		}
		return filtered;
	}
}
