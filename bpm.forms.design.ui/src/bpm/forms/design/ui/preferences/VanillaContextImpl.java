package bpm.forms.design.ui.preferences;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class VanillaContextImpl extends BaseRepositoryContext{

	
	
	
	public VanillaContextImpl(IVanillaContext vanillaContext, Group group,	Repository repository) {
		super(vanillaContext, group, repository);
	}

	public IVanillaAPI getVanillaApi() {
		
		return new RemoteVanillaPlatform(this.getVanillaContext());
	}
}
