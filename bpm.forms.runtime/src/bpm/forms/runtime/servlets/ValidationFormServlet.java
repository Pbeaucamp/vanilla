package bpm.forms.runtime.servlets;

import java.util.Properties;

import bpm.forms.core.runtime.IFormInstance;
import bpm.forms.runtime.VanillaFormsRuntimeComponent;
import bpm.forms.runtime.submission.Validator;
import bpm.vanilla.platform.core.beans.User;

public class ValidationFormServlet extends AbstractValidationFormServlet{

	public ValidationFormServlet(VanillaFormsRuntimeComponent component) {
		super(component);
	}
	
	@Override
	public void performAction(User user, String clientIp, IFormInstance formInstance, Properties properties) throws Exception {
		component.getLogger().info("Validating formInstance " + formInstance.getId() + " ...");
		
		Validator validator = new Validator(component, clientIp, user);
		
		try{
			//XXX : replace null by a VanillaContext
			validator.validate(formInstance, properties, null);
			component.getLogger().info("Validated formInstance " + formInstance.getId() );
		}catch(Exception ex){
			ex.printStackTrace();
			throw new Exception("Error while validating VanillaFormInstance " + formInstance.getId() + " : \n" + ex.getMessage(), ex);
		}

		
	}

}
