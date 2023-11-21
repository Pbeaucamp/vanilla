package bpm.workflow.runtime.model;

import bpm.workflow.runtime.resources.IResource;

/**
 * Interface of the KPI Activities
 * @author Charles MARTIN
 *
 */
public interface IFreeMetricsServer extends IServer{
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
