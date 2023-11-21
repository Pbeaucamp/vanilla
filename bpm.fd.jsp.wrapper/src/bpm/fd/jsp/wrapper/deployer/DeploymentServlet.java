package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import bpm.fd.jsp.wrapper.Activator;
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
 * http://host:port/wepappname/freedashDeployer?_login=system&_password=XXXXXXXXX&_group=System&_id=114&_repurl=1
 * _login : vanillaLogin
 * _password : vanillaPassword MD5 encrypted
 * _id : directoryItemId (the Fd model to deploy)
 * _group : user group name
 * _repurl : Repository Id in the vanilla DataBase
 * _pregeneration : true if it is a pregenration request(false or omited otherwise)
 * @author ludo
 *
 */
public class DeploymentServlet extends HttpServlet{
	protected static final String ECLIPSE_RELATIVE_PATH = "/WEB-INF/eclipse/plugins/";
	
	protected String vanillaUrl = null;

	protected String generationLocation;
	protected String pregenerationWorkingLocation;
	protected String pregenerationDeploymentLocation;
	
	protected FdProjectDeployerService service = new FdProjectDeployerService();
	
	protected ComponentFd component;
	
	public DeploymentServlet(ComponentFd component, String vanillaUrl) {
		super();
		this.vanillaUrl = vanillaUrl;
		this.component = component;
		this.component.getLogger().info("Using VanillaUrl=" + vanillaUrl);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();

		Bundle bpmFdJspWrapperBundle = Platform.getBundle(Activator.ID);
		if (bpmFdJspWrapperBundle != null){
			component.getLogger().info("bpmFdJspWrapperBundle found");
		}
		else{
			component.getLogger().error("bpmFdJspWrapperBundle not found");
		}
		
		String bundleLocation;
		try{
			bundleLocation = FileLocator.getBundleFile(bpmFdJspWrapperBundle).getAbsolutePath();
		}catch(Exception ex){
			ex.printStackTrace();
			bundleLocation =Platform.getInstallLocation().getURL().getPath() + "/" + Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/"; ;
		}
		generationLocation = bundleLocation + "/generation";
		component.getLogger().info("Dashboard Generation Location : " + generationLocation);

		
		pregenerationWorkingLocation = bundleLocation + "/pre-generation/";
		component.getLogger().info("Dashboard Working PreGeneration Location : " + pregenerationWorkingLocation);
		File f = new File(pregenerationWorkingLocation);
		if (!f.exists()){
			f.mkdirs();
			component.getLogger().info("missing directories for pregenration WorkingLocation created");
		}
		
		//Test
//		pregenerationDeploymentLocation = getServletContext().getRealPath("./") + ECLIPSE_RELATIVE_PATH +  Activator.ID +  "_" + bpmFdJspWrapperBundle.getHeaders().get("Bundle-Version") + "/pre-generation/";
		pregenerationDeploymentLocation = bundleLocation + "/pre-generation-deployement/";
		component.getLogger().info("Dashboard DeploymentPreGeneration Location : " + pregenerationDeploymentLocation);
		f = new File(pregenerationDeploymentLocation);
		if (!f.exists()){
			f.mkdirs();
			component.getLogger().info("missing directories for pregeneration Location created");
		}
	}

	protected void sendResultResponse(OutputStream out, String message){
		
		try {
			PrintWriter ps = null;
			try {
				ps = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			ps.append( "<result>" + message + "</result>");

			ps.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void sendErrorResponse(OutputStream out, String message){
		try {
			PrintWriter ps = null;
			try {
				ps = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			ps.append( "<error>" + message + "</error>");

			ps.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		ConfigurationManager.getInstance().reloadConfiguration();
		String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)req.getSession().getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}
		
		
		RemoteVanillaPlatform api = new RemoteVanillaPlatform(
				vanillaUrl, 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		VanillaSession svs = null;
		
		String groupId = req.getParameter("_group");
		int directoryItemId = Integer.parseInt(req.getParameter("_id"));
		String repId = req.getParameter("_repurl");//repID
		
		IRepositoryContext repCtx = null;
		
		Group group = null;
		try{
			svs = api.getVanillaSystemManager().getSession(sessionId);
			IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, svs.getUser().getLogin(), svs.getUser().getPassword());
	    	group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
	    	Repository def = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
	    	repCtx = new BaseRepositoryContext(ctx, group, def);
		}catch(Exception ex){
			message = "FdForm Deployer Service : missing arguments - " + ex.getMessage();
			component.getLogger().error(message, ex);
			ex.printStackTrace();
			sendErrorResponse(resp.getOutputStream(), message);
		}		
		
		/**
		 * ere, notes : this is not to tell if it has been pregenerated but if it should be pregen'd
		 */
		boolean isPregeneration = req.getParameter("_pregeneration") != null && Boolean.parseBoolean(req.getParameter("_pregeneration"));
		
		
		//http://localhost:8080/bridge/freedashDeployer?_login=system&_password=54b53072540eeeb8f8e9343e71f28176&_group=System&_id=245&_repurl=1
				
		
		component.getLogger().info("Fd Deployer Service configured");
		
		//extract parameters from url
		StringBuffer params = new StringBuffer();
		Enumeration en = req.getParameterNames();
		HashMap<String, String> parametersFromUrl = new HashMap<String, String>();
		while(en.hasMoreElements()){
			String pname = (String)en.nextElement();
			
			if (pname.equals("_repurl") || pname.equals("_pregeneration")|| pname.equals("_id")|| pname.equals("_group")|| pname.equals("_login")|| pname.equals("_pregeneration") || pname.equals("_password")){
				//already read, no more needed
			}
			else{
				parametersFromUrl.put(pname, req.getParameter(pname));
				params.append("&" + pname + "=" + req.getParameter(pname));
			}
		}
		
		if (!isPregeneration){
			try {
				
				String pregenUrl = null;
				
//				if (pregenUrl != null){
//					pregenUrl = pregenUrl.replace("<result>", "");
//					pregenUrl = pregenUrl.replace("</result>", "");
//					if (pregenUrl.startsWith("/")) {
//						pregenUrl = pregenUrl.replaceFirst("/", "");
//					}
//					if (pregenUrl != null && !pregenUrl.isEmpty()) {
//						component.getLogger().info("Fd Deployer Service : dashboard has been pregen'd at " + pregenUrl);
//						
//						String url = "/pre-generation/" + pregenUrl;
//						if (!url.contains("?")){
//							url = url + "?";
//						}
//						
//						if (vanillaUrl.endsWith("/")) {
//							url = vanillaUrl.substring(0, vanillaUrl.length() - 1) + url + params.toString();
//						}
//						else {
//							url = vanillaUrl + url + params.toString();
//						}
//						
//						component.getLogger().info("Fd Deployer Service : redirecting user to pre-gen'd dashboard at : " + url);
//						
//						url = ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(url);
//						resp.getOutputStream().write(url.getBytes());
//						return;
//					}
//				}
				
				
				
				component.getLogger().info("Fd Deployer Service : no pre-gen info has been set.");
				/**
				 * just deploy it
				 */
				
				String lang = null;
				if(req.getParameter("locale") != null) {
					lang = req.getParameter("locale");
				}
				else {
					lang = req.getLocale().getLanguage();
				}
				
				component.getLogger().info("Fd Deployer Service : generating Dashboard");
				String relativePath = service.deploy(directoryItemId, repCtx, generationLocation, lang, parametersFromUrl);
				component.getLogger().info("Fd Deployer Service : Dashboard generated in " + relativePath);
				
				String url = "/generation/" + relativePath;
				if (!url.contains("?")){
					url = url + "?";
				}
				
				if (vanillaUrl.endsWith("/")) {
					url = vanillaUrl.substring(0, vanillaUrl.length() - 1) + url + params.toString();
				}
				else {
					url = vanillaUrl + url + params.toString();
				}
				url = ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(url);
				url = ConfigurationManager.getInstance().getVanillaConfiguration().translateUrlToExternalUrl(url);
				resp.getOutputStream().write(url.getBytes());
			} catch (Exception e) {
				message = "Fd Deployer Service : error when generating Dashboard : " + e.getMessage();
				component.getLogger().error(message, e);
				e.printStackTrace();
				sendErrorResponse(resp.getOutputStream(), e.getMessage() == null ? message : e.getMessage());
				
				
				
			}
		}
		else{
			
			try {
				component.getLogger().info("Fd Deployer Service :pre-generating Dashboard in Working location");
				String s = "/" +service.deploy(directoryItemId, repCtx, pregenerationWorkingLocation, req.getLocale().getLanguage(), parametersFromUrl);
				
				
				/*
				 * rename the JSP with the GroupName
				 */
				File f = new File(pregenerationWorkingLocation + "/" + s);
				int i = s.lastIndexOf(".jsp");
				s = s.substring(0, i) + "_" + req.getParameter("_group") + ".jsp";
				//delete file with group suffix if alreay exist
				File _f = new File(pregenerationWorkingLocation + "/" + s);
				
				if (_f.exists()){
					  _f.delete();
					
				}
				f.renameTo(new File(pregenerationWorkingLocation + "/" + s));
				component.getLogger().info("Fd Deployer Service :pre-generated Dashboard in Working location " + s);
				component.getLogger().info("Fd Deployer Service :pre-generating Dashboard in Deployment location");
				s = "/" + service.deploy(directoryItemId, repCtx, pregenerationDeploymentLocation, req.getLocale().getLanguage(), parametersFromUrl);
				/*
				 * rename the JSP with the GroupName
				 */
				f = new File(pregenerationDeploymentLocation + "/" + s);
				i = s.lastIndexOf(".jsp");
				s = s.substring(0, i) + "_" + req.getParameter("_group") + ".jsp";
				_f = new File(pregenerationDeploymentLocation + "/" + s);
				
				if (_f.exists()){
					  _f.delete();
					
				}
				f.renameTo(new File(pregenerationDeploymentLocation + "/" + s));
				component.getLogger().info("Fd Deployer Service :pre-generated Dashboard in Deployment location " + s);

				
				sendResultResponse(resp.getOutputStream(), s);

			} catch (Exception e) {
				e.printStackTrace();

				message = "Fd Deployer Service : error when pre-generating Dashboard :" + e.getMessage();
				component.getLogger().error(message, e);
				e.printStackTrace();
				sendErrorResponse(resp.getOutputStream(), message);
				
			}
		}
	
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);

		
	}

	
	
}
