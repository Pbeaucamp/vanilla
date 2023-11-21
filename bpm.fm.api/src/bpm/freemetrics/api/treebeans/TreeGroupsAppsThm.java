/**
 * 
 */
package bpm.freemetrics.api.treebeans;

import java.util.HashMap;
import java.util.Map;

import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.utils.IConstants;

/**
 * @author Belgarde
 *
 */
public class TreeGroupsAppsThm {

	Group group;
	Map<Integer,TreeAppsThm> treeApps = new HashMap<Integer,TreeAppsThm>();
	
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
	/**
	 * @return the treeApps
	 */
	public Map<Integer,TreeAppsThm> getTreeApps() {
		return treeApps;
	}
	
	public boolean addTreeApp(int key,TreeAppsThm treeApp) {
		if(key == IConstants.OBJECTISNULL || treeApps.containsKey(key)){
			return false;
		}else{
			treeApps.put(key,treeApp);
			return true;
		}
	}
	
	public int getId(){
		return group != null ? group.getId() : IConstants.OBJECTISNULL;
	}
	public boolean hasChildren() {
		return ! treeApps.isEmpty();
	}
}
