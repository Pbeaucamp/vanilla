package bpm.vanilla.repository.beans.log;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class LogDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<RepositoryLog> findAll() {
		return getHibernateTemplate().find("from RepositoryLog");
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryLog> findAll(int startId, int num) {
		List<RepositoryLog> l = null;

		Session s = null;
		try {
			s = getHibernateTemplate().getSessionFactory().openSession();

			Query q = s.createQuery("from RepositoryLog where id>=" + startId + " order by id asc ");
			q.setFirstResult((int) startId + 1);
			q.setMaxResults(num);
			l = q.list();
		} catch (Exception ex) {
			ex.printStackTrace();
			l = getHibernateTemplate().find("from RepositoryLog where id>=" + startId + " order by id " + " asc " + num);
		} finally {
			if (s != null) {
				s.close();
			}
		}

		return l;
	}

	@SuppressWarnings("unchecked")
	public RepositoryLog findByModelId(int modelId) {
		List<RepositoryLog> l = getHibernateTemplate().find("from RepositoryLog d where id=" + modelId);

		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	public int save(RepositoryLog d) {
		int i = (Integer) getHibernateTemplate().save(d);
		getHibernateTemplate().flush();
		return i;
	}

	public void delete(RepositoryLog d) {
		getHibernateTemplate().update(d);
	}

	public void update(RepositoryLog d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryLog> findByItemId(int id) {
		return getHibernateTemplate().find("from RepositoryLog d where objectId=" + id);
	}

}
