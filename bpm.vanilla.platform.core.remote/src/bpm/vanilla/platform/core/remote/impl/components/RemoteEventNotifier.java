package bpm.vanilla.platform.core.remote.impl.components;

import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteEventNotifier {
	
	private class EventNotifier extends HttpCommunicator{
		@Override
		public String executeAction(XmlAction action, String message, boolean isDispatching)
				throws Exception {
			return sendMessage("", message);
		}
	}
	
	private HttpCommunicator comunicator;
	private static XStream xstream ;
	
	static{
		xstream = new XStream();
	}
	
	public RemoteEventNotifier(IVanillaComponentIdentifier identifier){
		comunicator = new EventNotifier();
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		comunicator.init(identifier.getComponentUrl() + WorkflowService.EVENT_NOTIFICATION_SERVLET, 
				conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
	}
	
	public void notify(IVanillaEvent event) throws Exception{
		XmlArgumentsHolder holder = new XmlArgumentsHolder();
		holder.addArgument(event);
		XmlAction op = new XmlAction(holder, null);

		comunicator.executeAction(op, xstream.toXML(op), false);
	}
}
