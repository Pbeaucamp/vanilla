package bpm.freemetrics.api.manager;

import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;

public interface IDynamicAxeLoading {

	/**
	 * Get the groups (the root of the tree)
	 * @param userId
	 * @param isSuperUser
	 * @return
	 */
	public List<Group> getGroups(int userId, boolean isSuperUser) throws Exception;
	
	/**
	 * 
	 * Get the children for an application or a group 
	 * @param parent
	 * @param start the index of the first application to load
	 * @param end the index of the last application to load
	 * @return
	 */
	public List<Application> loadChildren(Object parent, int start, int end) throws Exception;
	
	/**
	 * Get the applications for this level using a filter
	 * @param parent
	 * @param filter match with a part of the application name
	 * @param start the index of the first application to load
	 * @param end the index of the last application to load
	 * @return
	 */
	public List<Application> getApplicationForFilter(Object parent, String filter, int start, int end) throws Exception;
	
	/**
	 * Get the previous application level
	 * @param child if it's the first level use getGroups
	 * @param start the index of the first application to load
	 * @param end the index of the last application to load
	 * @return
	 */
	public List<Application> getParentApplications(Application parent, int start, int end) throws Exception;
	
	/**
	 * Return the number of children
	 * @param parent
	 * @return
	 */
	public int getChildrenCount(Object parent) throws Exception;

	public int getChildrenCount(Object previousParent, String text);
	
	/**
	 * A really shitty method to refresh the table
	 * This resetting all the elements kept in cache because it's to 
	 * complicated to find the elements we want to reset in all those maps....
	 */
	public void resetCache();
}
