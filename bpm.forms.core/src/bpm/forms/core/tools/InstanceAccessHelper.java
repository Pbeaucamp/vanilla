package bpm.forms.core.tools;


import bpm.forms.core.design.internal.FormsUIInternalConstants;
import bpm.forms.core.runtime.IFormInstance;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;


public class InstanceAccessHelper {

	public static String getFormUrl(IFormInstance instance, IVanillaContext context) throws Exception{
		
		StringBuffer url = new StringBuffer();
		
		
		url.append(context.getVanillaUrl());
		url.append("/forms/getForm?");
		url.append(getUrlsSubmitParameters(instance, context));
		
		
		return url.toString();
	}
	
	
	
	public static String getValidationFormUrl(IFormInstance instance, IRepositoryContext context) throws Exception{
		
		StringBuffer url = new StringBuffer();
		
		
		url.append(context.getVanillaContext().getVanillaUrl());
		url.append("/forms/getValidationForm?");
		url.append(getUrlsValidationParameters(instance, context));
		
		
		return url.toString();
	}
	
	private static String getUrlsSubmitParameters(IFormInstance instance, IVanillaContext context){
		StringBuffer url = new StringBuffer();
		
		url.append(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
		url.append("=");
		url.append(context.getLogin());
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
		url.append("=");
		url.append(context.getPassword());
		
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
		url.append("=");
		url.append(instance.getGroupId());
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		url.append("=");
		url.append(instance.getId());
		
		return url.toString();
	}
	
	private static String getUrlsValidationParameters(IFormInstance instance, IRepositoryContext context){
		StringBuffer url = new StringBuffer();
		
		url.append(FormsUIInternalConstants.VANILLA_CTX_LOGIN);
		url.append("=");
		url.append(context.getVanillaContext().getLogin());
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_CTX_PASSWORD);
		url.append("=");
		url.append(context.getVanillaContext().getPassword());
		
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_CTX_GROUP_ID);
		url.append("=");
		url.append(context.getGroup().getId() + "");
		
		url.append("&");
		url.append(FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID);
		url.append("=");
		url.append(instance.getId());
		
		return url.toString();
	}
}
