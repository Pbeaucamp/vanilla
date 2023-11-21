package bpm.android.vanilla.wrapper.reporting;

import java.util.ArrayList;
import java.util.List;

import bpm.android.vanilla.core.IAndroidVanillaManager;
import bpm.android.vanilla.core.beans.AndroidGroup;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class AndroidVanillaManager implements IAndroidVanillaManager{

	private ComponentAndroidWrapper component;
	
	public AndroidVanillaManager(ComponentAndroidWrapper component) {
		this.component = component;
	}
	
	@Override
	public AndroidVanillaContext getGroupsAndRepositories(AndroidVanillaContext ctx) throws Exception {
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword());
		
		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(ctx.getLogin());
		if((MD5Helper.encode(ctx.getPassword()).equals(user.getPassword()))){
			List<Repository> listRepositories = vanillaApi.getVanillaRepositoryManager().getRepositories();
			List<Group> listGroup = vanillaApi.getVanillaSecurityManager().getGroups(user);
			
			List<AndroidRepository> repositories = new ArrayList<AndroidRepository>();
			for(Repository rep : listRepositories){
				repositories.add(new AndroidRepository(rep.getId(), rep.getName()));
			}
			List<AndroidGroup> groups = new ArrayList<AndroidGroup>();
			for(Group gr : listGroup){
				groups.add(new AndroidGroup(gr.getId(), gr.getName()));
			}
			
			ctx.setAvailableRepositories(repositories);
			ctx.setAvailableGroups(groups);
			
			return ctx;
		}
		else {
			throw new Exception("The password is not correct for the user " + ctx.getLogin() + ", please try again.");
		}
	}

	@Override
	public String connect(AndroidVanillaContext ctx) throws Exception {
		if(ctx.getRepository() != null && ctx.getGroup() != null){

			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword());
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(ctx.getGroup().getId());
			Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(ctx.getRepository().getId());

			IVanillaContext vanillaCtx = new BaseVanillaContext(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword());
			
			SessionContent session = component.getSessionHolder().createSession();
			((AndroidReportingManager)session.getReportingManager()).setRepositoryContext(vanillaCtx, group, repository);
			((AndroidRepositoryManager)session.getRepositoryManager()).setRepositoryContext(vanillaCtx, group, repository);

			IRepositoryContext repositoryCtx = new BaseRepositoryContext(vanillaCtx, group, repository);
			session.setRepositoryContext(repositoryCtx);
			
			return session.getIdentifier();
		}
		else {
			throw new Exception("You need to choose a group and a repository to be able to browse the metadata.");
		}
	}

	@Override
	public AndroidVanillaContext getContextWithPublicGroupAndRepository(AndroidVanillaContext ctx) throws Exception {
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword());
		
		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(ctx.getLogin());
		if((MD5Helper.encode(ctx.getPassword()).equals(user.getPassword()))){
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			
			Group gr = vanillaApi.getVanillaSecurityManager().getGroupById(Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID)));
			Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID)));
			
			ctx.setGroup(new AndroidGroup(gr.getId(), gr.getName()));
			ctx.setRepository(new AndroidRepository(rep.getId(), rep.getName()));
			
			return ctx;
		}
		else {
			throw new Exception("The password is not correct for the user " + ctx.getLogin() + ", please try again.");
		}
	}
	
}
