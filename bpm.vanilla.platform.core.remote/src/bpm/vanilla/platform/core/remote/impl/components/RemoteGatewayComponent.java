package bpm.vanilla.platform.core.remote.impl.components;

import java.net.HttpURLConnection;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;

public class RemoteGatewayComponent extends RemoteServerManager implements GatewayComponent {

	public static class GatewayHttpCommunicator extends HttpCommunicator {

		private String componentUrl = "";
		
		public GatewayHttpCommunicator() { }
		
		public GatewayHttpCommunicator(String componentUrl) {
			this.componentUrl = componentUrl;
		}
		
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_GATEWAY);
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_COMPONENT_URL, componentUrl);
		}
	}

	public RemoteGatewayComponent(String vanillaUrl, String login, String password) {
		super(new GatewayHttpCommunicator(), vanillaUrl, login, password, false);
	}

	public RemoteGatewayComponent(String vanillaUrl, String login, String password, String componentUrl) {
		super(new GatewayHttpCommunicator(componentUrl), vanillaUrl, login, password, false);
	}

	public RemoteGatewayComponent(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}

	public RemoteGatewayComponent(IVanillaContext ctx, boolean isDispatching) {
		super(new GatewayHttpCommunicator(), ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword(), isDispatching);
	}

	@Override
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig runtimeConfig) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig), GatewayComponent.ActionType.GET_STATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (AlternateDataSourceHolder) xstream.fromXML(xml);
	}

	@Override
	public GatewayRuntimeState getRunState(IRunIdentifier runIdentifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(runIdentifier), GatewayComponent.ActionType.GET_STATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (GatewayRuntimeState) xstream.fromXML(xml);
	}

	@Override
	public RunIdentifier runGatewayAsynch(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig, user), GatewayComponent.ActionType.RUN_ASYNCH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (RunIdentifier) xstream.fromXML(xml);
	}

	@Override
	public GatewayRuntimeState runGateway(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig, user), GatewayComponent.ActionType.RUN_GATEWAY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (GatewayRuntimeState) xstream.fromXML(xml);
	}

	@Override
	public byte[] generateFmdtExtractionTransformation(GatewayModelGeneration4Fmdt config, User user) throws Exception {

		// delegate to the vanilla dispatcher
		XmlAction op = new XmlAction(createArguments(config, user), GatewayComponent.ActionType.GENERATE_FMDT_TRANSFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (byte[]) xstream.fromXML(xml);
	}
}
