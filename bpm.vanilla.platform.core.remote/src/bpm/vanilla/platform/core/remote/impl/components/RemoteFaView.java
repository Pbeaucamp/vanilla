package bpm.vanilla.platform.core.remote.impl.components;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.FaComponent;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteFaView implements FaComponent{
	public static String GROUP = "_group";
	public static String VIEWID = "_viewId";
	public static String REPID = "_repId";
	public static String WIDTH = "_width";
	public static class FaHttpCommunicator extends HttpCommunicator{
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_UNITEDOLAP);

		}
	}
	

	private FaHttpCommunicator httpCommunicator;
	private static XStream xstream;
	
	
	public RemoteFaView(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new FaHttpCommunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}
	
	public RemoteFaView(IVanillaContext ctx) {
		this.httpCommunicator = new FaHttpCommunicator();
		httpCommunicator.init(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}
	
	static {
		xstream = new XStream();
	}
	
	
	public InputStream getFaViewHtml(IRuntimeConfig config) throws Exception{
		StringBuffer b = new StringBuffer();
		b.append(VIEWID);b.append("=");b.append(config.getObjectIdentifier().getDirectoryItemId());
		b.append("&");b.append(REPID);b.append("=");b.append(config.getObjectIdentifier().getRepositoryId());
		b.append("&");b.append(WIDTH);b.append("=");b.append("1000");
		b.append("&");b.append(GROUP);b.append("=");b.append(config.getVanillaGroupId());
		
		
		for(VanillaGroupParameter g : config.getParametersValues()){
			
			for(VanillaParameter p : g.getParameters()){
				b.append("&");b.append(p.getName());b.append("=");
				boolean first = true;
				StringBuffer val = new StringBuffer();
				for(String s : p.getSelectedValues()){
					if (first){
						first = false;
					}
					else{
						val.append(";");
					}
					val.append(p.getValues().get(s));
				}
				b.append(URLEncoder.encode(val.toString(), "UTF-8"));
			}
		}
		
		return  httpCommunicator.executeActionAsStream(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + "?" + b.toString(), "");
 
	}
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
}
