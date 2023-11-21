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

public class GetValidationFormServlet extends HttpServlet{
	private VanillaFormsRuntimeComponent component;
	
	public GetValidationFormServlet(VanillaFormsRuntimeComponent component){
		this.component = component;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String _instanceId = req.getParameter(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		
		
		try{
			component.getLogger().info("Asking for a FormVaidation Url...");
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
			
			
			if (formInstance.isValidated()){
				throw new Exception("This VanillaFormInstance has already been validated.");
			}
			
			IFormDefinition formDefinition = component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
			
			String finalUrl = buildUrl(formDefinition, instanceId, component.getVanillaContext(ctxLogin, ctxPassword, ctxGroupId));
			
			
//			component.getLogger().info("redirect getting form at :" + finalUrl);
//			resp.sendRedirect(req.getContextPath() + finalUrl);
			
			component.getLogger().info("redirect getting form at :" + finalUrl);
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
							IFormComponent.SERVLET_VALIDATE_FORM,
							hiddenFields);
//			xxx : 
//				
//				todo add hiddenFields for VanillaForms
//				change the submissionURl (not sur but it does not seem ok)
			String s = r.deployValidationForm(conf);

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

	private String buildUrl(IFormDefinition formDefinition, Integer instanceId,	VanillaContext vanillaContext) throws Exception{
		
		IFormUi ui = formDefinition.getFormUI();
		
		
				
		StringBuffer buf = new StringBuffer();

		buf.append("/freedashboardRuntime/vanillaValidationFormDeployer?");
		buf.append("_repurl=" + ui.getPropertyValue(VanillaFdProperties.PROP_FD_REPOSITORY_ID));
		buf.append("&_login=" + vanillaContext.getLogin());
		buf.append("&_group=" + vanillaContext.getVanillaApi().getVanillaSecurityManager().getGroupById(vanillaContext.getGroupId()).getName());
		buf.append("&_password=" + vanillaContext.getPassword());
		buf.append("&_id=" + ui.getPropertyValue(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID));
		buf.append("&");
			
	
		
		buf.append(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
		buf.append("=");
		buf.append(vanillaContext.getGroupId());
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
		buf.append("=");
		buf.append(vanillaContext.getLogin());
		buf.append("&");
		buf.append(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
		buf.append("=");
		buf.append(vanillaContext.getPassword());
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
