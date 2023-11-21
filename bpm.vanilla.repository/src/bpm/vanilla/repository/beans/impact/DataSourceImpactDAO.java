package bpm.vanilla.repository.beans.impact;

import java.util.List;

import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DataSourceImpactDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<DataSourceImpact> getForDataSourceId(int id) {
		return getHibernateTemplate().find("from DataSourceImpact where datasourceId=" + id);
	}

	public void add(DataSourceImpact i) {
		getHibernateTemplate().save(i);
	}

	public void add(DataSource ds, RepositoryItem it) {
		DataSourceImpact dsi = new DataSourceImpact();
		dsi.setDatasourceId(ds.getId());
		dsi.setDirectoryItemId(it.getId());
		getHibernateTemplate().save(dsi);
	}

	public void delete(DataSourceImpact i) {
		getHibernateTemplate().delete(i);
	}

	public void deleteFor(RepositoryItem it) {
		for (Object dsi : getHibernateTemplate().find("from DataSourceImpact where directoryItemId=" + it.getId())) {
			delete((DataSourceImpact) dsi);
		}
	}

	public void deleteFor(DataSource ds) {
		for (Object dsi : getHibernateTemplate().find("from DataSourceImpact where datasourceId=" + ds.getId())) {
			delete((DataSourceImpact) dsi);
		}
	}

	/**
	 * create a link for the given ids
	 * 
	 * @param directoryItemId
	 * @param dataSourceId
	 */
	public void create(int directoryItemId, int dataSourceId) {
		DataSourceImpact d = new DataSourceImpact();
		d.setDatasourceId(dataSourceId);
		d.setDirectoryItemId(directoryItemId);
		getHibernateTemplate().save(d);

	}

	@SuppressWarnings("unchecked")
	public List<DataSourceImpact> getForItemId(int directoryItemId) {
		return getHibernateTemplate().find("from DataSourceImpact where directoryItemId=" + directoryItemId);
	}

}
