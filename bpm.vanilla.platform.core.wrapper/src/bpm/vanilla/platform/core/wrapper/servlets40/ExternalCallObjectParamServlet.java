package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.wrapper.servlets40.accessibility.HTMLErrorWriter;
import bpm.vanilla.platform.core.wrapper.servlets40.helper.ParameterHelper;

/**
 * This the servlet used to dynamically generate a parameter form
 * when using a BI object that has a parameter
 * 
 * @author manu
 *
 */
@SuppressWarnings("serial")
public class ExternalCallObjectParamServlet extends HttpServlet{

	//identifies the paramQuery
	public static final String P_QUERY_KEY = "pQueryKey";
	//identifies the value of the param to refresh (or fetch)
	public static final String P_QUERY_PARAM = "pQueryParam";
	//run report
	public static final String P_QUERY_PARAM_READY = "pQueryParamReady";
	
	private class ParamQuery {
		private String queryId;
		private List<VanillaGroupParameter> params;
		private RepositoryItem item;
		private IRuntimeConfig config;
		private PublicUrl publicUrl;
		private User user;
		
		public ParamQuery(String queryId, List<VanillaGroupParameter> params,
				RepositoryItem item, IRuntimeConfig config, PublicUrl publicUrl, User user) {
			this.queryId = queryId;
			this.params = params;
			this.item = item;
			this.config = config;
			this.publicUrl = publicUrl;
			this.user = user;
		}

		public String getQueryId() {
			return queryId;
		}

		public List<VanillaGroupParameter> getParams() {
			return params;
		}

		public RepositoryItem getItem() {
			return item;
		}

		public IRuntimeConfig getConfig() {
			return config;
		}
		
		public PublicUrl getPublicUrl() {
			return publicUrl;
		}
		
		public User getUser() {
			return user;
		}
	}
	
	private IVanillaComponentProvider component;
	
	private HashMap<String, ParamQuery> activeParamQueries;
	
	public ExternalCallObjectParamServlet(IVanillaComponentProvider component){
		this.component = component;
		this.activeParamQueries = new HashMap<String, ParamQuery>();
	}
	
	/**
	 * Called after the submit/refresh of the generated html/jsp?
	 * -if good redirect to main page with 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		try {
			String queryParamId = req.getParameter(P_QUERY_KEY);
			
			if (queryParamId == null || queryParamId.isEmpty()) {
				Logger.getLogger(getClass()).info("No query param id found, redirecting straight to doGet.");
				doGet(req, resp);
			}
			else if (!activeParamQueries.containsKey(queryParamId)) {
				Logger.getLogger(getClass()).info("Query param id found but invalid, redirecting straight to doGet.");
				doGet(req, resp);			
			}
			else {
				ParamQuery paramQuery = activeParamQueries.get(queryParamId);
				Logger.getLogger(getClass()).info("Query param id and instance of it found.");
				String paramName = req.getParameter(P_QUERY_PARAM);
				if (paramName == null || paramName.isEmpty()) {
					throw new Exception("No parameter name has been specified for refresh.");
				}
				Logger.getLogger(getClass()).info("Trying to fetch data for param '" + paramName +"'");
				
				//we should set existing values to associated params
				for (VanillaGroupParameter groupParams : paramQuery.getParams()) {
					for (VanillaParameter param : groupParams.getParameters()) {
						String parameterValue = req.getParameter(param.getName());
						if (parameterValue != null && !parameterValue.isEmpty()) {
							
							//XXX multi selection
							//param.
//							List<String> values = new ArrayList<String>();
//							values.add(parameterValue);
							param.addSelectedValue(parameterValue);
							//param.setSelectedValues(values);
						}
					}
				}
				//rebuild config
				IRuntimeConfig existingConfig = paramQuery.getConfig();
				IRuntimeConfig updatedConfig;
				
				if (paramQuery.getItem().getType() == IRepositoryApi.FWR_TYPE ||
						(paramQuery.getItem().getType() == IRepositoryApi.CUST_TYPE 
								&& paramQuery.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)
						) { 
					updatedConfig = new ReportRuntimeConfig(existingConfig.getObjectIdentifier(), paramQuery.getParams(), 
							existingConfig.getVanillaGroupId());
				}
				else {
					updatedConfig = new RuntimeConfiguration(existingConfig.getVanillaGroupId(), 
							existingConfig.getObjectIdentifier(), paramQuery.getParams());	
//					updatedConfig = new RuntimeConfig(existingConfig.getObjectIdentifier(), paramQuery.getParams(), 
//							existingConfig.getVanillaGroupId());
				}
				
				//issue query and set refreshed param
				VanillaParameter refreshedParam = ParameterHelper.getValues(paramQuery.getUser(), updatedConfig, 
						paramName, component);
				Logger.getLogger(getClass()).info("Param '" + refreshedParam + "' has been refreshed, now has " + 
						refreshedParam.getValues().keySet().size() + " possible value(s).");
				for (VanillaGroupParameter groupParams : paramQuery.getParams()) {
					for (VanillaParameter param : groupParams.getParameters()) {
						if (param.getName().equals(refreshedParam.getName())) {
							//found, update now
							for (String key : refreshedParam.getValues().keySet()) {
								param.addValue(key, refreshedParam.getValues().get(key));
							}
						}
					}
				}
				
				//generate updated html
				Logger.getLogger(getClass()).info("Generating updated HTML...");
				resp.getWriter().write(generateHtml("Parameters : " + paramQuery.getItem().getItemName() , 
						true, paramQuery));
				resp.getWriter().close();
				
			}
		} catch (Throwable ex){
			Logger.getLogger(getClass()).error("VanillaObject Params Error - "  + ex.getMessage(), ex);
			resp.getWriter().write(HTMLErrorWriter.getErrorHtml("Vanilla ExternalUrl Param Call Failed", ex, true));
			resp.getWriter().close();
		}
//		if (allgood) {
//			activeParamQueries.remove(paramQuery.getQueryId());
//		}
		
		//doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//Logger.getLogger(getClass()).info("Fetching parameters for public url");

		String publicKeyValue = req.getParameter(ExternalCallObjectServlet.P_PUBLIC_KEY);
		Logger.getLogger(getClass()).info("Request for public url parameters with key " + publicKeyValue);
		
		//resp.sendRedirect(VanillaConstants.VANILLA_EXTERNAL_CALL + "?Year=2011&Month=January");
		
		try{
			PublicUrl publicUrl = component.getExternalAccessibilityManager().getPublicUrlsByPublicKey(publicKeyValue);
			
			if (publicUrl == null){
				throw new Exception(publicKeyValue + " is not an existing PublicKey");
			}
			
			ParamQuery paramQuery = createParameterQuery(publicUrl);
			
			resp.getWriter().write(generateHtml("Parameters : " + paramQuery.getItem().getItemName() , 
					true, paramQuery));
			resp.getWriter().close();
			Logger.getLogger(getClass()).info("Saving param query with id " + paramQuery.getQueryId());
			activeParamQueries.put(paramQuery.getQueryId(), paramQuery);
		} catch (Throwable ex){
			Logger.getLogger(getClass()).error("Failed to access VanillaObject Params from externalUrl with public Key " + publicKeyValue + " - "  + ex.getMessage(), ex);
			resp.getWriter().write(HTMLErrorWriter.getErrorHtml("Vanilla ExternalUrl Param Call", ex, true));
			resp.getWriter().close();
		}
	}
	
	private ParamQuery createParameterQuery(PublicUrl publicUrl) throws Exception {
		List<PublicParameter> publicParams = component.getExternalAccessibilityManager().getParametersForPublicUrl(publicUrl.getId());
		
		Logger.getLogger(getClass()).debug("Found " + publicParams.size() + " parameter(s) for public Url");
		
		Date date = new Date();
		if (publicUrl.getEndDate() != null){
			if (!publicUrl.getEndDate().after(date)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				throw new Exception("This PublicUrl has expired on " + sdf.format(publicUrl.getEndDate()));
			}
		}
		
		
		//detect the ObjectType
		
		//we need a user
		User user = component.getSecurityManager().getUserById(publicUrl.getUserId());
		
		if (user == null){
			throw new Exception("The user that created this PublicUrl does not exist anymore. Contact a VanillaPlatform administrator to remove this PublicUrl.");
		}
		
		Repository rep = component.getRepositoryManager().getRepositoryById(publicUrl.getRepositoryId());
		
		if (rep == null){
			throw new Exception("The PublicUrl's is based on Repository that is not currently available.");
		}
		
		Group group = component.getSecurityManager().getGroupById(publicUrl.getGroupId());
		if (group == null){
			throw new Exception("The VanillaGroup used to create this PublicUrl does not exist anymore");
		}
		
		//check if the user still belongs to the Group
		UserGroup ug = component.getSecurityManager().getUserGroup(user.getId(), group.getId());
		if (ug == null){
			throw new Exception("The VanillaUser used to create this PublicUrl does not belong to the VanillaGroup " + group.getName() + " anymore.");
		}
		
		IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(
				new BaseVanillaContext(
						ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), 
						user.getLogin(), 
						user.getPassword()), 
				group, 
				rep)) ;

		
		
		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(publicUrl.getItemId());
//		RepositoryItem item = ((AxisRepositoryConnection)sock).getBrowseClient().getDirectoryItem(publicUrl.getItemId());
//		if (item == null){
//			throw new Exception("The BiObject with id " + publicUrl.getItemId() + " from the repository " + rep.getName() + " does not exists or is not accessible for the Group " + group.getName());
//		}
		
		//VanillaParameterComponentImpl paramComponent = (VanillaParameterComponentImpl) component.getVanillaParameterComponent();

		IRuntimeConfig config;
		
		if (item.getType() == IRepositoryApi.FWR_TYPE ||
				(item.getType() == IRepositoryApi.CUST_TYPE 
						&& item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)
				) { 
			IObjectIdentifier ident = new ObjectIdentifier(publicUrl.getRepositoryId(), publicUrl.getItemId());
			config = new ReportRuntimeConfig(ident, null, publicUrl.getGroupId());
		}
		else {
			IObjectIdentifier ident = new ObjectIdentifier(publicUrl.getRepositoryId(), publicUrl.getItemId());
			config = new RuntimeConfiguration(publicUrl.getGroupId(), ident, null);	
		}
		
		Logger.getLogger(getClass()).info("Getting objects params...");

		List<VanillaGroupParameter> groupParams = ParameterHelper.getDefinition(user, config, component);
		
		//set the first of each group (default selection)
		
		
		Logger.getLogger(getClass()).info("Found " + groupParams.size() + " groupParam(s).");
		
		String paramQueryId = UUID.randomUUID().toString();
		
		ParamQuery paramQuery = new ParamQuery(paramQueryId, groupParams, item, config, publicUrl, user);
		
		return paramQuery;
	}
	
	private String generateHtml(String objectName, boolean includeTitle, ParamQuery paramQuery) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append(generateHtmlHead());
		buf.append("    <body>\n");
		
		
		if (includeTitle) {
			//id=\"form_container\" 
			buf.append("<div id=\"form_container\" class=\"centre\">\n");
			//buf.append("    <div style=\"float:left; margin-top=auto;\"><img src=\"../images/Vanilla_Logo.gif\" /></div>\n");
			buf.append("    <div class=\"entete\"><p style=\"margin-left: 140px;\">" + objectName + "</p></div>\n");
		}
		
		//buf.append("    <form id=\"paramForm\" class=\"paramFormClass\"  method=\"post\" action=\""+ VanillaConstants.VANILLA_EXTERNAL_CALL +"\">\n");
		
		
		VanillaConfiguration vconf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String action = "";
		
		if (vconf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT) != null){
			action = vconf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT) ;	
			if(action.endsWith("/")) {
				action = action.substring(0, action.length()-1);
			}
		}
		action = action + VanillaConstants.VANILLA_EXTERNAL_CALL_PARAM;
		
		buf.append("    <form id=\"paramForm\" " +
				"class=\"paramFormClass\"  " +
				"method=\"post\" " +
				"action=\""+ action + 
				"\">\n");
		buf.append("        <ul>");
		for (VanillaGroupParameter groupParam : paramQuery.getParams()) {
			
			List<VanillaParameter> params = groupParam.getParameters();
			//need next in line for updates
			for (int i = 0; i < params.size() ; i++) {
				buf.append("            <li id=" + params.get(i).getName()+ "_id" + " >");
				buf.append("            	<label " +
						"style=\"text-align: left;\"" + 
						"class=\"groupParamDesc\" " +
						"for=\""+ groupParam.getName() +"\">" + 
						getDefaultDisplay(params.get(i)) + " :" + 
						"</label>");
				//do we have a next one(would be a dependent)
				//buf.append("            	<div>");
				if (params.size() > (i + 1))
					buf.append(generateHtmlForParam(params.get(i), params.get(i + 1)));
				else 
					buf.append(generateHtmlForParam(params.get(i), null));
				//buf.append("            	</div>");
				buf.append("            </li>");
			}
//			for (VanillaParameter param : groupParam.getParameters()) {
//				buf.append("            <li id=" + param.getName()+ "_id" + " >");
//				buf.append("            	<label class=\"groupParamDesc\" for=\""+ groupParam.getName() +"\">" + param.getDisplayName() + "</label>");
//				buf.append(generateHtmlForComboParam(param));
//				buf.append("            </li>");
//			}
			
		}
		buf.append(generateFormButtons(paramQuery));
		buf.append("        </ul>");
		buf.append("    </form>\n");
		if (includeTitle){
			buf.append("</div>\n");
		}
		
		
		
		buf.append("    </body>\n");
		
		buf.append("    <div id=\"footer\">Generated automatically by vanilla</div>");
		
		buf.append("</html>\n");
		
		return buf.toString();
	}
	
	private String generateHtmlHead() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <head>\n");

		buf.append("    <script type=\"text/javascript\">");
		buf.append("    	function updateWithParam(paramName) {");
		//buf.append("            document.forms[\"paramForm\"].submit();");
		//buf.append("            document.paramForm.submit();");
		buf.append("            var paramForm= document.getElementById('paramForm');");
		buf.append("            paramForm." + P_QUERY_PARAM + ".value = paramName;");
		buf.append("            paramForm.submit();");
		buf.append("        }");
		
		//redirects to other thing
		///vanilla40/accessibility
		buf.append("    	function hijackedSubmit() {");
		//buf.append("            alert(\"Hijacked submit\");");
		buf.append("            var paramForm= document.getElementById('paramForm');");
		
		VanillaConfiguration vconf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String ctx = vconf.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_CONTEXT);
		if (ctx != null){
			if(ctx.endsWith("/")) {
				ctx = ctx.substring(0, ctx.length()-1);
			}
			buf.append("            paramForm.action = '" + ctx + "/vanilla40/accessibility';");	
		}
		else{
			buf.append("            paramForm.action = '/vanilla40/accessibility';");
		}
		
		
		
		buf.append("            paramForm.submit();");
		buf.append("        }");
		buf.append("    </script>");
		
		//buf.append("    <% String " + P_QUERY_PARAM +  " = \"notset\"%>");
		
		buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../images/vanillaExternalCss.css\"/>\n");
		buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../images/vanillaExternalParamsCss.css\"/>\n");
		
		buf.append("         <style>\n");
		buf.append("             #round_corners_table { color:black;clear:both; border-color:black; border: 2px; border-collapse: collapse;}\n");
		buf.append("             #top-row {height: 20px; border: none; }\n");
		buf.append("             .empty {border: none; background-color: #E0E0F8; }\n");
		
		buf.append("             .st {background-color: #E0E0F8; }\n");
//		buf.append("             #logo {background-image: url(../images/Vanilla_Logo.gif); }\n");
		buf.append("             #bottom-row {height: 20px; border: none; }\n");
		buf.append("             #tl { border: none; width: 18px; background-image: url(../images/corner_tl.png); }\n");
		buf.append("             #tr { border: none;  width: 18px; background-image: url(../images/corner_tr.png); }\n");
		buf.append("             #bl {  border: none; background-image: url(../images/corner_bl.png); }\n");
		buf.append("             #br {  border: none; background-image: url(../images/corner_br.png); }\n");
		buf.append("         </style>\n");
		buf.append("    </head>\n");
		
		
		return buf.toString();
	}
	
	/**
	 * will select the good generator
	 * @param param
	 * @param paramToUpdate
	 * @return
	 */
	private String generateHtmlForParam(VanillaParameter param, VanillaParameter paramToUpdate) {
		if (param.getControlType() == VanillaParameter.LIST_BOX) {
			return generateHtmlForComboParam(param, paramToUpdate);
		}
		else if (param.getControlType() == VanillaParameter.TEXT_BOX) {
			return generateHtmlForTextParam(param, paramToUpdate);
		}
		else {
			Logger.getLogger(getClass()).warn("Unknow control type " + param.getControlType() + 
					" for param " + param.getName() + ", reverting to normal text box");
			return generateHtmlForTextParam(param, paramToUpdate);
		}
	}
	
	private String generateHtmlForTextParam(VanillaParameter param, VanillaParameter paramToUpdate) {
		StringBuffer buf = new StringBuffer();
		
		//<input type="text" name="lastname" />
		if (param.getDefaultValue() == null || param.getDefaultValue().isEmpty()) {
			buf.append("<input " +
					"size=\"40\"" +
					"type=\"text\" name=\"" + param.getName()  +"\" />");
		} else {
			buf.append("<input " +
					"size=\"40\"" + 
					"type=\"text\" name=\"" + param.getName()  +"\" value=\"" + param.getDefaultValue() + "\"/>");
		}
		
		return buf.toString();
	}
	
	/**
	 * 
	 * @param param
	 * @param paramToUpdate a dependant param that would need refresh if param is changed, pass null if none
	 * @return
	 */
	private String generateHtmlForComboParam(VanillaParameter param, VanillaParameter paramToUpdate) {
		StringBuffer buf = new StringBuffer();
		
		if (paramToUpdate == null) {
			buf.append("<select class=\"element select medium\" id=\"element_1\" name=\"" + param.getName() + "\">");
		}
		else {
			buf.append("<select " +
					"class=\"element select medium\" " +
					"id=\"element_1\"" +
					"onChange=\"updateWithParam('" + paramToUpdate.getName() + "')\"" +
					"name=\"" + param.getName() + "\">");
		}
		
		//adding a listenner :
		//onChange="setParameter('customersFilterCountry', this.value);setLocation();"
		
		//add a empty one
		buf.append("    <option STYLE=\"width:150\" value=\"" + "\" >" + "</option>");
		
		for (String key : param.getValues().keySet()) {
			
			if (param.getSelectedValues().contains(param.getValues().get(key))) {
				//selected
				buf.append("    <option selected=\"selected\" " +
						/*"size=\"40\"" +*/
						"value=\"" + param.getValues().get(key) + "\" >" + 
						key + "</option>");
			}
			else {
				buf.append("    <option " +
						/*"size=\"40\"" +*/
						"value=\"" + param.getValues().get(key) + "\" >" + 
						key + "</option>");
			}
		}
		
		buf.append("</select>");
		
		return buf.toString();
	}
	
	private String generateHtmlForMultiComboParam(VanillaParameter param) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<select multiple class=\"element select medium\" id=\"element_1\" name=\"" + param.getName() + "\">"); 
		
		for (String key : param.getValues().keySet()) {
			if (param.getSelectedValues().contains(param.getValues().get(key))) {
				//selected
				buf.append("    <option selected=\"selected\" value=\"" + key + "\" >" + param.getValues().get(key) + "</option>");
			}
			else {
				buf.append("    <option value=\"" + key + "\" >" + param.getValues().get(key) + "</option>");
			}
		}
		
		buf.append("</select>");
		
		return buf.toString();
	}
	
	/**
	 * Also includes hidden field for public url id
	 * @return
	 */
	private String generateFormButtons(ParamQuery paramQuery) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<li class=\"buttons\">");
		
		buf.append("<input type=\"hidden\" name=\"" + ExternalCallObjectServlet.P_PUBLIC_KEY +"\" value=\"" + paramQuery.getPublicUrl().getPublicKey() +"\" />");
		buf.append("<input type=\"hidden\" name=\"" + P_QUERY_KEY +"\" value=\"" + paramQuery.getQueryId() +"\" />");
		
		//not set for now
		buf.append("<input type=\"hidden\" name=\"" + P_QUERY_PARAM +"\" value=\"" + "notset" +"\" />");
		
		buf.append("<input type=\"hidden\" name=\"" + P_QUERY_PARAM_READY +"\" value=\"" + "notset" +"\" />");
		
		//buf.append("<input id=\"saveForm\" class=\"button_text\" type=\"submit\" name=\"saveForm\" value=\"Run\" />");
		////buf.append("<input id=\"saveForm\" class=\"button_text\" type=\"submit\" name=\"submit\" value=\"Submit\" />");
		buf.append("<input class=\"button\" type=\"button\" value=\"Run\" onclick=\"hijackedSubmit()\" />");
		buf.append("</li>");
		
		return buf.toString();
	}
	
	
	private String getDefaultDisplay(VanillaParameter param) {
		String paramName;
		if (param.getPromptText() != null && !param.getPromptText().isEmpty()) {
			paramName = param.getPromptText();
		}
		else if (param.getDisplayName() != null && !param.getDisplayName().isEmpty()) {
			paramName = param.getDisplayName();
		}
		else {
			paramName = param.getName();
		}
		
		return paramName;
	}
}
