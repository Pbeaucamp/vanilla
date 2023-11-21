package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameterCreator;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;

public class GatewayRunner implements IPublicUrlRunner{

	private IVanillaComponentProvider component;
	private User user;
	public GatewayRunner(IVanillaComponentProvider component, User user){
		this.component = component;
		this.user = user;
	}
	
	@Override
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception{
		
		//create RunConfig
		List<VanillaGroupParameter> vanillaParameters = VanillaParameterCreator.createVanillaGroupParameters(parameters);
		
		
		GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration(identifier, vanillaParameters, publicUrl.getGroupId());
		
		
		try {
			List<IVanillaComponentIdentifier> ids = component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_GATEWAY, false);
			
			if (ids.isEmpty()){
				throw new Exception("There is no Gateway Component currently available within the VanillaPlatform");
			}
			
			RemoteGatewayComponent remote = new RemoteGatewayComponent(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
					user.getLogin(), user.getPassword());
			
			
			
			try{
				Logger.getLogger(getClass()).info("Launching Gateway ....");
				IRunIdentifier id = remote.runGatewayAsynch(conf, user);
				Logger.getLogger(getClass()).info("Gateway successfully launched");
				
				
				VanillaConfiguration vConf = ConfigurationManager.getInstance().getVanillaConfiguration();
				StringBuffer url = new StringBuffer();
				if (vConf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT) != null){
					url.append(vConf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT));
				}
				url.append(GatewayComponent.GATEWAY_JOB_MONITOR_SERVLET );
				
				
				Logger.getLogger(getClass()).info("Redirecting at :" + url.toString());
				
				return url.toString() + "?" + GatewayComponent.P_GATEWAY_RUN_IDENTIFIER + "=" + URLEncoder.encode(id.getKey(), "UTF-8");
				
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to execute Report - " + ex.getMessage(), ex);
				throw new Exception("Failed to execute Report - " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			throw e;
		}
	}
}
