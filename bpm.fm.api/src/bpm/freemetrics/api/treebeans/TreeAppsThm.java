/**
 * 
 */
package bpm.freemetrics.api.treebeans;

import java.util.HashMap;
import java.util.Map;

import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.utils.IConstants;

/**
 * @author Belgarde
 *
 */
public class TreeAppsThm {

	Application app;
	
	Map<Integer,TreeThm> treeThms = new HashMap<Integer,TreeThm>();

	/**
	 * @return the app
	 */
	public Application getApp() {
		return app;
	}

	/**
	 * @param app the app to set
	 */
	public void setApp(Application app) {
		this.app = app;
	}

	/**
	 * @return the treeApps
	 */
	public Map<Integer, TreeThm> getTreeThms() {
		return treeThms;
	}
	
	public boolean addTreeThm(int key,TreeThm treeThm) {
		if(key == IConstants.OBJECTISNULL || treeThms.containsKey(key)){
			return false;
		}else{
			treeThms.put(key,treeThm);
			return true;
		}
	}
	
	public int getId(){
		return app != null ? app.getId() : IConstants.OBJECTISNULL;
	}
	
}
