package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * each Component should register within the VanillaRuntime when they are started
 * 
 * When a Component wants to inform the platform that something happened it should 
 * call the method fireEvent.
 * 
 * If a Listener is registered for this type of event, it will handle the event
 * and perform some action in a separated Thread
 * 
 * ere:added support for updateVanillaComponent, to keep track of component's status
 * ere: addTrackedComponent, stopComponent and startComponent are not meant for remote use,
 * 		they re only there to be implemented locally by VanillaListenerService and ClusterRegistrer
 * 
 * @author ludo
 *
 */
public interface IVanillaComponentListenerService {
	public static enum ActionType implements IXmlActionType{
		ADD_LISTENER(Level.DEBUG), REMOVE_LISTENER(Level.DEBUG), FIRE_EVENT(Level.DEBUG), LIST_COMPONENTS(Level.DEBUG),
		MOD_LISTENER(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	/**
	 * register a IVanillaComponent within the platform :
	 * 	- should be call by each Component that expected to be known by Vanilla 
	 * @param componentIdentifier
	 * @throws Exception
	 */
	public void registerVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception;
	
	/**
	 * updates the registered vanilla component 
	 * mainly for status for now.
	 * 
	 * @param componentIdentifier
	 * @throws Exception
	 */
	public void updateVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception;
	
	public void unregisterVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception;
	
	/**
	 * an event is fired by one of the Vanilla Component
	 * @param event
	 * @throws Exception
	 */
	public void fireEvent(IVanillaEvent event) throws Exception;
	
	
	/**
	 * ere added, lists everything, even not active 
	 * @return
	 * @throws Exception
	 */
	public List<IVanillaComponentIdentifier> getComponents() throws Exception;
	
	public List<IVanillaComponentIdentifier> getRegisteredComponents(String componentTypeName, boolean includeStoppedComponent) throws Exception;
	
	
	public void addTrackedComponent(IVanillaComponent component) throws Exception;
	
	public void stopComponent(String componentTypeName) throws Exception;
	public void startComponent(String componentTypeName) throws Exception;
//	public IVanillaComponentIdentifier getAvailableComponent(String componentTypeName) throws Exception;
}
