package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;

public class WorkflowDispatcher extends RunnableDispatcher implements IDispatcher{

	
	
	
	private FactoryDispatcher factory;
	
	public WorkflowDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory){
		super(component);
		this.factory = factory;
	}
	@Override
	public void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String componentUrl = extractComponentUrl(request) != null ? extractComponentUrl(request) : "default";
		
		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
		
		if (ids.isEmpty()){
			throw new VanillaException("No WorkflowComponent registered within Vanilla");
		}
		
		boolean found = false;
		if(componentUrl != null && !componentUrl.equals("default")) {
			for(IVanillaComponentIdentifier id : ids) {
				if(id.getComponentUrl() != null && id.getComponentUrl().equals(componentUrl)) {
					found = true;
					break;
				}
			}
		}
		
		StringBuilder url = new StringBuilder();
		if(found) {
			url.append(componentUrl);
		}
		else {
			url.append(ids.get(0).getComponentUrl());
		}

		url.append(WorkflowService.SERVLET_RUNTIME);
		String urll = url.toString();
		
		try {
			//rewrite the url to localhost if necessary
			String rewrite = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL_LOCALHOST_REWRITE);
			if(rewrite != null && Boolean.parseBoolean(rewrite)) {
				String baseUrlComp = getUrlHost(urll);
				String baseUrlRuntime = getUrlHost(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
				if(baseUrlComp.equals(baseUrlRuntime)) {
					//extract the port
					String port = extractPort(baseUrlRuntime);
					String protocole = getProtocole(baseUrlRuntime);
					if(port.equals("")) {
						urll = protocole + "localhost" + getUrlEnding(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
					}
					else {
						urll = protocole + "localhost:" + port + getUrlEnding(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL));
					}
//					System.out.println("url changed to : " + urll);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		try{
			sendCopy(request, response, urll);
		}catch(FileNotFoundException ex){
			throw new VanillaComponentDownException(ids.get(0), null);
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}
	}

	@Override
	public boolean needAuthentication() {
		return true;
	}

	private String extractComponentUrl(HttpServletRequest request) {
		String componentUrl = request.getHeader(VanillaConstants.HTTP_HEADER_COMPONENT_URL);
		return componentUrl != null && !componentUrl.isEmpty() ? componentUrl : null;
	}
	

}
