package bpm.fd.runtime.engine.components;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.runtime.engine.VanillaProfil;

public class ReportGenerator {

	public static String generateJspBlock(int offset, ComponentReport def, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil vanillaProfil) {
		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		StringBuffer params = new StringBuffer();
		
		
//		params.append("objectId=" + def.getDirectoryItemId());
//		params.append("&_group=" +  vanillaProfil.getVanillaGroupId());
//		params.append("&_login=" + vanillaProfil.getVanillaLogin());
//		params.append("&_password=" + vanillaProfil.getVanillaPassword());
//		params.append("&_host=" + vanillaProfil.getRepositoryUrl());
//		params.append("&_host=" + vanillaProfil.getRepositoryUrl());
//		
//		if (vanillaProfil.isPasswordEncrypted()){
//			params.append("&_encrypted=" + vanillaProfil.isPasswordEncrypted());
//		}
		params.append("HashMap<String, String> _reportParameter = new HashMap<String, String>();\n");
		for(ComponentParameter p : def.getParameters()){
//			params.append("&");
			
			
//			params.append(((ReportComponentParameter)p).getName() + "=");
			for(ComponentConfig cf : configs){
				if (cf.getTargetComponent() == def){
//					params.append("<%=_parameterMap.get( \"" +  ((ReportComponentParameter)p).getName() + "\")%>");
					params.append("_reportParameter.put(\"" + p.getName() + "\", _parameterMap.get(\"" + p.getName() + "\"));\n");
				}
//				
			}
		}
		String spacing = "";
		for(int i = 0; i < offset * 4; i++){
			spacing += " ";
		}
		
		ReportOptions opts = (ReportOptions)def.getOptions(ReportOptions.class);
		
		String s_Opts = "";
		
		if (opts != null){
			s_Opts = "width=\"" + opts.getWidth() + "px\" height=\"" + opts.getHeight() + "px\" ";
		}
		 
		
		buf.append(spacing + "<div id=\"" + def.getId() + "\" " + s_Opts + " \">\n");
		buf.append("<%\n");
		buf.append("try{\n");
		buf.append(params.toString());
		buf.append("String html = ReportHelper.generateHTML(\"");
		buf.append(vanillaProfil.getVanillaLogin() + "\",\"" + vanillaProfil.getVanillaPassword() + "\",");
		buf.append(vanillaProfil.getVanillaGroupId() + "," + vanillaProfil.getRepositoryId() + ",");
		buf.append(def.getDirectoryItemId() + ",_reportParameter);\n");
		buf.append("out.write(html);\n");
		
		
		buf.append("}catch(Exception ex){\n");
		buf.append("    ex.printStackTrace();\n");
		buf.append("    out.write(\"<p>\" + ex.getMessage() + \"</p>\");\n");
		buf.append("}");
		buf.append("%>\n");
		
		buf.append(spacing + "</div>");
		
		return buf.toString();
	}

}
