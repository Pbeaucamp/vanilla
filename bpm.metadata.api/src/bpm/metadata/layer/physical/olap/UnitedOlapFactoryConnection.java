package bpm.metadata.layer.physical.olap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;

public class UnitedOlapFactoryConnection {
	
	/**
	 * Create a unitedOlap connection
	 * @param identifier
	 * @param cubeName
	 * @param repositoryContext
	 * @return
	 */
	public static UnitedOlapConnection createUnitedOlapConnection(IObjectIdentifier identifier, String cubeName, IRepositoryContext repositoryContext) {
		initServices(repositoryContext.getVanillaContext());
		
		IRuntimeContext runtimeContext = new RuntimeContext(
				repositoryContext.getVanillaContext().getLogin(), 
				repositoryContext.getVanillaContext().getPassword(), 
				repositoryContext.getGroup().getName(), 
				repositoryContext.getGroup().getId());
		
		UnitedOlapConnection con = new UnitedOlapConnection("Default", identifier, cubeName, runtimeContext, new FaApiHelper(repositoryContext.getVanillaContext().getVanillaUrl(), UnitedOlapLoaderFactory.getLoader()));

		return con;
	}

	private static void initServices(IVanillaContext vanillaContext) {
		IServiceProvider remoteServiceProvider = new RemoteServiceProvider();
		remoteServiceProvider.configure(vanillaContext);
				
		UnitedOlapServiceProvider.getInstance().init(remoteServiceProvider.getRuntimeProvider(), remoteServiceProvider.getModelProvider());
	}

	public static UnitedOlapConnection createUnitedOlapConnection(UnitedOlapConnection con) {
		BaseVanillaContext ctx = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL),
				con.getRuntimeContext().getLogin(),
				con.getRuntimeContext().getPassword());
		
		initServices(ctx);
		return con;
	}

	public static List<UnitedOlapConnection> createUnitedOlapConnection(IObjectIdentifier id, IRepositoryContext repositoryContext) throws Exception {
		
		List<UnitedOlapConnection> cons = new ArrayList<UnitedOlapConnection>();
		
		initServices(repositoryContext.getVanillaContext());
		
		FaApiHelper helper = new FaApiHelper(repositoryContext.getVanillaContext().getVanillaUrl(), UnitedOlapLoaderFactory.getLoader());
		IRuntimeContext runtimeContext = new RuntimeContext(
				repositoryContext.getVanillaContext().getLogin(), 
				repositoryContext.getVanillaContext().getPassword(), 
				repositoryContext.getGroup().getName(), 
				repositoryContext.getGroup().getId());
		
		Collection<String> names = helper.getCubeNames(id, runtimeContext);
		
		for(String name : names) {
			UnitedOlapConnection con = new UnitedOlapConnection("Default", id, name, runtimeContext, helper);
			cons.add(con);
		}
		
		return cons;
	}
}
