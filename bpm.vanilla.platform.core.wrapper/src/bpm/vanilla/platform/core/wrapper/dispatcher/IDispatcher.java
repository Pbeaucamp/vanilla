package bpm.vanilla.platform.core.wrapper.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.exceptions.VanillaException;

public interface IDispatcher {

	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	public boolean needAuthentication();
	
	/**
	 * this method should be call to validate if the conf.getObjectIDentifier() can be run for 
	 * conf.getGroupId()
	 * 
	 * All runtime Component should call the method first to make sure that
	 * the item can be Run.
	 * 
	 * If the group has no right to run the object a VanillaException is thrown
	 * 
	 * @param conf
	 * @throws VanillaException
	 * @throws Exception 
	 */
	public void canBeRun(IRuntimeConfig conf) throws VanillaException, Exception;
	
}
