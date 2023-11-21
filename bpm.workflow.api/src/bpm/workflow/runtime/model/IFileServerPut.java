package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface of the "put.." activities among the file management activities
 * @author Charles MARTIN
 *
 */
//TODO delete this class and replace it by IFileServer and IFileTreatment
public interface IFileServerPut extends IServer {
	
	/** 
	 * @see Iserver
	 */
	public void setServer(IResource server);
	/**
	 * @see Iserver
	 */
	public IResource getServer();
	/**
	 * @see Iserver
	 */
	public Class<?> getServerClass();


}
