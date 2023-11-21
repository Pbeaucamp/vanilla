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
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;

public class WorkflowRunner implements IPublicUrlRunner{

	private IVanillaComponentProvider component;
	private User user;
	public WorkflowRunner(IVanillaComponentProvider component, User user){
		this.component = component;
		this.user = user;
	}
	
	@Override
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception{
		
		//create RunConfig
		List<VanillaGroupParameter> vanillaParameters = VanillaParameterCreator.createVanillaGroupParameters(parameters);
		
		
		RuntimeConfiguration conf = new RuntimeConfiguration(publicUrl.getGroupId(), identifier, vanillaParameters);
		
		
		try {
			List<IVanillaComponentIdentifier> ids = component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_GATEWAY, false);
			
			if (ids.isEmpty()){
				throw new Exception("There is no Gateway Component currently available within the VanillaPlatform");
			}
			
			RemoteWorkflowComponent remote = new RemoteWorkflowComponent(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
					user.getLogin(), user.getPassword());
			
			
			
			try{
				Logger.getLogger(getClass()).info("Launching Workflow ....");
				IRunIdentifier id = remote.startWorkflow(conf);
				Logger.getLogger(getClass()).info("Workflow successfully launched");
				
				
				VanillaConfiguration vConf = ConfigurationManager.getInstance().getVanillaConfiguration();
				StringBuffer url = new StringBuffer();
				if (vConf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT) != null){
					url.append(vConf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT));
				}
				url.append(WorkflowService.WORKFLOW_JOB_MONITOR_SERVLET );
				
				
				Logger.getLogger(getClass()).info("Redirecting at :" + url.toString());
				return url.toString() + "?" + WorkflowService.P_WORKFLOW_RUN_IDENTIFIER + "=" + URLEncoder.encode(id.getKey(), "UTF-8");
				
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to execute Workflow - " + ex.getMessage(), ex);
				throw new Exception("Failed to execute Workflow - " + ex.getMessage(), ex);
			}
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			throw e;
		}
	}
}
