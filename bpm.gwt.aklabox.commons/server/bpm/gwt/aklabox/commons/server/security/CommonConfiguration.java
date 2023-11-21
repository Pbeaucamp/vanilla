package bpm.gwt.aklabox.commons.server.security;

import org.apache.log4j.Logger;

import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.model.activities.AklaflowContext;
import bpm.aklabox.workflow.remote.RemoteAklaflowManager;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class CommonConfiguration {

	private static CommonConfiguration config;
	
	private static Logger logger = Logger.getLogger(CommonConfiguration.class);
	private VanillaConfiguration aklaboxConfig;
	private IVdmManager vdmManager;
	private IAklaflowManager aklaflowManager;

	public CommonConfiguration() {
		logger.info("Creating new PortalConfiguration...");
		connect();
	}
	
	public static CommonConfiguration getInstance() {
		if (config == null) {
			config = new CommonConfiguration();
		}
		
		return config;
	}
	
	public VdmContext getRootContext(){
		this.aklaboxConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String serverUrl = aklaboxConfig.getVanillaServerUrl();

		String rootUser = aklaboxConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String rootPass = aklaboxConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		
		return new VdmContext(serverUrl, rootUser, rootPass, 0);
	}
	
	public AklaflowContext getAklaflowRootContext(){
		this.aklaboxConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String serverUrl = aklaboxConfig.getVanillaServerUrl();

		String rootUser = aklaboxConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String rootPass = aklaboxConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new AklaflowContext(serverUrl, rootUser, rootPass);
	}

	public void reconnect() {
		connect();
	}
	
	private void connect() {
		VdmContext context = getRootContext();
		this.vdmManager = new RemoteVdmManager(context);
		
		AklaflowContext aklaflowContext = getAklaflowRootContext();
		this.aklaflowManager = new RemoteAklaflowManager(aklaflowContext);
	}
	
	public IVdmManager getRootAklaboxManager() {
		return vdmManager;
	}
	
	public IAklaflowManager getRootAklaflowManager() {
		return aklaflowManager;
	}
}
