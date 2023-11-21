package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;
import bpm.fd.api.core.model.components.definition.gauge.StaticGaugeDatas;
import bpm.fd.runtime.engine.chart.fusion.generator.AbstractFusionXmlGenerator;
import bpm.fd.runtime.model.DashState;

public class GaugeRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentGauge>{
	public String getJavaScriptFdObjectVariable(ComponentGauge gauge){
		String jsVar = "gauge_" + gauge.getId();
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("     fdObjects[\""+gauge.getId() + "\"]=new FdChart(" + jsVar  + ", '"+ gauge.getName() + "', \"\");");
		buf.append("</script>\n");
		return buf.toString();
	}
	
	public String getHTML(Rectangle layout, ComponentGauge gauge, DashState state, IResultSet datas, boolean refresh){
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, gauge));
		buf.append(getComponentDefinitionDivEnd());
		
		
		String jsVar = "gauge_" + gauge.getId();
		
		buf.append("<script type=\"text/javascript\">\n");
		GaugeOptions opts = (GaugeOptions)gauge.getOptions(GaugeOptions.class); 
		if(opts.isBulb()) {
			buf.append("     var " + jsVar + " = new FusionCharts('../../FusionCharts/Bulb.swf','"+ gauge.getId() + "_fs', '" + gauge.getWidth() + "', '" + gauge.getHeight() + "', '0', '0');\n");
		}
		else {
			buf.append("     var " + jsVar + " = new FusionCharts('../../FusionCharts/AngularGauge.swf','"+ gauge.getId() + "_fs', '" + gauge.getWidth() + "', '" + gauge.getHeight() + "', '0', '0');\n");
		}
		    
		buf.append("     " + jsVar + ".setTransparent(true);\n");	        
		buf.append(jsVar + ".render(\"" + gauge.getName() + "\");\n");    

		String xml = null;
		
		try {
			xml = generateXml(gauge, state, datas);
		} catch (Exception e) {
			Logger.getLogger(GaugeRenderer.class).error("Failed to generate Xml");
			e.printStackTrace();
		}
		buf.append("     fdObjects[\""+gauge.getId() + "\"]=new FdFusionChart(" + jsVar  + ", '"+ gauge.getName() + "', \""+ xml + "\");");
		
		buf.append("</script>\n");
		
		
		if (refresh){
			return xml;
		}
		
		return buf.toString();
	}
	
	
	
	private static String generateXml(ComponentGauge gauge, DashState state, IResultSet datas) throws Exception{
		Properties p = createGaugeProperties(gauge);
		double min = 0.;
		double max = 0.;
		double target = 0.;
		double tolerance = 0.;
		double value = 0.;
		double minSeuil = 0.;
		double maxSeuil = 0.;
		
		
		if (gauge.getDatas() instanceof StaticGaugeDatas){
			 min = ((StaticGaugeDatas)gauge.getDatas()).getMin();
			 max = ((StaticGaugeDatas)gauge.getDatas()).getMax();
			 target = ((StaticGaugeDatas)gauge.getDatas()).getTarget();
			 tolerance = ((StaticGaugeDatas)gauge.getDatas()).getTolerancePerc();
			 value = ((StaticGaugeDatas)gauge.getDatas()).getValue();
			 minSeuil = ((StaticGaugeDatas)gauge.getDatas()).getMinSeuil();
			 maxSeuil = ((StaticGaugeDatas)gauge.getDatas()).getMaxSeuil();
		}
		else if (gauge.getDatas() instanceof ComplexGaugeDatas){
			ComplexGaugeDatas dt = (ComplexGaugeDatas)gauge.getDatas();
			
			
			datas.next();
		
			if (dt.isRangeManual()){
				 min = dt.getMinValue() ;
				 max = dt.getMaxValue();
			}
			else{
				 min = datas.getDouble(dt.getIndexMin()) ;
				 max = datas.getDouble(dt.getIndexMax()) ;
			}
			
			if (dt.isTargetNeeded()){
				
				if (dt.isUseFieldForTarget()){
					try{
						target = datas.getDouble(dt.getTargetIndex());
					}catch(Exception ex){
						target = 0.0;
					}
				}
				else{
					target = dt.getTargetValue();//dt.getIndexTarget();
					
				}
				if (dt.getIndexTolerance() != null){
					tolerance = dt.getIndexTolerance();
				}
				
			}
			else{
				target = 0.;
				tolerance = 0.;
			}
			
			value = datas.getDouble(dt.getIndexValue());
			
			if (dt.isExpectedFieldsUsed()){
				minSeuil = datas.getDouble(dt.getIndexMinSeuil());
				maxSeuil = datas.getDouble(dt.getIndexMaxSeuil());
			}
			else{
				minSeuil = dt.getIndexMinSeuil();
				maxSeuil = dt.getIndexMaxSeuil();
			}
		}
		
		GaugeOptions opts = (GaugeOptions)gauge.getOptions(GaugeOptions.class); 
		if(opts.isBulb()) {
			return AbstractFusionXmlGenerator.createBulbXml(p, min, max, target, tolerance, value, minSeuil, maxSeuil);
		}
		else {
			return AbstractFusionXmlGenerator.createGaugeXml(p, min, max, target, tolerance, value, minSeuil, maxSeuil);
		}
	}
	

	private static Properties createGaugeProperties(ComponentGauge gauge){
		Properties p = new Properties();
		for(IComponentOptions opt : gauge.getOptions()){
			for(String key : opt.getInternationalizationKeys()){
				if (opt.getValue(key) != null){
					p.setProperty(key, opt.getValue(key));
				}
				
			}
			for(String key : opt.getNonInternationalizationKeys()){
				if (opt.getValue(key) != null){
					p.setProperty(key, opt.getValue(key));
				}
			}
		}
		
		return p;
	}

}
