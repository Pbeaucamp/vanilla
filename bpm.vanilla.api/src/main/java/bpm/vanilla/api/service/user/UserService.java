package bpm.vanilla.api.service.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;


public class UserService {

	private IVanillaAPI vanillaApi;
	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager vanillaRepositoryManager;

	
	public UserService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
	
		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		this.vanillaApi = vanillaApi;
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
		this.vanillaRepositoryManager = vanillaApi.getVanillaRepositoryManager();

		
	}
	
	
	public List<User> getUsers()  {
		List<User> users;
		try {
			users = vanillaSecurityManager.getUsers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USERS_NOT_FOUND);
		}
		return users; 
	}
	
	

	
	
	public User addUser(String name,String login, String password, String mail) {
		if (name.equals("") || login.equals("") || password.equals("")|| mail.equals("")) {
			throw new VanillaApiException(VanillaApiError.MISSING_PARAMETERS);
		}
		User user = new User();
		user.setName(name);
		user.setLogin(login);
		user.setPassword(password);
		user.setBusinessMail(mail);
		try {
			vanillaSecurityManager.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.DUPLICATE_USER);
		}
		
		RepositoryService repositoryService = new RepositoryService();
		

		List<Repository> repositories = repositoryService.getRepositories();

		for (Repository repo : repositories) {
			repositoryService.addUserToRepository(login, String.valueOf(repo.getId()));
		}
		
		return user;
	}
	
	
	public String removeUser(String login) {
		if (login.equals("system")) {
			throw new VanillaApiException(VanillaApiError.UNABLE_DELETE_USER);
		}
		
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(login);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		List<Group> userGroups;
		try {
			userGroups = vanillaSecurityManager.getGroups(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_GROUPS_NOT_FOUND);
		}

		List<Group> fGroups = userGroups.stream().filter(g -> (g.getName().equals("System")) ).collect(Collectors.toList());

		
		if ( fGroups.size() > 0 ) {
			throw new VanillaApiException(VanillaApiError.UNALBE_DELETE_ADMIN_USER);
		}
		try {
			vanillaSecurityManager.delUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_DELETE_USER);
		}
		
		return ("Successfully deleted user " + login+".");
	}
	
	
	
	public List<Group> getUserGroups(String login) {
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(login);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		List<Group> groups;
		try {
			groups = vanillaSecurityManager.getGroups(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_GROUPS_NOT_FOUND);
		}
		return groups;
	}
	
	
	public List<Repository> getUserRepositories(String login) {
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(login);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		try {
			return vanillaRepositoryManager.getUserRepositories(user.getLogin());
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_REPOSITORIES_NOT_FOUND);
		}
	}
	
	public User modifyUserPassword(String login, String password) {
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(login);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		user.setPassword(UserService.encodeMD5(password));
		user.setDatePasswordModification(new Date());
		user.setPasswordChange(0);
		user.setPasswordReset(0);
		
		try {
			vanillaSecurityManager.updateUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_UPDATE_USER);
		}
		
		return user;
	}
	

	public static String encodeMD5(String value){
		byte[] uniqueKey = value.getBytes();
		byte[] hash = null;
	
		try {
			// on récupère un objet qui permettra de crypter la chaine
			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
		}catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");
		}
		
		StringBuffer hashString = new StringBuffer();
	
		for (int i = 0; i < hash.length; ++i){
			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			}
			else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}
	
		return hashString.toString();
	}	
	
	
}
