package bpm.vanilla.api.service.user;

import java.util.List;
import java.util.stream.Collectors;

import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;

public class RepositoryService {
	
//	private int publicGroupId;
	
	private IVanillaAPI vanillaApi;
	private IRepositoryManager repositoryManager;
	private IVanillaSecurityManager vanillaSecurityManager;
	
	public RepositoryService(IVanillaAPI vanillaApi) {
		this.vanillaApi = vanillaApi;
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}
	
	public RepositoryService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
//		this.publicGroupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
	
		IVanillaContext vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		this.vanillaApi = vanillaApi;
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();
		this.vanillaSecurityManager = this.vanillaApi.getVanillaSecurityManager();
	}
	
	
	
	public List<Repository> getRepositories() {
		try {
			return repositoryManager.getRepositories();
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORIES_NOT_FOUND);
		} 
	}

	
	public UserRep addUserToRepository(String userLogin, String repositoryID) {
		User user;
		try {
			user = vanillaSecurityManager.getUserByLogin(userLogin);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.USER_NOT_FOUND);
		}
		
		Repository repository;
		try {
			repository = repositoryManager.getRepositoryById(Integer.parseInt(repositoryID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}

		
		List<Repository> userReps;
		try {
			userReps = repositoryManager.getUserRepositories(user.getLogin());
		} catch (Exception e) {
			e.printStackTrace(); 
			throw new VanillaApiException(VanillaApiError.USER_REPOSITORIES_NOT_FOUND);
		}
		
		// Vérification que le lien entre user et repository n'existe pas déjà
		int repId = repository.getId();
		List<Repository> fReps = userReps.stream().filter(r -> r.getId() == repId).collect(Collectors.toList());
		if (fReps.size()>0) {  
			throw new VanillaApiException(VanillaApiError.USER_ALREADY_IN_REPOSITORY);
		}

		UserRep userRep = new UserRep();
		userRep.setRepositoryId(repository.getId());
		userRep.setUserId(user.getId());
		try {
			repositoryManager.addUserRep(userRep);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_ADD_USERREP);
		}
		return userRep;
	}
	
	public RepositoryDirectory getDirectoryByName(int repositoryId, int groupId, int parentId, String directoryName) {
		IRepositoryApi repositoryApi = getRepositoryApi(repositoryId, groupId);
		try {
			RepositoryDirectory parent = repositoryApi.getRepositoryService().getDirectory(parentId);
			List<IRepositoryObject> dirs = repositoryApi.getRepositoryService().getDirectoryContent(parent, IRepositoryService.ONLY_DIRECTORY);
			if (dirs != null) {
				for (IRepositoryObject item : dirs) {
					if (item instanceof RepositoryDirectory && item.getName().equals(directoryName)) {
						return (RepositoryDirectory) item;
					}
				}
			}
			throw new VanillaApiException(VanillaApiError.DIRECTORY_NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.DIRECTORY_NOT_FOUND);
		}
	}
	
	public void addDirectory(int repositoryId, int groupId, int parentId, String directoryName) {
		IRepositoryApi repositoryApi = getRepositoryApi(repositoryId, groupId);
		try {
			RepositoryDirectory parent = repositoryApi.getRepositoryService().getDirectory(parentId);
			repositoryApi.getRepositoryService().addDirectory(directoryName, "", parent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_ADD_DIRECTORY);
		}
	}
	
	public void addGroupToDirectory(int repositoryId, int groupId, int directoryId) {
		IRepositoryApi repositoryApi = getRepositoryApi(repositoryId, groupId);
		try {
			repositoryApi.getAdminService().addGroupForDirectory(groupId, directoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_TO_ADD_GROUP_TO_DIRECTORY);
		}
	}
	
	private IRepositoryApi getRepositoryApi(int repositoryId, int groupId) {
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(groupId);
			if (group == null) {
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		
		Repository repository = null;
		try {
			repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			if (repository == null) {
				throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		BaseRepositoryContext ctx = new BaseRepositoryContext(vanillaApi.getVanillaContext(), group, repository);
		return new RemoteRepositoryApi(ctx);
	}

}
