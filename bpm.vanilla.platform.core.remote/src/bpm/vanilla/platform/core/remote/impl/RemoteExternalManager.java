package bpm.vanilla.platform.core.remote.impl;

import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IExternalManager;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteExternalManager implements IExternalManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteExternalManager(HttpCommunicator httpCommunicator) {
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
	@SuppressWarnings("unchecked")
	public List<D4C> getD4CDefinitions() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IExternalManager.ActionType.GET_D4C_DEFINITIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<D4C>) handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, List<D4CItem>>> getD4cItems(int parentId, TypeD4CItem type) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, type), IExternalManager.ActionType.GET_D4C_ITEMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<String, HashMap<String, List<D4CItem>>>) handleError(xml);
	}
}
