package bpm.vanilla.platform.core.runtime.tools;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.IVanillaSystemManager;

/**
 * shitty class, no other choice for now because multiple commiter on the
 * bpm.vanilla.core.wrapper, but the projects bpm.vanilla.core.wrapper and bpm.vanilla.runtime
 * should be me merged and the component VanillaCoreWrapper should become a Singleton
 * to be able to use all unitary component 
 * 
 * 
 * @author ludo
 *
 */
public class OSGIHelper {
	private static IRepositoryManager repositoryService;
	private static IVanillaSystemManager systemService;
	private static IVanillaSecurityManager secuService;
	
	public static void register(Object service){
		if (service instanceof IRepositoryManager){
			repositoryService = (IRepositoryManager)service;
		}
		if (service instanceof IVanillaSystemManager){
			systemService = (IVanillaSystemManager)service;
		}
		if (service instanceof IVanillaSecurityManager){
			secuService = (IVanillaSecurityManager)service;
		}
		
	}
	public static void unregister(Object service){
		if (service instanceof IRepositoryManager){
			repositoryService = null;
		}
		if (service instanceof IVanillaSystemManager){
			systemService = null;
		}
		if (service instanceof IVanillaSecurityManager){
			secuService = null;
		}
		
	}
	
	/**
	 * the reference should never be hold
	 * @return
	 */
	public static IRepositoryManager getRepositoryManager(){
		return repositoryService;
	}
	
	/**
	 * the reference should never be hold
	 * @return
	 */
	public static IVanillaSystemManager getSystemManager(){
		return systemService;
	}
	
	/**
	 * the reference should never be hold
	 * @return
	 */
	public static IVanillaSecurityManager getSecurityManager(){
		return secuService;
	}
	
}
