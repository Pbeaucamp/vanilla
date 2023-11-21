package bpm.freemetrics.api.features.favorites;

import java.util.List;

public class WatchListManager {
	private WatchListDAO dao;

	public WatchListManager() {
		super();
	}

	public void setDao(WatchListDAO d) {
		this.dao = d;
	}

	public WatchListDAO getDao() {
		return dao;
	}

	public List<WatchList> getWatchLists() {
		return dao.findAll();
	}

	public WatchList getWatchListById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addWatchList(WatchList d) throws Exception{

		return dao.save(d);

	}

	public boolean delWatchList(WatchList d) {
		dao.delete(d);
		return true;
	}

	public boolean updateWatchList(WatchList d) throws Exception {

		boolean updated = false;
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			updated  = true;
		}else{
			throw new Exception("This WatchList doesnt exists");
		}

		return updated;
	}

	public List<WatchList> getWatchlistByUserId(int userId) {

		 return dao.getWatchlistByUserId(userId);
	}

	public List<WatchList> getWatchlistForUserIdAppIdAndMetrId(int userId,
			int appId, int metrId) {

		return dao.getWatchlistForUserIdAppIdAndMetrId(userId, appId, metrId);
	}

}
