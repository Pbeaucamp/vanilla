package bpm.vanilla.platform.core.listeners;

import bpm.vanilla.platform.core.listeners.IVanillaEvent;


/**
 * Base interface to implement for a VanillaListener. Listeners are only part 
 * @author ludo
 *
 */
public interface IVanillaListener {

	public static final String FILTER_EVENT_NAME = "bpm.vanilla.platform.core.runtime.components.listener.event";
	
	
	public void handleEvent(IVanillaEvent event);
	
	public String[] getListenedEventTypes();
}
