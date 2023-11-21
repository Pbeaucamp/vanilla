package bpm.vanilla.user.api.service;

import java.util.List;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;

public class RepositoryService {
	private IVanillaAPI vanillaApi;
	private IRepositoryManager repositoryManager;
	private IVanillaSecurityManager vanillaSecurityManager;
	
	public RepositoryService(IVanillaAPI vanillaApi) {
		this.vanillaApi = vanillaApi;
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}
	
	public List<Repository> getRepositories() throws Exception {
		return repositoryManager.getRepositories(); 
	}
	
	public void addUserToRepository(int userId, int repositoryID) throws Exception {
		if (repositoryManager.getRepositoryById(repositoryID) == null) {
			throw new Exception("Repository not found.");
		}
		
		if (vanillaSecurityManager.getUserById(userId) == null) {
			throw new Exception("User not found.");
		}
		
		UserRep userRep = new UserRep();
		userRep.setRepositoryId(repositoryID);
		userRep.setUserId(userId);
		repositoryManager.addUserRep(userRep);
	}
	
	public void addUserToRepository(String userLogin, String repositoryName) throws Exception {
		User user = vanillaSecurityManager.getUserByLogin(userLogin);
		if (user == null) {
			throw new Exception("User not found.");
		}
		Repository repository = repositoryManager.getRepositoryByName(repositoryName);
		if (repository == null) {
			throw new Exception("Repository not found.");
		}
		UserRep userRep = new UserRep();
		userRep.setRepositoryId(repository.getId());
		userRep.setUserId(user.getId());
		repositoryManager.addUserRep(userRep);
	}

}
