package bpm.fd.runtime.engine.components;

import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.gauge.FmGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.StaticGaugeDatas;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;

public class GaugeGenerator {
	public static String generateJspBlock(int offset, ComponentGauge gauge, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		String classCss = "";
		if (gauge.getCssClass() != null && ! "".equals(gauge.getCssClass())){
			classCss = " class=\"" + gauge.getCssClass() + "\" ";
		}
		
		String resultSet = gauge.getId() + "ResultSet";
		String query = "";
		if (gauge.getDatas() instanceof FmGaugeDatas){
			query = gauge.getDatas().getDataSet().getId() + "Query";
			
			buf.append(" <%\n");
			buf.append("     IResultSet " + resultSet + " = null;\n");
			buf.append("     try{\n");
			
			buf.append(JSPDataGenerator.prepareQuery("", query, gauge));
			
			buf.append("        " + resultSet + " = " + query + ".executeQuery();\n");
			buf.append("        " + resultSet + ".next();\n");
			buf.append("     }catch(Exception e){\n");
			buf.append("         e.printStackTrace();\n");
			buf.append("     }\n");
			buf.append("%>\n\n");
		}
		
		buf.append("<p id=\"" + gauge.getId() + "\" " + classCss  +  ">\n");
		buf.append("    <script type=\"text/javascript\">\n");
		buf.append("<%\n");
		buf.append("        // create chartProperties\n");
		buf.append(generateOptions( "" + "        ", gauge.getOptions(), gauge.getName(), gauge.getId()));
	
		buf.append("    try{\n");
		buf.append("%>\n");
		buf.append("        var chart_" + gauge.getId() + " = new FusionCharts(\"../../FusionCharts/AngularGauge.swf\", " + "\"Id_"  + gauge.getId() + "\", \"" +gauge.getWidth()+"\", \"" + gauge.getHeight() +"\", \"0\", \"0\");\n");
		
		
		StringBuffer xml = new StringBuffer();
		
		xml.append("new FusionChartXmlGenerator(true).createGaugeXml(");
		xml.append(gauge.getId() + "GaugeP, " );
		if (gauge.getDatas() instanceof StaticGaugeDatas){
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getMin() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getMax() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getTarget() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getTolerancePerc() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getValue() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getMinSeuil() + ", ");
			xml.append(((StaticGaugeDatas)gauge.getDatas()).getMaxSeuil() + ") ");
		}
		else if (gauge.getDatas() instanceof FmGaugeDatas){
			ComplexGaugeDatas dt = (ComplexGaugeDatas)gauge.getDatas();
			if (dt.isRangeManual()){
				
				xml.append( dt.getMinValue() + ", ");
				xml.append( dt.getMaxValue() + ", ");
				
			}
			else{
				xml.append(" " + resultSet + ".getDouble(" + dt.getIndexMin() + ") , ");
				xml.append(" " + resultSet + ".getDouble(" +dt.getIndexMax()+ ") , ");
				
				
			}
			
			if (dt.isTargetNeeded()){
				//xml.append(dt.getIndexTarget() + " , ");
				xml.append(dt.getIndexTolerance() + "f , ");
				
				
			}
			else{
				xml.append( "0., ");
				xml.append( "0., ");
			}
			xml.append(" " + resultSet + ".getDouble(" +((FmGaugeDatas)gauge.getDatas()).getIndexValue() + ")  , ");
			
			if (dt.isExpectedFieldsUsed()){
				xml.append(" " + resultSet + ".getDouble(" +((FmGaugeDatas)gauge.getDatas()).getIndexMinSeuil() + ")  , ");
				xml.append(" " + resultSet + ".getDouble(" +((FmGaugeDatas)gauge.getDatas()).getIndexMaxSeuil() + ") ");
				
				
			}
			else{
				xml.append( "0., ");
				xml.append( "0.");
			}
			
			xml.append(" ) ");
		}
		
		
		buf.append("        chart_" + gauge.getId() + ".setDataXML(<%= \"\\\"\" +" + xml.toString() +" + \"\\\"\"%>);\n");  buf.append("        chart_"  + gauge.getId() + ".render(\""  + gauge.getId() + "\");\n");
		buf.append("    </script>\n");
		buf.append("<%\n");
		buf.append("    }catch(Exception ex){\n");
		buf.append("        ex.printStackTrace();\n");
		buf.append("    }\n");
		buf.append("%>\n");
		buf.append("</p>\n");
		if (gauge.getDatas() instanceof FmGaugeDatas){
			buf.append(" <%\n");
			buf.append("     try{\n");
			buf.append("        " + resultSet + ".close();\n");
			buf.append("     }catch(Exception e){\n");
			buf.append("         e.printStackTrace();\n");
			buf.append("     }\n");
			buf.append("%>\n\n");
		}
		
		return buf.toString();
	}
	
	private static String generateOptions(String spacing, List<IComponentOptions> opts, String name, String id){
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + "Properties " + id + "GaugeP = new Properties();\n");
		for(IComponentOptions opt : opts){
			
			
			for(String key : opt.getInternationalizationKeys()){
				buf.append(spacing + id + "GaugeP.setProperty(\"" + key + "\", i18nReader.getLabel(clientLocale, \"" + name + "." + key + "\", _parameterMap));\n");
			}
			
			for(String key : opt.getNonInternationalizationKeys()){
				if (opt.getValue(key) != null){
					buf.append(spacing + id + "GaugeP.setProperty(\"" + key + "\", \"" + opt.getValue(key)  + "\");\n");
				}
				
			}
		}
		
		
		buf.append("\n\n");
		return buf.toString();
	}
}
