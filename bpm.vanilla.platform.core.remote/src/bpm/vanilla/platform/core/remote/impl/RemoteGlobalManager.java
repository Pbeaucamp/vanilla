package bpm.vanilla.platform.core.remote.impl;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IGlobalManager;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteGlobalManager implements IGlobalManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteGlobalManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		return o;
	}
	
	@Override
	public Serializable manageItem(Serializable item, ManageAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, action), IGlobalManager.ActionType.MANAGE_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Serializable) handleError(xml);
	}
}
