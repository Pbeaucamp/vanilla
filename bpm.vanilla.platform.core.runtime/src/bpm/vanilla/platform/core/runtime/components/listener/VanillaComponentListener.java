package bpm.vanilla.platform.core.runtime.components.listener;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.IVanillaListener;
import bpm.vanilla.platform.core.remote.impl.components.RemoteEventNotifier;
/**
 * This listener is to listen what happened on one component and to forward
 * the event to another that will handle it
 * The Handler IVanillaComponent will have its notify method called that should handle the forwarded event
 * 
 * @author ludo
 *
 */
public class VanillaComponentListener implements IVanillaListener{
	private String[] listenedTypes;
	private RemoteEventNotifier remote;
	
	public VanillaComponentListener(String[] listenedTypes, IVanillaComponentIdentifier component){
		this.listenedTypes = listenedTypes;
		this.remote = new RemoteEventNotifier(component);
	}
	@Override
	public String[] getListenedEventTypes() {
		return listenedTypes;
	}

	@Override
	public void handleEvent(IVanillaEvent event) {
		for(String s : listenedTypes){
			if (event.getEventTypeName().equals(s)){
				try {
					remote.notify(event);
				} catch (Exception e) {
					Logger.getLogger(getClass()).error("Failed to notify "  +e.getMessage(), e);
				}
			}
		}
	}
	
	
}
