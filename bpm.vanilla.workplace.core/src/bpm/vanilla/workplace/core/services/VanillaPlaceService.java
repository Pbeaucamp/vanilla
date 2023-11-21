package bpm.vanilla.workplace.core.services;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.xstream.IXmlActionType;

public interface VanillaPlaceService {
	public static enum ActionType implements IXmlActionType{
		CREATE_USER, CONNECT, GET_PACKAGES, EXPORT_PACKAGE, IMPORT_PACKAGE
	}
	
	/**
	 * This method create a new user and send an email to him for confirmation
	 * 
	 * @param user
	 * @return true if the user has been successfully created
	 * @throws Exception
	 */
	public boolean createUserAndSendMail(IUser user) throws Exception;
	
	/**
	 * This method allows a user to connect to vanilla place
	 * 
	 * @param name
	 * @param password
	 * @return the user corresponding to the name and password
	 * @throws Exception if the user does not exist
	 */
	public IUser authentifyUser(String name, String password) throws Exception;
	
	/**
	 * This method allows to get all the available projects for a specific user
	 * 
	 * @param userId (null if you want all the projects)
	 * @return a array of projects
	 * @throws Exception
	 */
	public List<IProject> getAvailableProjects(Integer userId) throws Exception;
	
	/**
	 * This method allows to export a Vanilla Package
	 * 
	 * @param userId
	 * @param project
	 * @param vanillaPackage
	 * @return an array of projects availables for the user
	 * @throws Exception
	 */
	public List<IProject> exportPackage(int userId, IProject project, IPackage vanillaPackage, InputStream zipFile) throws Exception;
	
	/**
	 * This method allows to import a package
	 * 
	 * @param package the package
	 * @return the InputStream of the .bpmpackage file
	 * @throws Exception
	 */
	public InputStream importPackage(int userId, IPackage packageToImport) throws Exception;
}
