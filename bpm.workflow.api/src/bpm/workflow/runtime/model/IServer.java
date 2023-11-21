package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface of the activities which use a server
 * @author CHARBONNIER, MARTIN
 *
 */
public interface IServer {
	
	/**
	 * Set the server which will be linked to this activity
	 * @param server
	 */
	public void setServer(IResource server);
	
	/**
	 * 
	 * @return the server linked to this activity
	 */
	public IResource getServer();
	
	/**
	 * 
	 * @return the class of the server
	 */
	public Class<?> getServerClass();

}
