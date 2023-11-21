package bpm.fd.runtime.model.ui.jsp;

import java.util.HashMap;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.runtime.engine.GenerationContext;
import bpm.fd.runtime.model.DashBoardMeta;
import bpm.vanilla.platform.core.components.IFormComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class VanillaFormJspGenerator extends JSPCanvasGenerator{
	private HashMap<String, String> hiddenParameterMap;
	private String submitUrl;
	private boolean validation;
	
	public VanillaFormJspGenerator(boolean validation, DashBoardMeta meta, FdModel model, HashMap<String, String> hiddenParameterMap) {
		super(meta, model);
		this.hiddenParameterMap = hiddenParameterMap;
		this.validation = validation;
	}

	protected String generateHTMLBody(){
		StringBuffer buf = new StringBuffer();
		buf.append("<body");
		if (getModel().getCssClass() != null){
			buf.append(" class='" + getModel().getCssClass() + "' ");
		}
		buf.append(">\n");
		
		//here we wrapp the dashboard content with an HTML form tag
		buf.append("<form method=\"POST\" action=\"" + submitUrl + "\" name=\"mainForm\">\n");
		
		/*
		 * generate hiddenfields to add some parameters needed for the 
		 * sumitDestination
		 */
		if (hiddenParameterMap != null){
			for(String s : hiddenParameterMap.keySet()){
				buf.append("            <input type=\"hidden\" name=\"" + s + "\" value=\"" + (hiddenParameterMap.get(s) != null ? hiddenParameterMap.get(s) : "") + "\" />\n");
			}
		}
		
		
		buf.append(generateBodyContent());
		VanillaConfiguration cf = ConfigurationManager.getInstance().getVanillaConfiguration();
		GenerationContext formContext = new GenerationContext(
				cf.getVanillaServerUrl() + IFormComponent.SERVLET_VALIDATE_FORM,
				cf.getVanillaServerUrl() + IFormComponent.SERVLET_INVALIDATE_FORM);
		
		if (validation){
			buf.append("<div style='bottom:0px' >\n");
			buf.append("<input type=\"button\" value=\"Validate\" onClick=\"submitForm('"+ formContext.getValidationUrl() + "')\" />\n");
			buf.append("<input type=\"button\" value=\"Invalidate\" onClick=\"submitForm('" + formContext.getInvalidationUrl() + "')\" />\n");
			buf.append("</div>\n");
		}
		else{
			buf.append("<input type='button' value='Submit' onClick=\"submitForm('" + cf.getVanillaServerUrl() + IFormComponent.SERVLET_SUBMIT_FORM + "')\" />\n");
		}
		
		
		buf.append("</form>\n");
		buf.append("</body>\n");
		return buf.toString();
	}	
}
