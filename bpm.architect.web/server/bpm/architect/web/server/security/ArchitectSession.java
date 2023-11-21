package bpm.architect.web.server.security;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.Customer;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class ArchitectSession extends CommonSession {

	private IMdmProvider mdmRemote;
	
	private Contract currentContract;
	
	
	public ArchitectSession() {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.ARCHITECT_WEB;
	}
	
	public IMdmProvider getMdmRemote() {
		if (mdmRemote == null) {
			String login = getUser().getLogin();
			String password = getUser().getPassword();
			String vanillaUrl = getVanillaRuntimeUrl();
			this.mdmRemote = new MdmRemote(login, password, vanillaUrl);
		}
		return mdmRemote;
	}
	
	public Contract getCurrentContract() {
		return currentContract;
	}
	
	public void setCurrentContract(Contract currentContract) {
		this.currentContract = currentContract;
	}
	


	public Customer getCustomer() {
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		return conf.getCustomer();
	}
	
	@Override
	public IResourceManager getResourceManager() {
		return getVanillaApi().getResourceManager();
	}
}
