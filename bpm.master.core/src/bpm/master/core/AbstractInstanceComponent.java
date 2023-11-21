package bpm.master.core;

import bpm.master.core.model.InstanceIdentifier;
import bpm.master.core.remote.HttpConnector;
import bpm.master.core.remote.RemoteMasterService;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class AbstractInstanceComponent {

	public void registerInMaster(InstanceIdentifier identifier) throws Exception {
		getMasterService().register(identifier);
	}
	
	
	private IMasterService getMasterService() {
		return new RemoteMasterService(new HttpConnector(ConfigurationManager.getInstance().getProperty("bpm.master.url")));
	}
}
