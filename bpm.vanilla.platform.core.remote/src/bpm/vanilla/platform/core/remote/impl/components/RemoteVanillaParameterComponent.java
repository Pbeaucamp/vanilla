package bpm.vanilla.platform.core.remote.impl.components;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVanillaParameterComponent implements VanillaParameterComponent{

	
	public static class ParamHttpCommunicator extends HttpCommunicator{
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_PARAMETER_PROVIDER);

		}
	}
	
	private ParamHttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	
	public RemoteVanillaParameterComponent(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new ParamHttpCommunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}
	
	
	public RemoteVanillaParameterComponent(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}
	
	
	static {
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
	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config)	throws Exception {
		XmlAction op = new XmlAction(createArguments(config), VanillaParameterComponent.ActionType.PARAMETERS_DEFINITION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName) throws Exception {
		XmlAction op = new XmlAction(createArguments(runtimeConfig, parameterName), VanillaParameterComponent.ActionType.PARAMETER_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaParameter)xstream.fromXML(xml);
	}


	@Override
	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config, InputStream model) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = model.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		model.close();
		
		byte[] streamDatas = bos.toByteArray();
		streamDatas = Base64.encodeBase64(streamDatas);

		XmlAction op = new XmlAction(createArguments(config, streamDatas), VanillaParameterComponent.ActionType.PARAMETERS_DEFINITION_FROM_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<VanillaGroupParameter>)xstream.fromXML(xml);
	}


	@Override
	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName, InputStream model) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = model.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		model.close();
		
		byte[] streamDatas = bos.toByteArray();
		streamDatas = Base64.encodeBase64(streamDatas);
		
		XmlAction op = new XmlAction(createArguments(runtimeConfig, parameterName, streamDatas), VanillaParameterComponent.ActionType.PARAMETER_VALUES_FROM_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (VanillaParameter)xstream.fromXML(xml);
	}

}
