package bpm.freemetrics.api.features.favorites;

import java.util.List;

public class UserActionManager {
	private UserActionDAO dao;

	public UserActionManager() {
		super();
	}

	public void setDao(UserActionDAO d) {
		this.dao = d;
	}

	public UserActionDAO getDao() {
		return dao;
	}

	public List<UserAction> getUserActions() {
		return dao.findAll();
	}

	public UserAction getUserActionById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addUserAction(UserAction d) throws Exception{

		return dao.save(d);
	}

	public boolean delUserAction(UserAction d) {
		dao.delete(d);
		return true;
	}

	public boolean updateUserAction(UserAction d) throws Exception {
		boolean updated = false;
		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
			updated = true;
		}else{
			throw new Exception("This UserAction doesnt exists");
		}

		return updated;
	}

	public List<UserAction> getUserActionByUserId(int userId) {

		return dao.getUserActionByUserId(userId);
	}

}
