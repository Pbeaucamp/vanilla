package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;

/**
 * 
 * @author ludo
 *
 */
public class FdRuntimeDispatcher  extends RunnableDispatcher implements IDispatcher{

	private FactoryDispatcher factory;
	
	public FdRuntimeDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory){
		super(component);
		this.factory = factory;
	}
	
	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
		
		if (ids.isEmpty()){
			throw new VanillaException("No Dashboard Component " + " registered within Vanilla");
		}
		
		
		String actionName = request.getHeader(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION);
		if (actionName == null || actionName.equals("")){
			//TODO : probable throw an exception
			throw new Exception("Missing the VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION Header with the HttpRequest");
		}
		
		
		
		StringBuilder url = new StringBuilder();
		url.append(ids.get(0).getComponentUrl());
		
		if (FreeDashboardComponent.ACTION_DEPLOY.equals(actionName)){
			url.append(FreeDashboardComponent.SERVLET_DEPLOY);
		}
		else if (FreeDashboardComponent.ACTION_DEPLOY_FORM.equals(actionName)){
			url.append(FreeDashboardComponent.SERVLET_DEPLOY_FORM);
		}
		else if (FreeDashboardComponent.ACTION_DEPLOY_VALIDATION_FORM.equals(actionName)){
			url.append(FreeDashboardComponent.SERVLET_VALIDATE_FORM);
		}
		else if (FreeDashboardComponent.ACTION_DEPLOY_ZIP.equals(actionName)){
			url.append(FreeDashboardComponent.SERVLET_DEPLOY_ZIP);
		}
		else if(FreeDashboardComponent.ACTION_GET_ACTUAL_FOLDER_PAGE.equals(actionName) || FreeDashboardComponent.ACTION_GET_FOLDER_PAGES.equals(actionName)) {
			url.append(FreeDashboardComponent.SERVLET_FOLDER);
		}
		else{
			throw new Exception("Unknown Action:" + actionName);
		}
		url.append( "?" + request.getQueryString());
		
		//TODO Logs vanilla
		long start = new Date().getTime();
		
		/*
		 * redirecting
		 */
		try{
			sendCopy(request, response, url.toString());
			long end = new Date().getTime();
			log(ids.get(0).getComponentId(), actionName, Level.INFO, url.toString(), end - start, "", request);
		} catch(FileNotFoundException ex){
			log(ids.get(0).getComponentId(), actionName, Level.ERROR, url.toString(), 0, ex.getMessage(), request);
			throw new VanillaComponentDownException(ids.get(0), null);
		} catch(Exception ex) {
			log(ids.get(0).getComponentId(), actionName, Level.ERROR, url.toString(), 0, ex.getMessage(), request);
			throw ex;
		}
		
	}
	
	private void log(String componentId, String action, Level level, String url, long delay, String message, HttpServletRequest request) throws Exception {
		
		IRuntimeConfig conf = extractInfosFromUrl(url);
		
		log(level, new User(), action, componentId, request, conf, message, delay);
	}

	private IRuntimeConfig extractInfosFromUrl(String url) {
		int groupId = 0;
		int repId = 0;
		int itemId = 0;
		if(url.indexOf("_group=") > -1) {
			groupId = Integer.parseInt(url.substring(
					url.indexOf("_group=") + 7, 
					url.indexOf("&", url.indexOf("_group=") + 7)));
		}
		if(url.indexOf("_id") > -1) {
			itemId = Integer.parseInt(url.substring(
					url.indexOf("_id=") + 4, 
					url.indexOf("&", url.indexOf("_id=") + 4)));
		}
		if(url.indexOf("_repurl") > -1) {
			repId = Integer.parseInt(url.substring(
					url.indexOf("_repurl=") + 8, 
					url.indexOf("&", url.indexOf("_repurl=") + 8)));
		}
		else if(url.indexOf("_repid") > -1) {
			repId = Integer.parseInt(url.substring(
					url.indexOf("_repid=") + 7, 
					url.indexOf("&", url.indexOf("_repid=") + 7)));
		}
	
		IObjectIdentifier identifier = new ObjectIdentifier(repId, itemId);
		
		return new RuntimeConfiguration(groupId, identifier, null);
	}

	@Override
	public boolean needAuthentication() {
		return true;
	}
	
}
