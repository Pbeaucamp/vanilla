package bpm.vanilla.platform.core.remote.impl.components;

import java.net.HttpURLConnection;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;

import com.thoughtworks.xstream.XStream;

public class RemoteWorkflowComponent extends RemoteServerManager implements WorkflowService {
	
	public static class WorkflowHttpCommunicator extends HttpCommunicator {

		private String componentUrl = "";
		
		public WorkflowHttpCommunicator() { }
		
		public WorkflowHttpCommunicator(String componentUrl) {
			this.componentUrl = componentUrl;
		}
		
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_WORKFLOW);
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_COMPONENT_URL, componentUrl);
		}
	}

	private static XStream xstream;

	public RemoteWorkflowComponent(String vanillaUrl, String login, String password) {
		super(new WorkflowHttpCommunicator(), vanillaUrl, login, password, false);
	}

	public RemoteWorkflowComponent(String vanillaUrl, String login, String password, String componentUrl) {
		super(new WorkflowHttpCommunicator(componentUrl), vanillaUrl, login, password, false);
	}

	public RemoteWorkflowComponent(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}

	static {
		xstream = new XStream();
	}

	private Object parseResponse(String xml) throws Exception {
		if (xml.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(xml);
		if (o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public IRunIdentifier startWorkflow(IRuntimeConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), WorkflowService.ActionType.START);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (IRunIdentifier) parseResponse(xml);
	}

	@Override
	public WorkflowInstanceState getInfos(IRunIdentifier runIdentifier, int itemId, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(runIdentifier, itemId, repositoryId), WorkflowService.ActionType.INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (WorkflowInstanceState) parseResponse(xml);
	}

	@Override
	public IRunIdentifier startWorkflowAsync(IRuntimeConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), WorkflowService.ActionType.START_ASYNC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (IRunIdentifier) parseResponse(xml);
	}
}
