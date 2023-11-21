package bpm.fd.runtime.engine.components;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.jsp.JspOptions;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.runtime.engine.VanillaProfil;

public class JspGenerator {

	public static String generateJspBlock(int offset, ComponentJsp def, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil vanillaProfil) {
		StringBuffer buf = new StringBuffer();
		
		StringBuffer params = new StringBuffer();
		
				
		String spacing = "";
		for(int i = 0; i < offset * 4; i++){
			spacing += " ";
		}
		
		for(ComponentParameter p : def.getParameters()){
			params.append(spacing + "<fd:parameter name=\"" + p.getName() + "\" value=\"" );
			

			for(ComponentConfig cf : configs){
				if (cf.getTargetComponent() == def){
					params.append("<%=_parameterMap.get( \"" +  ((ReportComponentParameter)p).getName() + "\")%>");
				}
			}
			params.append("\"/>");
		}
		
		JspOptions opt = (JspOptions)def.getOptions(JspOptions.class);
		
		StringBuffer opts = new StringBuffer();
		
		if (opt != null){
			opts.append(" width=\"" + opt.getWidth()+ "\" ");
			opts.append(" height=\"" + opt.getHeight()+ "\" ");
			opts.append(" border_width=\"" + opt.getBorder_width()+ "\" ");
		}
		
		 
		if (def.hasParameter()){
			buf.append(spacing + "<fd:include url=\"" +def.getJspUrl() + "\" " + opts.toString() + ">\n");
			buf.append(spacing + params.toString() + "\n");
			buf.append(spacing + "</fd:include>\n");
		}
		else{
			buf.append(spacing + "<fd:include url=\"" +def.getJspUrl() + "\" " + opts.toString() + " />\n");
			
		}
		
		
		return buf.toString();
	}

}
