package bpm.vanilla.platform.core.wrapper.dispatcher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.UnitedOlapComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.StartStopServlet;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;
import bpm.vanilla.platform.core.remote.internal.AbstractRemoteAuthentifier;

/**
 * simple helper class to evalute the Load of a Distributable Component.
 * To Be Used during loadBalancing
 * @author ludo
 *
 */
public class StartStopComponent {

	
	public static class HttpSender extends AbstractRemoteAuthentifier{

		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {}
		
		public String sendMessage(String fullUrl) throws Exception{
			URL url = new URL(fullUrl);
			HttpURLConnection sock = (HttpURLConnection) url.openConnection();
			
			writeAuthentication(sock);
			writeAdditionalHttpHeader(sock);
			sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
//			sock.setConnectTimeout(connectionTimeOut);
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("POST");
				
			
			String result = null;
			try{
				InputStream is = sock.getInputStream();
			
				result = IOUtils.toString(is, "UTF-8");
				is.close();
				sock.disconnect();
				
				extractSessionId(sock);
				//error catching
				if (result.contains("<error>")){
					
					if (result.contains("<error><session>")){
						throw new VanillaSessionExpiredException(result.substring(result.indexOf("<error><session>") + 16, result.indexOf("</session></error>")));
					}
					else{
						throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
					}
				}
				return result;
			}catch(Exception ex){
				throw ex;
			}

		}
		
	}
	
	public static int startComponent(Server serverComponent) throws Exception{
		
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		
		HttpSender httpSender = new HttpSender();
		httpSender.init(login, password);
		
		StringBuilder fullUrl = new StringBuilder();
		fullUrl.append(serverComponent.getUrl());
		
		fullUrl.append(IVanillaComponent.SERVICE_ADMIN_SERVLET);
//		if (VanillaComponentType.COMPONENT_REPORTING.equals(serverComponent.getComponentNature())){
//			fullUrl.append(ReportingComponent.REPORTING_SERVICE_ADMIN_SERVLET);
//		}
//		else if (VanillaComponentType.COMPONENT_REPORTING.equals(id.getComponentNature())){
//			fullUrl.append(ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET);
//		}
//		else if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(id.getComponentNature())){
//			fullUrl.append(UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET);
//		}
//		else {
//			throw new Exception("Implement me for the VanillaComponent Type = " + serverComponent.getComponentNature());
//		}
		
		fullUrl.append("?" + StartStopServlet.ACTION_TYPE + "=" + StartStopServlet.TYPE_START);
		
		fullUrl.append("&" + StartStopServlet.COMPONENT_TYPE + "=" + serverComponent.getComponentNature());
		
		String result = httpSender.sendMessage(fullUrl.toString());
		//return Integer.parseInt(result);
		return 0;
	}
	
	public static int stopComponent(Server serverComponent) throws Exception{
		
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		
		HttpSender httpSender = new HttpSender();
		httpSender.init(login, password);
		
		StringBuilder fullUrl = new StringBuilder();
		fullUrl.append(serverComponent.getUrl());
		
		
		fullUrl.append(IVanillaComponent.SERVICE_ADMIN_SERVLET);
//		if (VanillaComponentType.COMPONENT_REPORTING.equals(serverComponent.getComponentNature())){
//			fullUrl.append(ReportingComponent.REPORTING_SERVICE_ADMIN_SERVLET);
//		}
//		else if (VanillaComponentType.COMPONENT_REPORTING.equals(id.getComponentNature())){
//			fullUrl.append(ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET);
//		}
//		else if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(id.getComponentNature())){
//			fullUrl.append(UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET);
//		}
//		else {
//			throw new Exception("Implement me for the VanillaComponent Type = " + serverComponent.getComponentNature());
//		}
		
		fullUrl.append("?" + StartStopServlet.ACTION_TYPE + "=" + StartStopServlet.TYPE_STOP);
		
		fullUrl.append("&" + StartStopServlet.COMPONENT_TYPE + "=" + serverComponent.getComponentNature());
		
		String result = httpSender.sendMessage(fullUrl.toString());
		//return Integer.parseInt(result);
		return 0;
	}
}
