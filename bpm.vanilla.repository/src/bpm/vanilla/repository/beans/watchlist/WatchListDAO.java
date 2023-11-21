package bpm.vanilla.repository.beans.watchlist;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class WatchListDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<WatchList> getForModel(String modelType, int modelId) {
		return (List<WatchList>) getHibernateTemplate().find("from WatchList where modelId=" + modelId + " AND type='" + modelType + "'");
	}

	@SuppressWarnings("unchecked")
	public List<WatchList> getForUserId(int userId) {
		return (List<WatchList>) getHibernateTemplate().find("from WatchList where creatorId=" + userId);
	}

	public int save(WatchList d) {
		d.setId((Integer) getHibernateTemplate().save(d));
		return d.getId();
	}

	public void delete(WatchList d) {
		getHibernateTemplate().delete(d);
	}

	public void update(WatchList d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public WatchList findById(String id) {
		List<WatchList> l = (List<WatchList>) getHibernateTemplate().find("from WatchList where Id=" + id);

		if (l == null || l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	@SuppressWarnings("unchecked")
	public WatchList find(String id, String id2) {
		List<WatchList> l = (List<WatchList>) getHibernateTemplate().find("from WatchList where creatorId=" + id + "AND directoryItemId=" + id2);

		if (l == null || l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}
}
