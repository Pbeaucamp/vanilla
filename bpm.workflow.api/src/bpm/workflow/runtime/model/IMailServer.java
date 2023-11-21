package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;
/**
 * Interface of the Mail activities
 * @author CHARBONNIER, MARTIN
 *
 */
public interface IMailServer extends IServer{
	
	/**
	 * @see IServer
	 */
	public void setServer(IResource server);
	
	/**
	 * @see IServer
	 */
	public IResource getServer();
	
	/**
	 * @see IServer
	 */
	public Class<?> getServerClass();

}
