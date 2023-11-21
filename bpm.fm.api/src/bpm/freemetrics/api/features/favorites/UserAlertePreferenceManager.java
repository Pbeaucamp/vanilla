package bpm.freemetrics.api.features.favorites;

import java.util.List;

public class UserAlertePreferenceManager {
	private UserAlertePreferenceDAO dao;

	public UserAlertePreferenceManager() {
		super();
	}

	public void setDao(UserAlertePreferenceDAO d) {
		this.dao = d;
	}

	public UserAlertePreferenceDAO getDao() {
		return dao;
	}

	public List<UserAlertePreference> getUserAlertePreferences() {
		return dao.findAll();
	}

	public UserAlertePreference getUserAlertePreferenceById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addUserAlertePreference(UserAlertePreference d) throws Exception{

		return dao.save(d);
	}

	public boolean delUserAlertePreference(UserAlertePreference d) {
		dao.delete(d);
		return true;
	}

	public boolean updateUserAlertePreference(UserAlertePreference d) throws Exception {
		boolean updated = false;

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			updated = true;
		}else{
			throw new Exception("This UserAlertePreference doesnt exists");
		}

		return updated;
	}

	public List<UserAlertePreference> getUserAlertePreferenceByUserId(int userId) {

		return dao.getUserAlertePreferenceByUserId(userId);
	}

}
