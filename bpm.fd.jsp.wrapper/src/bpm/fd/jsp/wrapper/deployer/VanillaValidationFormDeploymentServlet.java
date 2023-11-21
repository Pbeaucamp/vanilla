package bpm.fd.jsp.wrapper.deployer;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fd.jsp.wrapper.ComponentFd;
import bpm.fd.runtime.engine.GenerationContext;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.components.IFormComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * Servlet to Deploy a Dashboard in simple access mode or pre-generation mode
 * 
 * http://localhost:9090/bpm.fd.servletbridge/vanillaFormDeployer?_login=system&_password=54b53072540eeeb8f8e9343e71f28176&_group=System&_id=230&_repurl=1&_submitUrl=http://localhost:8080/toto&zozo=z&zizi=i
 * _login : vanillaLogin
 * _pasword : vanillaPassword MD5 encrypted
 * _id : directoryItemId (the Fd model to deploy)
 * _group : user group name
 * _repurl : Repository Id in the vanilla DataBase
 * _pregeneration : true if it is a pregenration request(false or omited otherwise)
 * _submitUrl : the Url to call on the FormSubmit
 * 
 * allParameters that do not starts with an "_" (in the sample url, zizi and zozo)
 * will generate and hidden input in the HTML forms with the given value (i for zizi in the url sample)
 * to allow to give some requested information to the Submission Url
 * 
 * @author ludo
 *
 */
public class VanillaValidationFormDeploymentServlet extends DeploymentServlet{

	public VanillaValidationFormDeploymentServlet(ComponentFd component, String vanillaUrl) {
		super(component, vanillaUrl);
		
	}

	
	
	
	/**
	 * extract from the Request INput STream parameters from xml
	 * 
	 * <root>
	 * <vanillaLogin> not encrypted
	 * <vanillaPassword>
	 * <vanillaGroup>
	 * <vanillaDirectoryItemId>
	 * <vanillaRepositoryUrl>
	 * <vanillaUrl>
	 * </root>
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		

		String message = "";
//		String login = req.getParameter("_login");
//		String password = req.getParameter("_password");
		String groupId = req.getParameter("_group");
		int directoryItemId = Integer.parseInt(req.getParameter("_id"));
		String repId = req.getParameter("_repurl");//repID
		
		IRepositoryContext repCtx = null;
		VanillaProfil profil = null;
		
		String validationUrl = null;
		String invalidationUrl = null;

		String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)req.getSession().getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}
		
		
				
		VanillaSession svs = null;
		try{
			RemoteVanillaPlatform api = new RemoteVanillaPlatform(
					vanillaUrl, 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

			svs = api.getVanillaSystemManager().getSession(sessionId);
			IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, svs.getUser().getLogin(), svs.getUser().getPassword());
	    	Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
	    	Repository def = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
	    	repCtx = new BaseRepositoryContext(ctx, group, def);
//	    	profil = new VanillaProfil(repCtx);
		}catch(Exception ex){
			message = "FdForm Deployer Service : missing arguments - " + ex.getMessage();
			component.getLogger().error(message, ex);
			ex.printStackTrace();
			sendErrorResponse(resp.getOutputStream(), message);
		}	
		
		try{
	    	profil = new VanillaProfil(repCtx, directoryItemId);
	    	
	    	
	    	validationUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl() + IFormComponent.SERVLET_VALIDATE_FORM;
			invalidationUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl() + IFormComponent.SERVLET_INVALIDATE_FORM;

	    	
		}catch(Exception ex){
			message = "FdForm Deployer Service : missing arguments - " + ex.getMessage();
			component.getLogger().error(message, ex);
			ex.printStackTrace();
			sendErrorResponse(resp.getOutputStream(), message);
		}					
		
		/*
		 * we get the submission String 
		 */
		String submitUrl = req.getParameter("_submitUrl");
		
		
		/*
		 * we get the mapping between component from FD, and
		 * their raplcement names to send values to the submit URL
		 */
		Enumeration<String> en = req.getParameterNames();
		HashMap<String, String> componentNames = new HashMap<String, String>();
		
		StringBuffer ctx_param = new StringBuffer();
		boolean firstP = true;
		while(en.hasMoreElements()){
			String s = en.nextElement();
			if (!s.startsWith("_")){
				componentNames.put(s, req.getParameter(s));
				if (firstP){
					firstP = false;
				}
				else{
					ctx_param.append("&");
				}
				ctx_param.append(s + "=" + req.getParameter(s));
			}
			if (s.startsWith("bpm.")){
				
			}
		}
		
		
//		//Ere, left this part after merge 4.0 but not sure
//		try {
//			service.setVanillaConfig(deploymentInfo, true);
//		} catch (ConfigurationException e1) {
//			message = "FdForm Deployer Service : Failed to set vanilla config";
//			component.getLogger().error(message, e1);
//			sendErrorResponse(resp.getOutputStream(), message);
//		}
		component.getLogger().info("FdForm Deployer Service configured");
		
		
		try {
			component.getLogger().info("FdForm Deployer Service : generating Dashboard");
			
		
			String relativePath = service.deployVanillaForm(!submitUrl.equals(IFormComponent.SERVLET_SUBMIT_FORM), directoryItemId, repCtx,generationLocation, null, componentNames);
			component.getLogger().info("FdForm Deployer Service : Dashboard generated in " + relativePath);
			
			
			if (vanillaUrl.endsWith("/")) {
				vanillaUrl = vanillaUrl.substring(0, vanillaUrl.length() - 1);
			}
			//old way
//			resp.sendRedirect(vanillaUrl + "/generation/" + relativePath + "?" + ctx_param.toString());
			String jspUrl = vanillaUrl + "/generation/" + relativePath ;//+ "?" + ctx_param.toString();
			
			
			resp.getOutputStream().write(jspUrl.getBytes());

//			resp.sendRedirect(req.getContextPath() + "/generation/" + relativePath + "?" + ctx_param.toString());
		} catch (Exception e) {
			message = "FdForm Deployer Service : error when generating Dashboard";
			component.getLogger().error(message, e);
			e.printStackTrace();
			sendErrorResponse(resp.getOutputStream(), message);
			
		}
		
	}
}
