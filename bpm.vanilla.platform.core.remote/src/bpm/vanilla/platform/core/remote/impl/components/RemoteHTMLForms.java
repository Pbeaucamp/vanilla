package bpm.vanilla.platform.core.remote.impl.components;

import java.net.HttpURLConnection;
import java.util.List;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.HTMLFormComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.forms.Form;
import bpm.vanilla.platform.core.components.forms.IForm;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteHTMLForms implements HTMLFormComponent{

//	public static class RemoteHTMLFormsCommunicator extends HttpCommunicator{
//		@Override
//		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
//			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_);
//
//		}
//	}
	

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	public RemoteHTMLForms(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}
	
	public RemoteHTMLForms(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}
	
	static{
		xstream = new XStream();
	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	@Override
	public List<IForm> getActiveForms(Integer vanillaGroupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(vanillaGroupId), HTMLFormComponent.ActionType.LIST);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
		return (List)xstream.fromXML(xml);
	}

}
