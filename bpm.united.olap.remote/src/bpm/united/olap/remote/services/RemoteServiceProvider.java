package bpm.united.olap.remote.services;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.communication.IRuntimeService;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.remote.internal.ModelServiceProvider;
import bpm.united.olap.remote.internal.RuntimeServiceProvider;
import bpm.vanilla.platform.core.IVanillaContext;

public class RemoteServiceProvider implements IServiceProvider {

	private RuntimeServiceProvider runtime = new RuntimeServiceProvider(this);
	private ModelServiceProvider model = new ModelServiceProvider();
//	private IUnitedOlapCacheManager cache = new RemoteUnitedOlapCacheManager();
	
//	private ICacheManagerService cacheManager = new CacheManagerServiceProvider();
	
	@Override
	public void configure(IVanillaContext ctx) {
		runtime.init(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
		model.init(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}

	@Override
	public IModelService getModelProvider() {
		return model;
	}

	@Override
	public IRuntimeService getRuntimeProvider() {
		return runtime;
	}

//	@Override
//	public IUnitedOlapCacheManager getCacheManager() {
//		return cache;
//	}


}
