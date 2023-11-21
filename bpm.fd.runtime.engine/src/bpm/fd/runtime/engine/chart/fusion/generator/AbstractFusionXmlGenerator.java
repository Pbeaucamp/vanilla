package bpm.fd.runtime.engine.chart.fusion.generator;

import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;

public abstract class AbstractFusionXmlGenerator {
	
	
	
	

	public static String createGaugeXml(Properties props, double min, double max, double target, double tolerance, double value, double minSeuil, double maxSeuil){
//		double t = (target*tolerance)/100;
		
		  
		double amplitude = max-min;
		if (amplitude < 0){
			amplitude = - amplitude;
		}
		double t_perc = (double)amplitude * tolerance/100 ; 
		  Document document = DocumentHelper.createDocument();
		  Element root = document.addElement("chart");

		  		  
		  
		  //create general options
		  root.addAttribute("lowerLimit", min  + "");
		  root.addAttribute("upperLimit", max + "");
		  root.addAttribute("gaugeStartAngle", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_START_ANGLE], "0"));
		  root.addAttribute("gaugeEndAngle", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_STOP_ANGLE], "180"));
		  root.addAttribute("palette", "1");
		  root.addAttribute("showTickMarks", "0");
		  root.addAttribute("showTickValues", "0");
		  root.addAttribute("trendValueDistance", "25");
		  root.addAttribute("gaugeOuterRadius", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_OUTER_RADIUS], "100"));
		  root.addAttribute("gaugeInnerRadius", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_INNER_RADIUS], "70"));
		  root.addAttribute("bgColor",props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_BACKGROUND_COLOR], "FFFFFF"));
		  root.addAttribute("bgAlpha",props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_BG_ALPHA], "50"));
		  
		  for(String k : NumberFormatOptions.standardKeys){
			  String val = props.getProperty(k);
			  if (k != null){
				  root.addAttribute(k, val);
			  }
		  }
		  
		  
		  String unitF = "";
		  
		  
		  
		  Element trends = root.addElement("trendpoints");
		  
		  
		 
		  
		  Element trendPoint1 = trends.addElement("point"); 
		  trendPoint1.addAttribute("startValue", minSeuil + "");
		  trendPoint1.addAttribute("displayValue", minSeuil + " " + unitF);
		  trendPoint1.addAttribute("color", "666666");
		  trendPoint1.addAttribute("useMarker", "1");
		  trendPoint1.addAttribute("markerColor", "F1f1f1");
		  trendPoint1.addAttribute("markerBorderColor", "666666");
		//  trendPoint1.addAttribute("markerRadius", "666666");
		  trendPoint1.addAttribute("markerTooltext", minSeuil + " " + unitF);

		 
		  trendPoint1 = trends.addElement("point"); 
		  trendPoint1.addAttribute("startValue", min + "");
		  trendPoint1.addAttribute("displayValue", min+ " " + unitF);
		  trendPoint1.addAttribute("color", "666666");
		  trendPoint1.addAttribute("useMarker", "1");
		  trendPoint1.addAttribute("markerColor", "F1f1f1");
		  trendPoint1.addAttribute("markerBorderColor", "666666");
		//  trendPoint1.addAttribute("markerRadius", "666666");
		  trendPoint1.addAttribute("markerTooltext", min + " " + unitF);
		  
		  trendPoint1 = trends.addElement("point"); 
		  trendPoint1.addAttribute("startValue", max + "");
		  trendPoint1.addAttribute("displayValue", max+ " " + unitF);
		  trendPoint1.addAttribute("color", "666666");
		  trendPoint1.addAttribute("useMarker", "1");
		  trendPoint1.addAttribute("markerColor", "F1f1f1");
		  trendPoint1.addAttribute("markerBorderColor", "666666");
		//  trendPoint1.addAttribute("markerRadius", "666666");
		  trendPoint1.addAttribute("markerTooltext", max + " " + unitF);
		  
		  if(tolerance == 15) {
			  Element trendPoint2 = trends.addElement("point"); 
			  trendPoint2.addAttribute("startValue", (target-t_perc) + "" + "");
			  trendPoint2.addAttribute("displayValue", (target-t_perc) + " " + unitF);
			  trendPoint2.addAttribute("color", "666666");
			  trendPoint2.addAttribute("useMarker", "0");
			  trendPoint2.addAttribute("markerColor", "F1f1f1");
			  trendPoint2.addAttribute("markerBorderColor", "666666");
			//  trendPoint2.addAttribute("markerRadius", "666666");
			  trendPoint2.addAttribute("markerTooltext", (target-t_perc) + " " + unitF);
			  
			  
			  Element trendPoint3 = trends.addElement("point"); 
			  trendPoint3.addAttribute("startValue", (target+t_perc) + "" + "");
			  trendPoint3.addAttribute("displayValue", (target+t_perc) + " " + unitF);
			  trendPoint3.addAttribute("color", "666666");
			  trendPoint3.addAttribute("useMarker", "0");
			  trendPoint3.addAttribute("markerColor", "F1f1f1");
			  trendPoint3.addAttribute("markerBorderColor", "666666");
			//  trendPoint3.addAttribute("markerRadius", "666666");
			  trendPoint3.addAttribute("markerTooltext", (target+t_perc) + " " + unitF);
			  
		  }
		  
		  
		  Element trendPoint4 = trends.addElement("point"); 
		  trendPoint4.addAttribute("startValue", maxSeuil + "");
		  trendPoint4.addAttribute("displayValue", maxSeuil + " " + unitF);
		  trendPoint4.addAttribute("color", "666666");
		  trendPoint4.addAttribute("useMarker", "1");
		  trendPoint4.addAttribute("markerColor", "F1f1f1");
		  trendPoint4.addAttribute("markerBorderColor", "666666");
		//  trendPoint4.addAttribute("markerRadius", "666666");
		  trendPoint4.addAttribute("markerTooltext", maxSeuil + " " + unitF);
		  
		  //create colors
		  Element colorRange = root.addElement("colorRange");
		  
		 

		  
		  
		  //Calcul de la zone a afficher
		  
		  
		  Element red1 = colorRange.addElement("color");
		  if (min <= minSeuil){
			 
			  red1.addAttribute("minValue", min + "");
			  red1.addAttribute("maxValue", minSeuil + "" );
			  red1.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD], "FF654F"));  
		  }
		  
		  
		  
		  if (minSeuil <= (target-t_perc)) {
			  Element yellow1 = colorRange.addElement("color");
			  yellow1.addAttribute("minValue", minSeuil + "");
			  yellow1.addAttribute("maxValue", (target-t_perc) + "" );
			  yellow1.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_MEDIUM], "F6BD0F"));

		  }
		 	
		  if ((target-t_perc) <= (target+t_perc)) {
			  Element green = colorRange.addElement("color");
			  green.addAttribute("minValue", ((target-t_perc) > -1 ? (target-t_perc) : 0) + "");
			  green.addAttribute("maxValue", (target+t_perc) + "");
			  green.addAttribute("code",  props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD], "8BBA00"));

		  }
		  if ((target+t_perc) <= (maxSeuil)) {
			  Element yellow2 = colorRange.addElement("color");
			  yellow2.addAttribute("minValue", (target+t_perc) + "");
			  yellow2.addAttribute("maxValue", maxSeuil + "" );
			  yellow2.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_MEDIUM], "F6BD0F"));
 
		  }
		  		  
		  
		  
		  
		if (maxSeuil <= max) {
			Element red2 = colorRange.addElement("color");
			red2.addAttribute("minValue", maxSeuil + "");
			red2.addAttribute("maxValue", max + "");
			if(tolerance == 15) {
				red2.addAttribute("code", props.getProperty(
											GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD],
//											GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD],
											"FF654F"));
			}
			else {
				red2.addAttribute("code", props.getProperty(
	//					GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD],
						GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD],
						"FF654F"));
			}

		}	  
		  
		  
		  Element dial = root.addElement("dials").addElement("dial");
		  dial.addAttribute("value", value + "");
		  dial.addAttribute("rearExtension", "10"); 
		  dial.addAttribute("showValue", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_SHOW_VALUES], "1"));
		  
//		  String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
		  String res =root.asXML().replace("\"", "'");
		  return res;
	}

	public static String createBulbXml(Properties props, double min, double max, double target, double tolerance, double value, double minSeuil, double maxSeuil) {
		double amplitude = max-min;
		if (amplitude < 0){
			amplitude = - amplitude;
		}
		double t_perc = (double)amplitude * tolerance/100 ; 
		  Document document = DocumentHelper.createDocument();
		  Element root = document.addElement("chart");

		  		  
		  
		  //create general options
//		  root.addAttribute("lowerLimit", min  + "");
//		  root.addAttribute("upperLimit", max + "");
//		  root.addAttribute("gaugeStartAngle", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_START_ANGLE], "0"));
//		  root.addAttribute("gaugeEndAngle", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_STOP_ANGLE], "180"));
		  root.addAttribute("palette", "1");
//		  root.addAttribute("showTickMarks", "0");
//		  root.addAttribute("showTickValues", "0");
//		  root.addAttribute("trendValueDistance", "25");
//		  root.addAttribute("gaugeOuterRadius", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_OUTER_RADIUS], "100"));
//		  root.addAttribute("gaugeInnerRadius", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_INNER_RADIUS], "70"));
		  root.addAttribute("bgColor",props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_BACKGROUND_COLOR], "FFFFFF"));
		  root.addAttribute("bgAlpha",props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_BG_ALPHA], "50"));
		  
		  for(String k : NumberFormatOptions.standardKeys){
			  String val = props.getProperty(k);
			  if (k != null){
				  root.addAttribute(k, val);
			  }
		  }
		  
		  
		  String unitF = "";
		  
		  
		  
//		  Element trends = root.addElement("trendpoints");
//		  
//		  
//		 
//		  
//		  Element trendPoint1 = trends.addElement("point"); 
//		  trendPoint1.addAttribute("startValue", minSeuil + "");
//		  trendPoint1.addAttribute("displayValue", minSeuil + " " + unitF);
//		  trendPoint1.addAttribute("color", "666666");
//		  trendPoint1.addAttribute("useMarker", "1");
//		  trendPoint1.addAttribute("markerColor", "F1f1f1");
//		  trendPoint1.addAttribute("markerBorderColor", "666666");
//		//  trendPoint1.addAttribute("markerRadius", "666666");
//		  trendPoint1.addAttribute("markerTooltext", minSeuil + " " + unitF);
//
//		 
//		  trendPoint1 = trends.addElement("point"); 
//		  trendPoint1.addAttribute("startValue", min + "");
//		  trendPoint1.addAttribute("displayValue", min+ " " + unitF);
//		  trendPoint1.addAttribute("color", "666666");
//		  trendPoint1.addAttribute("useMarker", "1");
//		  trendPoint1.addAttribute("markerColor", "F1f1f1");
//		  trendPoint1.addAttribute("markerBorderColor", "666666");
//		//  trendPoint1.addAttribute("markerRadius", "666666");
//		  trendPoint1.addAttribute("markerTooltext", min + " " + unitF);
//		  
//		  trendPoint1 = trends.addElement("point"); 
//		  trendPoint1.addAttribute("startValue", max + "");
//		  trendPoint1.addAttribute("displayValue", max+ " " + unitF);
//		  trendPoint1.addAttribute("color", "666666");
//		  trendPoint1.addAttribute("useMarker", "1");
//		  trendPoint1.addAttribute("markerColor", "F1f1f1");
//		  trendPoint1.addAttribute("markerBorderColor", "666666");
//		//  trendPoint1.addAttribute("markerRadius", "666666");
//		  trendPoint1.addAttribute("markerTooltext", max + " " + unitF);
//		  
//		  if(tolerance == 15) {
//			  Element trendPoint2 = trends.addElement("point"); 
//			  trendPoint2.addAttribute("startValue", (target-t_perc) + "" + "");
//			  trendPoint2.addAttribute("displayValue", (target-t_perc) + " " + unitF);
//			  trendPoint2.addAttribute("color", "666666");
//			  trendPoint2.addAttribute("useMarker", "0");
//			  trendPoint2.addAttribute("markerColor", "F1f1f1");
//			  trendPoint2.addAttribute("markerBorderColor", "666666");
//			//  trendPoint2.addAttribute("markerRadius", "666666");
//			  trendPoint2.addAttribute("markerTooltext", (target-t_perc) + " " + unitF);
//			  
//			  
//			  Element trendPoint3 = trends.addElement("point"); 
//			  trendPoint3.addAttribute("startValue", (target+t_perc) + "" + "");
//			  trendPoint3.addAttribute("displayValue", (target+t_perc) + " " + unitF);
//			  trendPoint3.addAttribute("color", "666666");
//			  trendPoint3.addAttribute("useMarker", "0");
//			  trendPoint3.addAttribute("markerColor", "F1f1f1");
//			  trendPoint3.addAttribute("markerBorderColor", "666666");
//			//  trendPoint3.addAttribute("markerRadius", "666666");
//			  trendPoint3.addAttribute("markerTooltext", (target+t_perc) + " " + unitF);
//			  
//		  }
//		  
//		  
//		  Element trendPoint4 = trends.addElement("point"); 
//		  trendPoint4.addAttribute("startValue", maxSeuil + "");
//		  trendPoint4.addAttribute("displayValue", maxSeuil + " " + unitF);
//		  trendPoint4.addAttribute("color", "666666");
//		  trendPoint4.addAttribute("useMarker", "1");
//		  trendPoint4.addAttribute("markerColor", "F1f1f1");
//		  trendPoint4.addAttribute("markerBorderColor", "666666");
//		//  trendPoint4.addAttribute("markerRadius", "666666");
//		  trendPoint4.addAttribute("markerTooltext", maxSeuil + " " + unitF);
		  
		  //create colors
		  Element colorRange = root.addElement("colorRange");
		  
		 

		  
		  
		  //Calcul de la zone a afficher
		  
		  
		  Element red1 = colorRange.addElement("color");
		  if (min <= minSeuil){
			 
			  red1.addAttribute("minValue", min + "");
			  red1.addAttribute("maxValue", minSeuil + "" );
			  red1.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD], "FF654F"));  
		  }
		  
		  
		  
		  if (minSeuil <= (target-t_perc)) {
			  Element yellow1 = colorRange.addElement("color");
			  yellow1.addAttribute("minValue", minSeuil + "");
			  yellow1.addAttribute("maxValue", (target-t_perc) + "" );
			  yellow1.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_MEDIUM], "F6BD0F"));

		  }
		 	
		  if ((target-t_perc) <= (target+t_perc)) {
			  Element green = colorRange.addElement("color");
			  green.addAttribute("minValue", (target-t_perc) + "");
			  green.addAttribute("maxValue", (target+t_perc) + "");
			  green.addAttribute("code",  props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD], "8BBA00"));

		  }
		  if ((target+t_perc) <= (maxSeuil)) {
			  Element yellow2 = colorRange.addElement("color");
			  yellow2.addAttribute("minValue", (target+t_perc) + "");
			  yellow2.addAttribute("maxValue", maxSeuil + "" );
			  yellow2.addAttribute("code", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_MEDIUM], "F6BD0F"));
 
		  }
		  		  
		  
		  
		  
		if (maxSeuil <= max) {
			Element red2 = colorRange.addElement("color");
			red2.addAttribute("minValue", maxSeuil + "");
			red2.addAttribute("maxValue", max + "");
			if(tolerance == 15) {
				red2.addAttribute("code", props.getProperty(
											GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD],
//											GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD],
											"FF654F"));
			}
			else {
				red2.addAttribute("code", props.getProperty(
	//					GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD],
						GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_GOOD],
						"FF654F"));
			}

		}	  
		  
		  
//		  Element dial = root.addElement("dials").addElement("dial");
//		  dial.addAttribute("value", value + "");
//		  dial.addAttribute("rearExtension", "10"); 
//		  dial.addAttribute("showValue", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_SHOW_VALUES], "1"));
		  
			root.addElement("value").setText(value+"");
		
		  String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
		  return /*bom + */root.asXML().replace("\"", "'");
	}

}
