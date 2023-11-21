package bpm.vanillahub.runtime;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanillahub.core.exception.UserNotFoundException;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.workflow.commons.remote.IAdminManager;

public class AdminManager extends AbstractManager implements IAdminManager {

	private ResourceDao resourceDao;

	public AdminManager(ComponentVanillaHub component) {
		super(component);
	}

	@Override
	protected void init() throws Exception {
		this.resourceDao = getComponent().getResourceDao();
	}

	private void updateUser(User user, String locale) {
		user.setLocale(locale);
		resourceDao.manageUser(user, true);
	}
	
	private String getVanillaUrl() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		return config.getProperty(VanillaConfiguration.P_VANILLA_URL);
	}

	@Override
	public User login(String login, String password, String locale) throws Exception {
		//Getting user from vanilla and login
		//If exist update in DB
		String vanillaUrl = getVanillaUrl();
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaUrl, login, password);
		User user = vanillaApi.getVanillaSecurityManager().authentify(null, login, password, false);

		if (user == null) {
			throw new UserNotFoundException(login);
		}
		
		User userHub = resourceDao.getUser(login);
		
		if (userHub == null) {
			userHub = new User();
			userHub.setLogin(user.getLogin());
			userHub.setLocale(user.getLocale());
			userHub.setName(user.getName());
		}
		
		updateUser(userHub, locale);

		return user;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public String connect(User user) throws Exception {
		return null;
	}
}
