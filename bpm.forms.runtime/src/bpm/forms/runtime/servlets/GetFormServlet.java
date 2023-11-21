package bpm.forms.runtime.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.design.ui.VanillaFdProperties;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.core.runtime.IFormInstanceFieldState;
import bpm.forms.runtime.VanillaContext;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.vanilla.platform.core.components.IFormComponent;
import bpm.vanilla.platform.core.components.fd.FdFormRuntimeConfiguration;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFdRuntime;

public class GetFormServlet extends HttpServlet{
	
	private VanillaFormsRuntimeComponent component;
	
	public GetFormServlet(VanillaFormsRuntimeComponent component){
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String _instanceId = req.getParameter(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		
		
		try{
			component.getLogger().info("Asking for a FormSubmission Url...");
			if (_instanceId == null){
				throw new Exception("Missing bpm.forms.runtime.instanceId parameter");
			}
			
			Integer instanceId = Integer.parseInt(_instanceId);
			
			IFormInstance formInstance = component.getFormServiceProvider().getInstanceService().getRunningInstance(instanceId);
			
			if (formInstance == null){
				throw new Exception("No FormInstance with id = " + instanceId);
			}
			
			//TODO : check User identity
			String ctxLogin = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
			String ctxPassword = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
			
			
			//check that the group is the same as the one given to the formInstance
			String ctxGroupId = req.getParameter(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
			if (ctxGroupId == null){
				throw new Exception("Missing Vanilla Context Group Id when calling Vanilla Form submission servlet");
			}
			if (formInstance.getGroupId() != Integer.parseInt(ctxGroupId)){
				throw new Exception("The Vanilla Context Group Id is not the same as the FormInstance's one.");
			}
			
			
			IFormDefinition formDefinition = component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
			
			//String finalUrl = buildUrl(formDefinition, instanceId, component.getVanillaContext(ctxLogin, ctxPassword, ctxGroupId));
			
			if (component.getFormServiceProvider().getInstanceService().getRunningInstance(instanceId).isSubmited()){
				throw new Exception("This formInstance has already been submited.");
			}
			
			//component.getLogger().info("redirect getting form at :" + finalUrl);
			RemoteFdRuntime r = new RemoteFdRuntime(
					ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(),
					ctxLogin, ctxPassword);
			
			IFormUi  ui = formDefinition.getFormUI();
			
			
			HashMap<String, String> hiddenFields = new HashMap<String, String>();
			hiddenFields.put(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID, formInstance.getId() + "");
			hiddenFields.put(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID, ctxGroupId + "");
			hiddenFields.put(FormsUIInternalConstants.VANILLA_CTX_LOGIN, ctxLogin + "");
			hiddenFields.put(FormsUIInternalConstants.VANILLA_CTX_PASSWORD, ctxPassword + "");
			
			FdFormRuntimeConfiguration conf = new FdFormRuntimeConfiguration(
					Integer.parseInt(ctxGroupId),
					new ObjectIdentifier( Integer.parseInt(ui.getPropertyValue(VanillaFdProperties.PROP_FD_REPOSITORY_ID)), 
							Integer.parseInt(ui.getPropertyValue(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID))), 
							IFormComponent.SERVLET_SUBMIT_FORM,
							hiddenFields);
//			xxx : 
//				
//				todo add hiddenFields for VanillaForms
//				change the submissionURl (not sur but it does not seem ok)
			String s = r.deployForm(conf);

			resp.sendRedirect(s);//req.getContextPath() + finalUrl);
			
		}catch(Exception ex){
			ex.printStackTrace();
			component.getLogger().error(ex);
			resp.getWriter().write("<html><header></header><body>");
			resp.getWriter().write("Submission failed!!!<br>");
			ex.printStackTrace(resp.getWriter());
			
			resp.getWriter().write("</body></html>");
			
			resp.getWriter().flush();
			
			resp.flushBuffer();
			
		}
	}

	private String buildUrl(IFormDefinition formDefinition, Integer instanceId,	VanillaContext context) throws Exception{
		
		IFormUi  ui = formDefinition.getFormUI();
		
		
//		AdminAccess aa = new AdminAccess(context.getVanillaUrl());
//		RepositoryDefinition repDef = null;
//		try{
//			repDef = aa.getRepository(ui.getVanillaRepositoryId());
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new Exception("Unable to find RepositoryDefinition in Vanilla for the given formUi", ex);
//		}
//		
//		AxisRepositoryConnection axisConn = (AxisRepositoryConnection)FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT, repDef.getUrl(), context.getLogin(), context.getPassword(), null, context.getGroupId());
//		
//		
//		
//		GetGeneratedRelativeUrlForGroup op = new GetGeneratedRelativeUrlForGroup();
//		op.setDirectoryItemId(ui.getDirectoryItemId());
//		op.setGroupId(context.getGroupId());
//		GetGeneratedRelativeUrlForGroupResponse resp = null;
//		
//		try{
//			resp = axisConn.getAdminStub().getGeneratedRelativeUrlForGroup(op);
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new Exception("Error when looking for FdForm pregenerated Url : " + ex.getMessage(), ex);
//		}
		
		String baseUrl = null ;
		StringBuffer buf = new StringBuffer();
//		buf.append()
		if (baseUrl == null){
			baseUrl = "/freedashboardRuntime/deployForm?";
			baseUrl += "_repurl=" + ui.getPropertyValue(VanillaFdProperties.PROP_FD_REPOSITORY_ID);
			baseUrl += "&_login=" + context.getLogin();
			baseUrl += "&_group=" + context.getVanillaApi().getVanillaSecurityManager().getGroupById(context.getGroupId()).getName();
			baseUrl += "&_password=" + context.getPassword();
			baseUrl += "&_submitUrl=" + context.getVanillaApi().getVanillaSystemManager().getVanillaSetup().getVanillaRuntimeServersUrl() + "/submitForm";
			baseUrl += "&_id=" + ui.getPropertyValue(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID);
			baseUrl += "&";
			
		}
		else{
			baseUrl = "/VanillaRuntime/" + baseUrl;
			baseUrl += "?";
		}
				
		buf.append(baseUrl);
		
		buf.append(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
		buf.append("=");
		buf.append(context.getGroupId());
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
		buf.append("=");
		buf.append(context.getLogin());
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
		buf.append("=");
		buf.append(context.getPassword());
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		buf.append("=");
		buf.append(instanceId + "");

		
		
		/*
		 * here we get stored Field Values
		 */
		
		for(IFormInstanceFieldState f : component.getFormServiceProvider().getInstanceService().getFieldsState(instanceId)){
			if (f.getValue() != null){
				buf.append("&");
				
				for(IFormFieldMapping fm : formDefinition.getIFormFieldMappings()){
					if (fm.getId() == f.getFormFieldMappingId()){
						buf.append(fm.getFormFieldId());
						buf.append("=");
						buf.append(f.getValue());
						break;
					}
				}
			}
		}
		
		
		return buf.toString();
	}
	
}
