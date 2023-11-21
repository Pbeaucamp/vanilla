package bpm.smart.runtime;

import bpm.smart.runtime.dao.SmartDao;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.exceptions.PasswordException;
import bpm.vanilla.platform.core.exceptions.UserNotFoundException;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.workflow.commons.remote.IAdminManager;

public class AdminManager extends AbstractManager implements IAdminManager {

	private SmartDao resourceDao;

	public AdminManager(SmartManagerComponent component) {
		super(component);
	}

	@Override
	protected void init() throws Exception {
		this.resourceDao = getComponent().getSmartDao();
	}

	private void updateUser(User user, String locale) {
		user.setLocale(locale);
		resourceDao.manageUser(user, true);
	}

	@Override
	public User login(String login, String password, String locale) throws Exception {
		User user = resourceDao.getUser(login);

		if (user == null) {
			throw new UserNotFoundException(login);
		}

		// Encode the password
		String md5password = password;
		if (!password.matches("[0-9a-f]{32}")) {
			md5password = MD5Helper.encode(password);
		}

		if (!user.getPassword().equalsIgnoreCase(md5password)) {
			throw new PasswordException();
		}
		
		updateUser(user, locale);

		return user;
	}

	@Override
	public String connect(User user) throws Exception {
		return null;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}
}
