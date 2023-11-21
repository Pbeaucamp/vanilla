package bpm.freemetrics.api.features.favorites;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class WatchListDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<WatchList> findAll() {
		return getHibernateTemplate().find("from WatchList");
	}


	@SuppressWarnings("unchecked")
	public WatchList findByPrimaryKey(int key) {
		List<WatchList> c = getHibernateTemplate().find("from WatchList d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(WatchList d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from WatchList");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(WatchList d) {
		getHibernateTemplate().delete(d);
	}
	public void update(WatchList d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<WatchList> getWatchlistByUserId(int userId) {
		return getHibernateTemplate().find("from WatchList d where d.mwUserId=" + userId);
	}


	@SuppressWarnings("unchecked")
	public List<WatchList> getWatchlistForUserIdAppIdAndMetrId(int userId,int appId, int metrId) {
		return getHibernateTemplate().find("from WatchList d where d.mwUserId=" +userId+" AND d.mwApplicationId = "+appId+" AND d.mwMetricsId = "+metrId);
	}	

}
