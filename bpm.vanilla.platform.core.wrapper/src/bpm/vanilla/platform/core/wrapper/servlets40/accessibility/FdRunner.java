package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameterCreator;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFdRuntime;

public class FdRunner implements IPublicUrlRunner{

	private IVanillaComponentProvider component;
	private User user;
	
	public FdRunner(IVanillaComponentProvider component, User user){
		this.component = component;
		this.user = user;
	}
	@Override
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception{
		
		//create RunConfig
		List<VanillaGroupParameter> vanillaParameters = VanillaParameterCreator.createVanillaGroupParameters(parameters);
		
		
		RuntimeConfiguration conf = new RuntimeConfiguration(publicUrl.getGroupId(), identifier, vanillaParameters);
		String url = null;
		try {
			List<IVanillaComponentIdentifier> ids = component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_FREEDASHBOARD, false);
			
			if (ids.isEmpty()){
				throw new Exception("There is no FreeDashboard Component currently available within the VanillaPlatform");
			}
			
			RemoteFdRuntime remote = new RemoteFdRuntime(ids.get(0).getComponentUrl(), 
					user.getLogin(), user.getPassword());
			
			
			
			try{
				Logger.getLogger(getClass()).info("Launching Dashboard ....");
				url = remote.deployDashboard(conf);
				Logger.getLogger(getClass()).info("Dashboard successfully executed");
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to execute Report - " + ex.getMessage(), ex);
				throw new Exception("Failed to execute Report - " + ex.getMessage(), ex);
			}
			
			
			
			return url;
			
			
			
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			throw e;
		}
	}

}
