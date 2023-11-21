package bpm.vanilla.workplace.client.services;

import java.util.List;

import bpm.vanilla.workplace.shared.exceptions.ServiceException;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("AdminService")
public interface AdminService extends RemoteService {
	
	public static class Connect{
		private static AdminServiceAsync instance;
		
		public static AdminServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(AdminService.class);
			}
			return instance;
		}
	}
	
	public PlaceWebUser authentifyUser(String name, String password) throws ServiceException;
	
	public void deleteUser(int userId) throws ServiceException;
	
	public List<PlaceWebUser> getAllUser() throws ServiceException;
	
	public List<PlaceWebPackage> getAllPackages() throws ServiceException;
	
	public List<PlaceWebPackage> getPackageForUser(int userId) throws ServiceException;
	
	public void allowUserForPackage(int packageId, List<PlaceWebUser> users) throws ServiceException;
	
	public void deleteLinkPackageForUser(int userId, int packageId) throws ServiceException;
	
	public void createUser(PlaceWebUser user) throws ServiceException;
	
	public void updateUser(PlaceWebUser user) throws ServiceException;
	
	public List<PlaceWebProject> getProjects() throws ServiceException;
	
	public List<PlaceWebUser> getUsersAvailable(int packageId) throws ServiceException;
	
	public void deleteProject(int userId, int projectId) throws ServiceException;
	
	public void deletePackage(int userId, int projectId, int packageId) throws ServiceException;
	
	public void addProject(int userId, PlaceWebProject project) throws ServiceException;
	
	public void addPackage(PlaceWebPackage newPackage) throws ServiceException;
}
