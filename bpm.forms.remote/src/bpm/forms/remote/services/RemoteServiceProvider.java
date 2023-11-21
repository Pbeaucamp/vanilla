package bpm.forms.remote.services;

import bpm.forms.core.design.IDefinitionService;
import bpm.forms.core.design.IServiceProvider;
import bpm.forms.core.runtime.IInstanceService;

public class RemoteServiceProvider implements IServiceProvider{

		
	private IDefinitionService def = new RemoteDefinitionService();
	private IInstanceService inst = new RemoteInstanceService();
	
	public void setUrl(String vanillaRuntimeUrl){
		def.configure(vanillaRuntimeUrl);
		inst.configure(vanillaRuntimeUrl);
		
	}
	

	@Override
	public IDefinitionService getDefinitionService() {
		return def;
	}

	@Override
	public IInstanceService getInstanceService() {
		return inst;
	}


	@Override
	public void configure(Object object) {
		setUrl((String)object);
		
	}

}
