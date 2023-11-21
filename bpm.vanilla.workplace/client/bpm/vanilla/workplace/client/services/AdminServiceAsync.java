package bpm.vanilla.workplace.client.services;

import java.util.List;

import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	
	public void authentifyUser(String name, String password, AsyncCallback<PlaceWebUser> callback);
	
	public void deleteUser(int userId, AsyncCallback<Void> callback);

	public void getAllUser(AsyncCallback<List<PlaceWebUser>> callback);
	
	public void getAllPackages(AsyncCallback<List<PlaceWebPackage>> callback);
	
	public void getPackageForUser(int userId, AsyncCallback<List<PlaceWebPackage>> callback);
	
	public void allowUserForPackage(int packageId, List<PlaceWebUser> users, AsyncCallback<Void> callback);
	
	public void deleteLinkPackageForUser(int userId, int packageId , AsyncCallback<Void> callback);
	
	public void createUser(PlaceWebUser user, AsyncCallback<Void> callback);
	
	public void updateUser(PlaceWebUser user, AsyncCallback<Void> callback);
	
	public void getProjects(AsyncCallback<List<PlaceWebProject>> callback);
	
	public void getUsersAvailable(int packageId, AsyncCallback<List<PlaceWebUser>> callback);
	
	public void deleteProject(int userId, int projectId, AsyncCallback<Void> callback);
	
	public void deletePackage(int userId, int projectId, int packageId, AsyncCallback<Void> callback);
	
	public void addProject(int userId, PlaceWebProject project, AsyncCallback<Void> callback);
	
	public void addPackage(PlaceWebPackage newPackage, AsyncCallback<Void> callback);
}
