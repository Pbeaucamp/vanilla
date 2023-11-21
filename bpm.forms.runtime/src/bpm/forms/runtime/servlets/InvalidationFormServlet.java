package bpm.forms.runtime.servlets;

import java.util.Properties;

import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.forms.runtime.submission.Validator;
import bpm.vanilla.platform.core.beans.User;

public class InvalidationFormServlet extends AbstractValidationFormServlet{

	

	
	
	public InvalidationFormServlet(VanillaFormsRuntimeComponent component) {
		super(component);
	}

	@Override
	public void performAction(User user, String clientIp, IFormInstance formInstance, Properties properties) throws Exception {
		component.getLogger().info("Invalidating formInstance " + formInstance.getId() + " ...");
		
		Validator validator = new Validator(component, clientIp, user);
		
		try{
			//XXX : replace null by a VanillaContext
			validator.invalidate(formInstance, properties, null);
			component.getLogger().info("Invalidating formInstance " + formInstance.getId() );
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error while validating VanillaFormInstance " + formInstance.getId() + " : \n" + ex.getMessage(), ex);
		}

		
	}

}
