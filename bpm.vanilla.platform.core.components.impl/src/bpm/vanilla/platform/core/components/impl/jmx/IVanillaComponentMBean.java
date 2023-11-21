package bpm.vanilla.platform.core.components.impl.jmx;

import bpm.vanilla.platform.core.components.IVanillaComponent.Status;

/**
 * An interface to limit bean operations.
 * 
 * Defines vanilla specific features, outside
 * the purely bean like ones.
 * For example, provides a unique name to identify registered beans
 * 
 * XXX todo, distributed runtime compliant
 * 
 * @author manu
 *
 */
public interface IVanillaComponentMBean {

	/**
	 * Such as :
	 * bpm.vanilla.platform.core.components.type.GatewayComponent
	 * bpm.vanilla.platform.core.components.type.ReportingComponent
	 * ...
	 * @return
	 */
	public String getName();
	
	/**
	 * will provide the vanilla status of the underlying component
	 * @return
	 */
	public Status getStatus();
}
