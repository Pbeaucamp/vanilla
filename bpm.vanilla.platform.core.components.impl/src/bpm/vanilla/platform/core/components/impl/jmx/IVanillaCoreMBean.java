package bpm.vanilla.platform.core.components.impl.jmx;

import java.util.List;

/**
 * An interface to limit bean operations.
 * 
 * Defines vanilla specific features for the core bean, outside
 * the purely bean like ones.
 * For example, provides a registration and retrieval for beans
 * 
 * XXX todo, distributed runtime compliant
 * @author manu
 *
 */
public interface IVanillaCoreMBean {

	/**
	 * register the given bean
	 * 
	 * @param componentBean
	 */
	public void registerBean(IVanillaComponentMBean componentBean);
	
	//no unregister
	
	public List<IVanillaComponentMBean> listRegisteredBeans();
}
