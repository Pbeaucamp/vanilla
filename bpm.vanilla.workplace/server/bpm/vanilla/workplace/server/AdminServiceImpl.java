package bpm.vanilla.workplace.server;

import java.io.File;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.server.helper.TransformObject;
import bpm.vanilla.workplace.server.runtime.PlaceProjectRuntime;
import bpm.vanilla.workplace.server.runtime.PlaceUserRuntime;
import bpm.vanilla.workplace.server.security.WorkplaceSession;
import bpm.vanilla.workplace.server.security.WorkplaceSessionHelper;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		/**
		 * We init the logger
		 */
		try {
			PlaceConfiguration.init(getServletContext().getRealPath(File.separator));
			PlaceConfiguration.getInstance();
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
//	private String escapeHtml(String html) {
//		if (html == null) {
//			return null;
//		}
//		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
//				.replaceAll(">", "&gt;");
//	}

	@Override
	public PlaceWebUser authentifyUser(String name, String password) throws ServiceException {
		PlaceUserRuntime workplaceRuntime = new PlaceUserRuntime();
		IUser user = workplaceRuntime.authentifyUser(name, password, getThreadLocalRequest());
		return TransformObject.transformToWebUser(user);
	}

	@Override
	public void deleteUser(int userId) throws ServiceException {
		PlaceUserRuntime workplaceRuntime = new PlaceUserRuntime();
		workplaceRuntime.deleteUser(userId);
	}

	@Override
	public List<PlaceWebUser> getAllUser() throws ServiceException {
		PlaceUserRuntime workplaceRuntime = new PlaceUserRuntime();
		return workplaceRuntime.getAllUser();
	}

	@Override
	public List<PlaceWebPackage> getPackageForUser(int userId) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		return projRuntime.getPackageForUser(userId);
	}

	@Override
	public List<PlaceWebPackage> getAllPackages() throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		return projRuntime.getAllPackages();
	}

	@Override
	public void allowUserForPackage(int packageId, List<PlaceWebUser> users) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		projRuntime.allowUserForPackage(packageId, users);
	}

	@Override
	public void deleteLinkPackageForUser(int userId, int packageId) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		projRuntime.deleteLinkUserPackage(userId, packageId);
	}

	@Override
	public void createUser(PlaceWebUser user) throws ServiceException {
		PlaceUserRuntime userRuntime = new PlaceUserRuntime();
		userRuntime.createUser(user);
	}

	@Override
	public void updateUser(PlaceWebUser user) throws ServiceException {
		PlaceUserRuntime userRuntime = new PlaceUserRuntime();
		userRuntime.updateUser(user);
	}

	@Override
	public List<PlaceWebProject> getProjects() throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		return projRuntime.getAllProjects();
	}

	@Override
	public List<PlaceWebUser> getUsersAvailable(int packageId) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		return projRuntime.getUsersForPackage(packageId);
	}

	@Override
	public void deletePackage(int userId, int projectId, int packageId) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		projRuntime.deletePackage(userId, projectId, packageId);
	}

	@Override
	public void deleteProject(int userId, int projectId) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		projRuntime.deleteProject(userId, projectId);
	}

	@Override
	public void addProject(int userId, PlaceWebProject project) throws ServiceException {
		PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
		projRuntime.createProject(userId, project);
	}

	@Override
	public void addPackage(PlaceWebPackage newPackage) throws ServiceException {
		WorkplaceSession session = WorkplaceSessionHelper.getCurrentSession(getThreadLocalRequest());
		session.setPackageInformations(newPackage);
	}
}
