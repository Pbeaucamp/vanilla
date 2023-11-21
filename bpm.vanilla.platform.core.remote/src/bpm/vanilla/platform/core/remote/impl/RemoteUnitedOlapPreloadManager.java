package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IUnitedOlapPreloadManager;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteUnitedOlapPreloadManager implements IUnitedOlapPreloadManager{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	public RemoteUnitedOlapPreloadManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	

	@Override
	public void addPreload(UOlapPreloadBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IUnitedOlapPreloadManager.ActionType.ADD_PRELOAD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<UOlapPreloadBean> getPreloadForIdentifier(IObjectIdentifier identifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier), IUnitedOlapPreloadManager.ActionType.LIST_PRELOAD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public void removePreload(UOlapPreloadBean bean) throws Exception {
		XmlAction op = new XmlAction(createArguments(bean), IUnitedOlapPreloadManager.ActionType.DEL_PRELOAD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
}
