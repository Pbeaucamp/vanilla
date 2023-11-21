package bpm.vanilla.platform.core.runtime.dao.platform;

import java.util.List;

import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class RepositoryDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Repository> findAll() {
		List<Repository> repositories = getHibernateTemplate().find("from Repository");
		if (repositories != null) {
			for (Repository rep : repositories) {
				rep.setUrl(ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(rep.getUrl()));
			}
		}
		return repositories;
	}

	@SuppressWarnings("unchecked")
	public Repository findByPrimaryKey(int key) {
		List<Repository> c = getHibernateTemplate().find("from Repository d where d.id=" + key);
		if (c != null && c.size() > 0) {
			Repository rep = (Repository) c.get(0);
			rep.setUrl(ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(rep.getUrl()));
			return rep;
		}
		else {
			return null;
		}
	}

	public void save(Repository d) {
		int id = (Integer) getHibernateTemplate().save(d);
		d.setId(id);
	}

	public void delete(Repository d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Repository d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public Repository findForName(String name) {
		List<Repository> c = getHibernateTemplate().find("from Repository where name='" + name + "'");
		if (c != null && c.size() > 0) {
			Repository rep = (Repository) c.get(0);
			rep.setUrl(ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(rep.getUrl()));
			return rep;
		}
		else {
			return null;
		}
	}
}
