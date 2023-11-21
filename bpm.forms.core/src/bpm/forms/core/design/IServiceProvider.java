package bpm.forms.core.design;

import bpm.forms.core.runtime.IInstanceService;

/**
 * A simple interface to store the different IFormService 
 * 
 * 
 * 
 * @author ludo
 *
 */
public interface IServiceProvider {
	public IDefinitionService getDefinitionService();

	public IInstanceService getInstanceService();

	/**
	 * may be used to perform some specific inits
	 * @param object
	 */
	public void configure(Object object);
	
	
}
