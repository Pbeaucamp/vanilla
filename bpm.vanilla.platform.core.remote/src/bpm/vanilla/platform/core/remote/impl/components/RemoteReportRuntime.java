package bpm.vanilla.platform.core.remote.impl.components;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.FileInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;

public class RemoteReportRuntime extends RemoteServerManager implements ReportingComponent {

	public static class ReportHttpCommunicator extends HttpCommunicator {

		private String componentUrl = "";
		
		public ReportHttpCommunicator() { }
		
		public ReportHttpCommunicator(String componentUrl) {
			this.componentUrl = componentUrl;
		}

		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_REPORTING);
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_COMPONENT_URL, componentUrl);
		}
	}

	public RemoteReportRuntime(String vanillaUrl, String login, String password) {
		super(new ReportHttpCommunicator(), vanillaUrl, login, password, false);
	}

	public RemoteReportRuntime(String vanillaUrl, String login, String password, String componentUrl) {
		super(new ReportHttpCommunicator(componentUrl), vanillaUrl, login, password, false);
	}

	public RemoteReportRuntime(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}

	public RemoteReportRuntime(IVanillaContext ctx, boolean isDispatching) {
		super(new ReportHttpCommunicator(), ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword(), isDispatching);
	}

	@Override
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig runtimeConfig, User user) throws Exception {

		XmlAction op = new XmlAction(createArguments(runtimeConfig, user), ReportingComponent.ActionType.LIST_ALTERNATE_DATASOURCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return !xml.isEmpty() ? (AlternateDataSourceHolder) xstream.fromXML(xml) : null;
	}

	@Override
	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, runtimeConfig, parameterName, dependanciesValues), ReportingComponent.ActionType.PARAMETER_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (VanillaParameter) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, runtimeConfig), ReportingComponent.ActionType.PARAMETERS_DEFINITION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (List) xstream.fromXML(xml);
	}

	@Override
	public InputStream loadGeneratedReport(IRunIdentifier runIdentifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(runIdentifier), ReportingComponent.ActionType.LOAD_RESULT);
		return httpCommunicator.executeActionForBigStream(ReportingComponent.REPORTING_SERVLET, xstream.toXML(op));
//		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), isDispatching);

	}

	@Override
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig, user), ReportingComponent.ActionType.RUN);
		return httpCommunicator.executeActionForBigStream(ReportingComponent.REPORTING_SERVLET, xstream.toXML(op));
//		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, InputStream reportModel, User user, boolean isDisco) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = reportModel.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		reportModel.close();

		byte[] streamDatas = bos.toByteArray();
		if (!isDispatching) {
			streamDatas = Base64.encodeBase64(streamDatas);
		}

		XmlAction op = new XmlAction(createArguments(runtimeConfig, streamDatas, user, isDisco), ReportingComponent.ActionType.RUN_FROM_DEFINITION);
		return httpCommunicator.executeActionForBigStream(ReportingComponent.REPORTING_SERVLET, xstream.toXML(op));
//		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public RunIdentifier runReportAsynch(IReportRuntimeConfig runtimeConfig, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig, user), ReportingComponent.ActionType.RUN_ASYNCH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (RunIdentifier) xstream.fromXML(xml);
	}

	@Override
	public InputStream buildRptDesignFromFWR(IReportRuntimeConfig runtimeConfig, InputStream reportModel) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = reportModel.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		reportModel.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(runtimeConfig, streamDatas), ReportingComponent.ActionType.BUILD_RPT_DESIGN);
		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public boolean checkRunAsynchState(IRunIdentifier runIdentifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(runIdentifier), ReportingComponent.ActionType.CHECK_RUN_ASYNCH_STATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public InputStream generateDiscoPackage(IObjectIdentifier objectIdentifier, int groupId, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(objectIdentifier, groupId, user), ReportingComponent.ActionType.GENERATE_DISCO_PACKAGE);
		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), isDispatching);
	}

	@Override
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig, InputStream model) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = model.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		model.close();

		byte[] streamDatas = bos.toByteArray();

		XmlAction op = new XmlAction(createArguments(user, runtimeConfig, streamDatas), ReportingComponent.ActionType.PARAMETERS_DEFINITION_FROM_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (List<VanillaGroupParameter>) xstream.fromXML(xml);
	}

	@Override
	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues, InputStream model) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = model.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		model.close();

		byte[] streamDatas = bos.toByteArray();

		XmlAction op = new XmlAction(createArguments(user, runtimeConfig, parameterName, dependanciesValues, streamDatas), ReportingComponent.ActionType.PARAMETER_VALUES_FROM_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);

		return (VanillaParameter) xstream.fromXML(xml);
	}

	@Override
	public FileInformations stockReportBackground(IRunIdentifier runIdentifier) throws Exception {
		XmlAction op = new XmlAction(createArguments(runIdentifier), ReportingComponent.ActionType.STOCK_REPORT_BACKGROUND);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), isDispatching);
		return (FileInformations) xstream.fromXML(xml);
	}
}
