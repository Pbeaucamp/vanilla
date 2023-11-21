package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaDispatcher;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RunnableDispatcher extends AbstractVanillaDispatcher{
	private IVanillaComponentProvider component;
	
	protected RunnableDispatcher(IVanillaComponentProvider component){
		this.component = component;
	}
	
	protected IVanillaComponentProvider getComponent(){
		return component;
	}
	
	public void canBeRun(IRuntimeConfig runtimeConfig) throws Exception {
		VanillaConfiguration vanillaConf = ConfigurationManager.getInstance().getVanillaConfiguration();
		Repository rep = getComponent().getRepositoryManager().getRepositoryById(runtimeConfig.getObjectIdentifier().getRepositoryId());
		
		if (rep == null){
			throw new VanillaException("The Repository with id=" + runtimeConfig.getObjectIdentifier().getRepositoryId() + " is not available.");
		}
		
		
		IRepositoryContext ctx = new BaseRepositoryContext(
				new BaseVanillaContext(
						vanillaConf.getVanillaServerUrl(), 
						vanillaConf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
						vanillaConf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)), 
				getComponent().getSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId()), 
				rep);
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		
		
		if (!repositoryApi.getAdminService().isDirectoryItemAccessible(runtimeConfig.getObjectIdentifier().getDirectoryItemId(), runtimeConfig.getVanillaGroupId())){
			Group group = getComponent().getSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());
			throw new VanillaException("The VanillaObject " + runtimeConfig.getObjectIdentifier().toString() + " is not available for the Group " + group.getName());
		}
		
		if (!repositoryApi.getAdminService().canRun(runtimeConfig.getObjectIdentifier().getDirectoryItemId(), runtimeConfig.getVanillaGroupId())){
			Group group = getComponent().getSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId());
			throw new VanillaException("The VanillaObject " + runtimeConfig.getObjectIdentifier().toString() + " cannot be run by the Group " + group.getName());
		}
	}
	
	public void log(VanillaLogs.Level level, User user, String actionType, String componentId, HttpServletRequest request, IRuntimeConfig config, String message, long delay) throws Exception {
		String ipAddress = request.getRemoteAddr();
		VanillaLogs log = null;
		if(config != null) {
			log = new VanillaLogs(level, componentId, actionType, new Date(), user.getId(), config.getVanillaGroupId(), config.getObjectIdentifier().getRepositoryId(), config.getObjectIdentifier().getDirectoryItemId(), ipAddress, message, delay);	
		}
		else {
			log = new VanillaLogs(level, componentId, actionType, new Date(), user.getId(), ipAddress);
		}
		component.getLoggingManager().addVanillaLog(log);
	}
	
	public void log(VanillaLogs.Level level, User user, IXmlActionType actionType, String componentId, HttpServletRequest request, IRuntimeConfig config, String message) throws Exception {
		log(level, user, actionType.toString(), componentId, request, config, message, 0);
	}
	
	public IRuntimeConfig getRuntimeConfig(XmlArgumentsHolder args) {
		if(args.getArguments() != null && args.getArguments().size() > 0) {
			for(Object o : args.getArguments()) {
				if(o instanceof IRuntimeConfig) {
					return (IRuntimeConfig) o;
				}
			}
		}
		return null;
	}
	
	public String getUrlHost(String url) {
		int firstSlash = url.indexOf("/");
		int secondSlash = url.indexOf("/", firstSlash + 2);
		
		return url.substring(0, secondSlash > -1 ? secondSlash : url.length());
	}
	
	public String extractPort(String url) {
		String port = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
		return port;
	}
	
	public String getProtocole(String url) {
		String protocole = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
		return protocole;
	}
	
	public String getUrlEnding(String url) {
		int firstSlash = url.indexOf("/");
		int secondSlash = url.indexOf("/", firstSlash + 2);
		
		return url.substring(secondSlash > -1 ? secondSlash : url.length(), url.length());
	}
}
