package bpm.freemetrics.api.features.favorites;

import java.util.List;

public class UserPreferenceManager {
	private UserPreferenceDAO dao;

	public UserPreferenceManager() {
		super();
	}

	public void setDao(UserPreferenceDAO d) {
		this.dao = d;
	}

	public UserPreferenceDAO getDao() {
		return dao;
	}

	public List<UserPreference> getUserPreferences() {
		return dao.findAll();
	}

	public UserPreference getUserPreferenceById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addUserPreference(UserPreference d) throws Exception{

		return dao.save(d);
	}

	public boolean delUserPreference(UserPreference d) {
		dao.delete(d);
		return true;
	}

	public boolean updateUserPreference(UserPreference d) throws Exception {
		boolean updated = false;
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			updated = true;
		}else{
			throw new Exception("This UserPreference doesnt exists");
		}

		return updated;
	}

	public List<UserPreference> getUserPreferenceByUserId(int userId) {

		return dao.getUserPreferenceByUserId(userId);
	}

}
