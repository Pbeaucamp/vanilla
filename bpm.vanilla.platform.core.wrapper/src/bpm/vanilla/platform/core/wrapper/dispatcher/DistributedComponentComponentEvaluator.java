package bpm.vanilla.platform.core.wrapper.dispatcher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.UnitedOlapComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaSessionExpiredException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.internal.AbstractRemoteAuthentifier;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * simple helper class to evalute the Load of a Distributable Component.
 * To Be Used during loadBalancing
 * @author ludo
 *
 */
public class DistributedComponentComponentEvaluator {

	
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
						try {
							throw new Exception(result.substring(result.indexOf("<error>") + 7, result.indexOf("</error>")));
						} catch(Exception e) {
							System.out.println(result);
						}
					}
				}
				return result;
			}catch(Exception ex){
				throw ex;
			}

		}
		
	}
	
	/**
	 * Test all the registred component for the dispatcher and return the user defined or the best one
	 * 
	 * @param dispatcher
	 * @param objectIdentifier
	 * @param factory
	 * @return
	 * @throws Exception
	 */
	public static IVanillaComponentIdentifier computeLoad(IDispatcher dispatcher, IObjectIdentifier objectIdentifier, FactoryDispatcher factory) throws Exception{
		
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		//FIXME : Test if a cluster is defined for this item
		//If not let the vanilla evaluator choose the best cluster
		String itemRuntimeUrl = null;
		if(objectIdentifier != null && objectIdentifier.getDirectoryItemId() > 0) {
			
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, login, password);
			
			IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
			Repository repository = api.getVanillaRepositoryManager().getRepositoryById(objectIdentifier.getRepositoryId());
			
			Group group = new Group();
			group.setId(-1);
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);
			
			IRepositoryApi rep = new RemoteRepositoryApi(ctx);
			RepositoryItem item = rep.getRepositoryService().getDirectoryItem(objectIdentifier.getDirectoryItemId());
			itemRuntimeUrl = item.getRuntimeUrl();
		}
		
		return computeLoad(dispatcher, itemRuntimeUrl, factory);
	}
	
	public static IVanillaComponentIdentifier computeLoad(IDispatcher dispatcher, String runtimeUrl, FactoryDispatcher factory) throws Exception {
		String login = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		IVanillaComponentIdentifier bestComponent = null;
		Integer bestComponentIndice = null; 
		
		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(dispatcher); 
		
		if(runtimeUrl != null && !runtimeUrl.equals("default")) {
			for(IVanillaComponentIdentifier id : ids) {
				if(id.getComponentUrl() != null && id.getComponentUrl().equals(runtimeUrl)) {
					bestComponent = id;
					break;
				}
			}
		}
		
		if(bestComponent == null) {
		
			for(IVanillaComponentIdentifier id : ids) {
				//Test this cluster
				HttpSender httpSender = new HttpSender();
				httpSender.init(login, password);
				
				StringBuilder fullUrl = new StringBuilder();
				fullUrl.append(id.getComponentUrl());
				if (VanillaComponentType.COMPONENT_GATEWAY.equals(id.getComponentNature())){
					fullUrl.append(GatewayComponent.GATEWAY_LOAD_EVALUATOR_SERVLET);
				}
				else if (VanillaComponentType.COMPONENT_REPORTING.equals(id.getComponentNature())){
					fullUrl.append(ReportingComponent.REPORTING_LOAD_EVALUATOR_SERVLET);
				}
				else if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(id.getComponentNature())){
					fullUrl.append(UnitedOlapComponent.UOLAP_LOAD_EVALUATOR_SERVLET);
				}
				else{
	//					throw new Exception("Implement me for the VanillaComponent Type = " + id.getComponentNature());
				}
				
				String result = httpSender.sendMessage(fullUrl.toString());
				int componentIndice = Integer.parseInt(result);
				if(bestComponent == null ) {
					bestComponent = id;
					bestComponentIndice = componentIndice;
				}
				else {
					if (componentIndice < bestComponentIndice) { 
						bestComponent = id;
						bestComponentIndice = componentIndice;
					}
				}
			}
		}
		return bestComponent;
	}
}
