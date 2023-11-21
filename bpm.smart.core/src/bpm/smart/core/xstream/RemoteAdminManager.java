package bpm.smart.core.xstream;

import java.util.Locale;

import bpm.smart.core.xstream.internal.HttpCommunicator;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.remote.IAdminManager;

import com.thoughtworks.xstream.XStream;

public class RemoteAdminManager implements IAdminManager {

//	public class RemoteAdminManagerHttpCommunicator extends HttpCommunicator {
//		
//		@Override
//		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
////			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_SMART);
//			if (getLocale() != null) {
//				sock.setRequestProperty(VanillaConstants.HTTP_HEADER_LOCALE, getLocale().getLanguage());
//			}
//		}
//		
//		@Override
//		protected void writeAuthentication(HttpURLConnection sock) {
//			super.writeAuthentication(sock);
//		}
//		
//		@Override
//		public String executeAction(XmlAction action, String message, boolean isDispatching) throws Exception {
//			return sendMessage(IWorkflowManager.SMART_WORKFLOW_MANAGER_URL, message);
//		}
//	}
	
	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteAdminManager(String runtimeUrl, String sessionId, Locale locale) {
		httpCommunicator.init(runtimeUrl, sessionId, locale);
	}
	
	public String getSessionId() {
		return httpCommunicator.getSessionId();
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
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
		if (o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public User login(String login, String password, String locale) throws Exception {
		XmlAction op = new XmlAction(createArguments(login, password, locale), IAdminManager.ActionType.LOGIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) handleError(xml);
	}

	@Override
	public void logout() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAdminManager.ActionType.LOGOUT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}

//	@Override
//	public UpdateInformations checkUpdates() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IAdminManager.ActionType.CHECK_UDPATES);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (UpdateInformations) handleError(xml);
//	}

	@Override
	public String connect(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAdminManager.ActionType.CONNECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}
}
