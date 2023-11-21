package bpm.fd.runtime.engine.components;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class OlapViewGenerator {

	public static String generateJspBlock(int offset, ComponentFaView def, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil vanillaProfil) {
		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		StringBuffer params = new StringBuffer();
		
		
//		params.append("_viewId=" + def.getDirectoryItemId());
//		params.append("&_group=" +  vanillaProfil.getVanillaGroupId());
//		params.append("&_user=" + vanillaProfil.getVanillaLogin());
//		params.append("&_password=" + vanillaProfil.getVanillaPassword());
//		params.append("&_repId=" + vanillaProfil.getRepositoryId() );
//		
		
		
//		params.append("&_encrypted=" + vanillaProfil.isPasswordEncrypted());
	
		for(ComponentParameter p : def.getParameters()){
			params.append("\"&");
			
			params.append(((ReportComponentParameter)p).getName() + "=" + "\" + ");
			for(ComponentConfig cf : configs){
				if (cf.getTargetComponent() == def){
					params.append("_parameterMap.get( \"" +  ((ReportComponentParameter)p).getName() + "\")");
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
			s_Opts = "width=\\\"" + opts.getWidth() + "px\\\" height=\\\"" + opts.getHeight() + "px\\\" ";
		}
		if (params.length() > 0){
			params.append("+\"&width=" + opts.getWidth() + "\"");
		}
		else{
			params.append("\"&width=" + opts.getWidth() + "\"");
		}
		
		
//		buf.append(spacing + "<iframe id=\"" + def.getId() + "\" " + s_Opts + " src=\"" + vanillaProfil.getVanillaUrl() + "/RunOlapView?" + params.toString() + "\">\n");
//		buf.append(spacing + "<iframe id=\"" + def.getId() + "\" " + s_Opts + " src=\"" +  vanillaProfil.getVanillaUrl() + "/faRuntime/FdServlet?" + params.toString() + "\">\n");
//		buf.append(spacing + "    <p>Not available</p>\n");
//		buf.append(spacing + "</iframe>");
//		buf.append(spacing + "<div id=\"" + def.getId() + "\" " + s_Opts + " />");
		buf.append("<%\n");
		buf.append("try{\n");
		
		
		buf.append("out.write(OlapViewHelper.generateHTML(");
		buf.append("\"" +vanillaProfil.getVanillaLogin() + "\", ");
		
		buf.append("\"" +MD5Helper.encode(vanillaProfil.getVanillaPassword()) + "\", ");
		if(vanillaProfil.getVanillaGroupId() == null){
			buf.append( + vanillaProfil.getVanillaGroupId() +", ");
		}
		else{
			buf.append(vanillaProfil.getVanillaGroupId() + ", ");
		}
		
		buf.append(vanillaProfil.getRepositoryId() + ", ");
		buf.append(opts.getWidth()  + ", ");
		buf.append("\"" + def.getId() + "\", ");
		buf.append(def.getDirectoryItemId() + ", ");
		buf.append("\"" + s_Opts + "\""+ ", "+ params + "));\n");
		
		
		buf.append("}catch(Exception ex){\n");
		buf.append(" out.write(\" Unable to generate the FreeAnalysisView : \" + ex.getMessage());\n"  );
		buf.append(" ex.printStackTrace();\n"  );
		buf.append("}\n");
		buf.append("%>\n");
		
				
		buf.append(spacing + "<a href=\"" + vanillaProfil.getFreeAnalysisWebUrl() + "?initfromfd=true&" + params.toString() + "\" style=\"display:block;\">Open this view in FreeAnalysisWeb</a>");
		
		return buf.toString();
	}

}
