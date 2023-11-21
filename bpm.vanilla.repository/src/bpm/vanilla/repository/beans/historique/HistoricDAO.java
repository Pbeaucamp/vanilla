package bpm.vanilla.repository.beans.historique;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class HistoricDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Historic> getForUser(int userId) {
		return (List<Historic>) getHibernateTemplate().find("FROM Historic WHERE userId=" + userId + " ORDER BY dateHistoric DESC", 10);
//		return (List<Historic>) getHibernateTemplate().find("FROM Historic WHERE userId=" + userId);
	}

	public void purgeForUser(int userId) {
		for (Historic o : getForUser(userId)) {
			delete(o);
		}
	}

	public int save(Historic d) throws Exception {
		d.setId((Integer) getHibernateTemplate().save(d));
		return d.getId();
	}

	public void delete(Historic d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Historic d) {
		getHibernateTemplate().update(d);
	}
}
