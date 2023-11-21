package bpm.android.vanilla.remote;

import bpm.android.vanilla.core.IAndroidVanillaManager;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteAndroidVanilla implements IAndroidVanillaManager{

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteAndroidVanilla(AndroidVanillaContext ctx) {
		httpCommunicator.init(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword(), null);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public String connect(AndroidVanillaContext vanillaContext) throws Exception {
		XmlAction op = new XmlAction(createArguments(vanillaContext), IAndroidVanillaManager.ActionType.CONNECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	public AndroidVanillaContext getGroupsAndRepositories(AndroidVanillaContext vanillaContext) throws Exception {
		XmlAction op = new XmlAction(createArguments(vanillaContext), IAndroidVanillaManager.ActionType.GET_GROUP_REPOSITORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidVanillaContext)xstream.fromXML(xml);
	}

	@Override
	public AndroidVanillaContext getContextWithPublicGroupAndRepository(AndroidVanillaContext vanillaContext) throws Exception {
		XmlAction op = new XmlAction(createArguments(vanillaContext), IAndroidVanillaManager.ActionType.GET_CONTEXT_WITH_PUBLIC_GROUP_AND_REPOSITORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidVanillaContext)xstream.fromXML(xml);
	}

}
