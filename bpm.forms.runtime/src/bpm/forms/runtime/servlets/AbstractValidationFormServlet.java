package bpm.forms.runtime.servlets;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.forms.core.design.IForm;
import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.vanilla.platform.core.beans.User;

public abstract class AbstractValidationFormServlet extends HttpServlet{

	protected VanillaFormsRuntimeComponent component;
	
	
	public AbstractValidationFormServlet(VanillaFormsRuntimeComponent component){
		this.component = component;
	}
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		component.getLogger().error("ValidateFormServlet only support POST method");
		throw new ServletException("Only POST method is supported");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		String _instanceId = req.getParameter(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		
		
		try{
			
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
			
			Integer groupId = Integer.parseInt(ctxGroupId);
			boolean isValidationGroup = false;
			
			IFormDefinition formDefinition= component.getFormServiceProvider().getDefinitionService().getFormDefinition(formInstance.getFormDefinitionId());
			IForm form = component.getFormServiceProvider().getDefinitionService().getForm(formDefinition.getFormId());

			
			for(Integer i : form.getValidatorGroups()){
				if (i.intValue() == groupId){
					isValidationGroup = true;
					break;
				}
			}
			
			if (!isValidationGroup){
				throw new Exception("The Vanilla Context Group Id is not part of the  FormInstance's Validation Groups.");
			}
			
			
			Properties prop = new Properties();
			
			for(Object key : req.getParameterMap().keySet()){
				if (key instanceof String){
					prop.setProperty((String)key,req.getParameter((String)key));
				}
			}
			

			performAction(null, req.getRemoteAddr(), formInstance, prop);
			
			
			
			resp.getWriter().write("<html><header></header><body>");
			resp.getWriter().write("Submission succeeded!!!<br>");
			resp.getWriter().write("</body></html>");
			
			resp.getWriter().flush();
			
			resp.flushBuffer();
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			component.getLogger().error(ex);
			resp.getWriter().write("<html><header></header><body>");
			resp.getWriter().write("Submission failed!!!<br><br>");
			ex.printStackTrace(resp.getWriter());
			
			resp.getWriter().write("</body></html>");
			
			resp.getWriter().flush();
			
			resp.flushBuffer();
			
		}
		
		
	}
	
	abstract public void performAction(User user, String clientIp, IFormInstance formInstance, Properties properties) throws Exception; 
}
