package bpm.vanilla.platform.core.components.impl;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;


public class VanillaServiceTracker extends ServiceTracker {

	private IVanillaComponentListenerService listenerService;
	
	public VanillaServiceTracker(BundleContext bundleContext, IVanillaComponentListenerService listenerService) {
		super(bundleContext, IVanillaComponent.class.getName(), null);
		
		this.listenerService = listenerService;
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		
		if (o instanceof IVanillaComponent) {
			try {
				listenerService.addTrackedComponent((IVanillaComponent) o);
			} catch (Exception e) {
				Logger.getLogger(getClass()).error("Failed to add a tracked component to the vanilla listener : " + e.getMessage(), e);
				throw new RuntimeException("Failed to add a tracked component to the vanilla listener : " + e.getMessage(), e);
			}
		}
		return o;
	}
	
	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		super.modifiedService(reference, service);
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
	}
}
