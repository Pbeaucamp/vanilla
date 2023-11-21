package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaListenerService implements IVanillaComponentListenerService{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	public RemoteVanillaListenerService(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	

	@Override
	public void registerVanillaComponent(IVanillaComponentIdentifier listener) throws Exception{
		XmlAction op = new XmlAction(createArguments(listener), IVanillaComponentListenerService.ActionType.ADD_LISTENER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		
	}

	@Override
	public void fireEvent(IVanillaEvent event) throws Exception{
		XmlAction op = new XmlAction(createArguments(event), IVanillaComponentListenerService.ActionType.FIRE_EVENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public void unregisterVanillaComponent(IVanillaComponentIdentifier listener)throws Exception{
		XmlAction op = new XmlAction(createArguments(listener), IVanillaComponentListenerService.ActionType.REMOVE_LISTENER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public List<IVanillaComponentIdentifier> getRegisteredComponents(String componentTypeName, boolean includeStoppedComponent) throws Exception {
		XmlAction op = new XmlAction(createArguments(componentTypeName, includeStoppedComponent), IVanillaComponentListenerService.ActionType.LIST_COMPONENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<IVanillaComponentIdentifier>)xstream.fromXML(xml);
	}

	@Override
	public List<IVanillaComponentIdentifier> getComponents() throws Exception {
//		XmlAction op = new XmlAction(createArguments(componentTypeName), IVanillaComponentListenerService.ActionType.LIST_COMPONENTS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List)xstream.fromXML(xml);
		//return new ArrayList<IVanillaComponentIdentifier>();
		throw new Exception("Not callable from clientside");
	}
	
	@Override
	public void updateVanillaComponent(IVanillaComponentIdentifier componentIdentifier) throws Exception {
		
		//System.out.println("called remote update, dumping request.");
		XmlAction op = new XmlAction(createArguments(componentIdentifier), 
				IVanillaComponentListenerService.ActionType.MOD_LISTENER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	public void startComponent(String componentTypeName) throws Exception {
		throw new Exception("Not callable from clientside");
	}
	
	@Override
	public void stopComponent(String componentTypeName) throws Exception {
		throw new Exception("Not callable from clientside");	
	}
	
	@Override
	public void addTrackedComponent(IVanillaComponent component)
			throws Exception {
		throw new Exception("Not callable from clientside");		
	}
}
