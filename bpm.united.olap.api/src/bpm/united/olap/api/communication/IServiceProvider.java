package bpm.united.olap.api.communication;

import bpm.vanilla.platform.core.IVanillaContext;



/**
 * Interface to store service providers
 * @author Marc Lanquetin
 *
 */
public interface IServiceProvider {

	IRuntimeService getRuntimeProvider();
	
	IModelService getModelProvider();
	
//	IUnitedOlapCacheManager getCacheManager();
	
	/**
	 * expected IVanillaContext
	 */
	void configure(IVanillaContext ctxs);
}
