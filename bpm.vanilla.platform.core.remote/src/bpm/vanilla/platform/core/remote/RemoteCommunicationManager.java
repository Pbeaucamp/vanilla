package bpm.vanilla.platform.core.remote;

import bpm.vanilla.platform.core.ICommunicationManager;
import bpm.vanilla.platform.core.beans.feedback.Feedback;
import bpm.vanilla.platform.core.beans.feedback.UserInformations;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteCommunicationManager implements ICommunicationManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteCommunicationManager(String updateManagerUrl, String login, String password) {
		httpCommunicator.init(updateManagerUrl, login, password);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void sendInfos(UserInformations userInfos) throws Exception {
		XmlAction op = new XmlAction(createArguments(userInfos), ICommunicationManager.ActionType.SEND_INFOS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void sendFeedback(Feedback feedback) throws Exception {
		XmlAction op = new XmlAction(createArguments(feedback), ICommunicationManager.ActionType.SEND_FEEDBACK);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
}
