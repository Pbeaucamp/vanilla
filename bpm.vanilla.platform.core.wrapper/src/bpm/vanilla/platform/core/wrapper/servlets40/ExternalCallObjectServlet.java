package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.FaViewRunner;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.FdRunner;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.GatewayRunner;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.HTMLErrorWriter;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.IPublicUrlRunner;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.ReportRunner;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.WorkflowRunner;

/**
 * this servlet is the one that should be used to call an Object from Outside of that VanillaPlatform ere : if you want admin stuff it s {@link ExternalAccessibilityServlet}
 * 
 * @author ludo
 * 
 */
public class ExternalCallObjectServlet extends HttpServlet {
	public static final String P_PUBLIC_KEY = "publickey";
	public static final String P_SECOND_CALL = "secondcall";

	private IVanillaComponentProvider component;

	public ExternalCallObjectServlet(IVanillaComponentProvider component) {
		this.component = component;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// super.doPost(req, resp);
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String publicKeyValue = req.getParameter(P_PUBLIC_KEY);
		String secondCall = req.getParameter(P_SECOND_CALL);

		boolean alreadyCalled = false;

		if (secondCall != null && Boolean.parseBoolean(secondCall)) {
			alreadyCalled = true;
		}

		Logger.getLogger(getClass()).info("Request for public url with key " + publicKeyValue);

		// these are the parameters given to us (most probably from the param servlet
		HashMap<String, String> dynamicParameters = new HashMap<String, String>();

		// these are the parameters to execute the object
		HashMap<String, String> executionParameters = new HashMap<String, String>();

		Enumeration<String> enumPNames = req.getParameterNames();

		while (enumPNames.hasMoreElements()) {
			String pName = enumPNames.nextElement();
			String item = req.getParameter(pName); 

			byte[] bytes = item.getBytes(StandardCharsets.ISO_8859_1);
			item = new String(bytes, StandardCharsets.UTF_8);
			dynamicParameters.put(pName, item);
		}

		try {
			PublicUrl publicUrl = component.getExternalAccessibilityManager().getPublicUrlsByPublicKey(publicKeyValue);
			if (publicUrl == null) {
				throw new Exception(publicKeyValue + " is not an existing PublicKey");
			}

			Logger.getLogger(getClass()).info("Public url with key : " + publicKeyValue + " has been found and is being processed...");

			Date date = new Date();
			if (publicUrl.getEndDate() != null) {
				if (!publicUrl.getEndDate().after(date)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					throw new Exception("This PublicUrl has expired on " + sdf.format(publicUrl.getEndDate()));
				}
			}

			// we need a user
			User user = component.getSecurityManager().getUserById(publicUrl.getUserId());
			if (user == null) {
				throw new Exception("The user that created this PublicUrl does not exist anymore. Contact a VanillaPlatform administrator to remove this PublicUrl.");
			}

			Group group = component.getSecurityManager().getGroupById(publicUrl.getGroupId());
			if (group == null) {
				throw new Exception("The VanillaGroup used to create this PublicUrl does not exist anymore");
			}

			// check if the user still belongs to the Group
			UserGroup ug = component.getSecurityManager().getUserGroup(user.getId(), group.getId());
			if (ug == null) {
				throw new Exception("The VanillaUser (with id '" + user.getId() + "') used to create this PublicUrl does not belong to the VanillaGroup " + group.getName() + " anymore.");
			}
			
			BaseVanillaContext vanillaCtx = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword());
			
			if (publicUrl.getTypeUrl() == TypeURL.REPOSITORY_ITEM) {
				List<PublicParameter> publicParams = component.getExternalAccessibilityManager().getParametersForPublicUrl(publicUrl.getId());
	
				Logger.getLogger(getClass()).debug("Found " + publicParams.size() + " parameter(s) for public Url");
	
				for (PublicParameter p : publicParams) {
					if (dynamicParameters.containsKey(p.getParameterName())) {
						String value = dynamicParameters.get(p.getParameterName());
						Logger.getLogger(getClass()).debug("Using Dynamic Parameter (name/value) : " + p.getParameterName() + "/" + value);
						executionParameters.put(p.getParameterName(), value);
					}
					else {
						Logger.getLogger(getClass()).debug("Using pre-defined Parameter (name/value) : " + p.getParameterName() + "/" + p.getParameterValue());
						executionParameters.put(p.getParameterName(), p.getParameterValue());
					}
	
				}
	
				// check if parameters are set
				Logger.getLogger(getClass()).debug("Check parameters");
				if (!verifyParam(executionParameters)) {
					String ctx = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT);
	
					if (ctx != null) {
						ctx = ctx.trim();
						if (ctx.endsWith("/")) {
							ctx = ctx.substring(0, ctx.length() - 1);
						}
	
						String url = ctx + VanillaConstants.VANILLA_EXTERNAL_CALL_PARAM + "?" + P_PUBLIC_KEY + "=" + publicUrl.getPublicKey();
						Logger.getLogger(getClass()).debug("Redirect to - " + url);
						resp.sendRedirect(url);
	
					}
					else {
						String url = VanillaConstants.VANILLA_EXTERNAL_CALL_PARAM + "?" + P_PUBLIC_KEY + "=" + publicUrl.getPublicKey();
						Logger.getLogger(getClass()).debug("Redirect to - " + url);
						resp.sendRedirect(url);
	
					}
					return;
				}
				Logger.getLogger(getClass()).debug("Parameters are filled");

				Repository rep = component.getRepositoryManager().getRepositoryById(publicUrl.getRepositoryId());
				if (rep == null) {
					throw new Exception("The PublicUrl's is based on Repository that is not currently available.");
				}
	
				IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, group, rep));
	
				RepositoryItem item = sock.getRepositoryService().getDirectoryItem(publicUrl.getItemId());
				if (item == null) {
					throw new Exception("The BiObject with id " + publicUrl.getItemId() + " from the repository " + rep.getName() + " does not exists or is not accessible for the Group " + group.getName());
				}
	
				switch (item.getType()) {
				case IRepositoryApi.FAV_TYPE:
				case IRepositoryApi.CUST_TYPE:
				case IRepositoryApi.FWR_TYPE:
					alreadyCalled = true;
					break;
				}

				if (alreadyCalled) {
	
					IPublicUrlRunner runner = null;
					switch (item.getType()) {
					case IRepositoryApi.BIW_TYPE:
						runner = new WorkflowRunner(component, user);
						break;
					case IRepositoryApi.GTW_TYPE:
						runner = new GatewayRunner(component, user);
						break;
					case IRepositoryApi.FAV_TYPE:
						runner = new FaViewRunner(component, user);
						break;
					case IRepositoryApi.FD_TYPE:
						runner = new FdRunner(component, user);
						executionParameters = dynamicParameters;
						break;
					case IRepositoryApi.CUST_TYPE:
					case IRepositoryApi.FWR_TYPE:
						runner = new ReportRunner(component, user);
						break;
					default:
						throw new Exception("The BiObject of type " + IRepositoryApi.MODEL_TYPE_NAMES[item.getType()] + " cannot be accessed.");
					}
	
					try {
						String result = runner.runObject(resp, publicUrl, new ObjectIdentifier(rep.getId(), item.getId()), executionParameters);
						switch (item.getType()) {
						case IRepositoryApi.BIW_TYPE:
						case IRepositoryApi.GTW_TYPE:
						case IRepositoryApi.FD_TYPE:
							resp.getWriter().write(result);
							break;
						}
	
					} catch (VanillaException ex) {
						throw ex;
					} catch (Exception ex) {
						throw new Exception("Error when running the BiObject <br> " + ex.getMessage(), ex);
					}
				}
				else {
					resp.setContentType("text/html");
					resp.getWriter().write(writeWait(req));
				}
			}
			else if (publicUrl.getTypeUrl() == TypeURL.DOCUMENT_VERSION) {
				IGedComponent gedComponent = new RemoteGedComponent(vanillaCtx);
				
				DocumentVersion docVersion = gedComponent.getDocumentVersionById(publicUrl.getItemId());
				
				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), user.getId(), docVersion.getVersion());
				
				InputStream is = gedComponent.loadGedDocument(config);
				ServletOutputStream out = resp.getOutputStream();

				byte buffer[] = new byte[512 * 1024];
				int nbLecture;
				while ((nbLecture = is.read(buffer)) != -1) {
					out.write(buffer, 0, nbLecture);
				}
				is.close();
				out.close();
			}
		} catch (Throwable ex) {
			Logger.getLogger(getClass()).error("Failed to access VanillaObject from externalUrl with public Key " + publicKeyValue + " - " + ex.getMessage(), ex);
			resp.getWriter().write(HTMLErrorWriter.getErrorHtml("Vanilla ExternalUrl Call", ex, true));
			resp.getWriter().close();
		}

	}

	private String writeWait(HttpServletRequest req) {
		StringBuilder buf = new StringBuilder();

		buf.append("<html style=\"height: 100%; width:100%;\">\n");
		buf.append("	<body style=\"height: 100%; width:100%; margin: 0; padding: 0;\">\n");

		buf.append("		<div style=\"background-color: #000000;bottom: 0;left: 0;opacity: 0.3;position: absolute;right: 0;top: 0;z-index: 4;\">\n");

		buf.append("			<div style=\"background: none repeat scroll 0 0 #FFFFFF;border: 1px solid #808080;border-radius: 5px 5px 5px 5px;height: 70px;width: 230px;z-index: 4;margin: auto;margin-top: 20%;\">\n");

		String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		url = url.toLowerCase().replace("vanillaruntime", "");
		url += ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES).replace("webapps/", "") + "/VanillaCss/loadingBig.gif";

		buf.append("				<img src=\"" + url + "\" style=\"display: block;margin: auto;\">\n");
		buf.append("				</img>\n");
		buf.append("				<label style=\"display: block;margin: 5px auto auto;width: 90px;\">Please wait...</label>\n");

		buf.append("				<script>\n");
		buf.append("					var xhttp = new XMLHttpRequest();\n");
		buf.append("					xhttp.open(\"GET\", \"" + req.getRequestURI() + "?" + req.getQueryString() + "&" + P_SECOND_CALL + "=true\", true);\n");
		buf.append("					xhttp.onreadystatechange = function() { \n");
		buf.append("						if(xhttp.readyState == 4) {\n");
		buf.append("							window.location=xhttp.responseText; \n");
		buf.append("						}\n");
		buf.append("					}\n");
		buf.append("					xhttp.send();\n");
		buf.append("				</script>\n");

		buf.append("			</div>\n");

		buf.append("		</div>\n");

		buf.append("	</body>\n");
		buf.append("</html>\n");

		return buf.toString();
	}

	/**
	 * Are params fully set
	 * 
	 * @param params
	 * @return
	 */
	private boolean verifyParam(HashMap<String, String> params) {
		for (String paramName : params.keySet()) {
			if (params.get(paramName) == null || params.get(paramName).isEmpty()) {
				return false;
			}
		}
		return true;
	}
}
