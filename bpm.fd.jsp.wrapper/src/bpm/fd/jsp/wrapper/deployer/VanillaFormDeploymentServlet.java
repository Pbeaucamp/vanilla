package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.fd.jsp.wrapper.ComponentFd;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.VanillaSession;
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
public class VanillaFormDeploymentServlet extends DeploymentServlet{

	public VanillaFormDeploymentServlet(ComponentFd component, String vanillaUrl) {
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
		String groupId = req.getParameter("_group");
		String repId = req.getParameter("_repurl");//repID
		String message = "";

		String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)req.getSession().getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}
		
		
		RemoteVanillaPlatform api = new RemoteVanillaPlatform(
				vanillaUrl, 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		VanillaSession svs = null;
		IRepositoryContext repCtx = null;
//		VanillaProfil profil = null;
		try{
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
		
		
		
		
		int directoryItemId = Integer.parseInt(req.getParameter("_id"));

		//boolean isPregeneration = req.getParameter("_pregeneration") != null && Boolean.parseBoolean(req.getParameter("_pregeneration"));
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
		
		
		
		
//		try {
//			service.setVanillaConfig(deploymentInfo, true);
//		} catch (ConfigurationException e1) {
//			message = "FdForm Deployer Service : Failed to set vanilla config";
//			component.getLogger().error(message, e1);
//			sendErrorResponse(resp.getOutputStream(), message);
//		}
		
		component.getLogger().info("FdForm Deployer Service configured");
		
//		if (!isPregeneration){
			try {
				component.getLogger().info("FdForm Deployer Service : generating Dashboard");
				String relativePath = service.deployVanillaForm(false,
						directoryItemId, 
						repCtx, 
						generationLocation, 
						null, 
						componentNames);
				component.getLogger().info("FdForm Deployer Service : Dashboard generated in " + relativePath);
				
				if (vanillaUrl.endsWith("/")) {
					vanillaUrl = vanillaUrl.substring(0, vanillaUrl.length() - 1);
				}
				//old way
//				resp.sendRedirect(vanillaUrl + "/generation/" + relativePath + "?" + ctx_param.toString());
				String jspUrl = vanillaUrl + "/generation/" + relativePath ;//+ "?" + ctx_param.toString();
				
				
				resp.getOutputStream().write(jspUrl.getBytes());
			} catch (Exception e) {
				message = "FdForm Deployer Service : error when generating Dashboard";
				component.getLogger().error(message, e);
				e.printStackTrace();
				sendErrorResponse(resp.getOutputStream(), message);
				
				
				
			}
//		}
//		else{
//			
//			try {
//				component.getLogger().info("FdForm Deployer Service :pre-generating Dashboard in Working location");
//				String s = "/" +service.deployVanillaForm(
//						directoryItemId, 
//						repCtx, 
//						pregenerationWorkingLocation, 
//						null, 
//						componentNames);
//				
//				
//				/*
//				 * rename the JSP with the GroupName
//				 */
//				File f = new File(pregenerationWorkingLocation + "/" + s);
//				int i = s.lastIndexOf(".jsp");
//				s = s.substring(0, i) + "_" + req.getParameter("_group") + ".jsp";
//				//delete file with group suffix if alreay exist
//				File _f = new File(pregenerationWorkingLocation + "/" + s);
//				
//				if (_f.exists()){
//					  _f.delete();
//					
//				}
//				f.renameTo(new File(pregenerationWorkingLocation + "/" + s));
//				component.getLogger().info("FdForm Deployer Service :pre-generated Dashboard in Working location " + s);
//				component.getLogger().info("FdForm Deployer Service :pre-generating Dashboard in Deployment location");
//				s = "/" + service.deploy(directoryItemId, repCtx, pregenerationDeploymentLocation, req.getLocale().getLanguage());
//				/*
//				 * rename the JSP with the GroupName
//				 */
//				f = new File(pregenerationDeploymentLocation + "/" + s);
//				i = s.lastIndexOf(".jsp");
//				s = s.substring(0, i) + "_" + req.getParameter("_group") + ".jsp";
//				_f = new File(pregenerationDeploymentLocation + "/" + s);
//				
//				if (_f.exists()){
//					  _f.delete();
//					
//				}
//				f.renameTo(new File(pregenerationDeploymentLocation + "/" + s));
//				component.getLogger().info("FdForm Deployer Service :pre-generated Dashboard in Deployment location " + s);
//
//				
//				sendResultResponse(resp.getOutputStream(), s);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//
//				message = "FdForm Deployer Service : error when pre-generating Dashboard";
//				component.getLogger().error(message, e);
//				e.printStackTrace();
//				sendErrorResponse(resp.getOutputStream(), message);
//				
//			}
//		}
	
	}
}
