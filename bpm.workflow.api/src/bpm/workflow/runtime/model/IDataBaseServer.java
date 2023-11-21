package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface of the DataBases servers
 * @author CHARBONNIER,MARTIN
 *
 */
public interface IDataBaseServer extends IServer{
	
	/**
	 * Set the server 
	 */
	public void setServer(IResource server);
	
	/**
	 * @return the server
	 */
	public IResource getServer();
	
	/**
	 * @return the class of the server
	 */
	public Class<?> getServerClass();

}
