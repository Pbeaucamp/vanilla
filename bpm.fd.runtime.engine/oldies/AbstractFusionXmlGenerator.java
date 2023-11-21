package bpm.fd.runtime.engine.chart.fusion.generator;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;
import bpm.fd.api.core.model.resources.Palette;

public abstract class AbstractFusionXmlGenerator {
	
	protected String ROOT = "chart";
	protected String SET = "set";
	protected String SET_LABEL = "label";
	protected String SET_VALUE = "value";
	protected String SET_COLOR = "color";
	
	protected String DRILL = "link";
	protected boolean oldWay;
	public AbstractFusionXmlGenerator(boolean oldWay){
		this.oldWay = oldWay;
	}
	
	
	public String createMonoSerieXml(List<List<Object>> values, Properties chartProperties, Properties drillProperties, String[][] colors, Palette palette) throws Exception{
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ROOT);
		
//		<styles>
//      <definition>
//          <style name='myValuesFont' type='font' size='12' color='FFFFFF' bold='1' bgColor='666666' borderColor='666666'/>
//      </definition>
//      <application>
//          <apply toObject='DataValues' styles='myValuesFont' />
//      </application>
//  </styles>
		Element style = root.addElement("styles");
		Element myStyle = style.addElement("definition").addElement("style");
		myStyle.addAttribute("name", "mine").addAttribute("type", "font");
		style.addElement("application").addElement("apply")
			.addAttribute("toObject", "DataValues")
			.addAttribute("styles", "mine");
		
		
		if (chartProperties != null){
			overrideProperties(chartProperties);
			for(Object o : chartProperties.keySet()){
				if(o != null && o instanceof String) {
					if(((String)o).equals("multiLineLabels")) {
						boolean multiLineStyle = Boolean.parseBoolean(chartProperties.getProperty((String)o));
						if(multiLineStyle) {
							root.addAttribute("labelSepChar", "\\n");
						}
						
					}
					else if (((String)o).startsWith("_style_")){
						if (((String)o).endsWith("_bold")){
							myStyle.addAttribute("bold", chartProperties.getProperty((String)o));
						}
						if (((String)o).endsWith("_italic")){
							myStyle.addAttribute("italic", chartProperties.getProperty((String)o));
						}
						if (((String)o).endsWith("_underline")){
							myStyle.addAttribute("underline", chartProperties.getProperty((String)o));
						}
						if (((String)o).endsWith("_background")){
							myStyle.addAttribute("bgColor", chartProperties.getProperty((String)o));
						}
						if (((String)o).endsWith("fontColor")){
							myStyle.addAttribute("color", chartProperties.getProperty((String)o));
						}
					}
					else {
						root.addAttribute((String)o, chartProperties.getProperty((String)o));
					}
				}
			}
		}
		
		Collections.sort(values, new Comparator<List<Object>>() {

			public int compare(List<Object> l1, List<Object> l2) {
				if (l1.isEmpty() || l2.isEmpty()){
					return -1;
				}
				else if (l1.get(3) == null){
					return 1;
				}
				else if (l2.get(3) == null){
					return -1;
				}
				try{
					return ((Comparable)l1.get(3)).compareTo((Comparable)l2.get(3));
				}catch(Exception ex){
					
				}return l1.get(3).toString().compareTo(l2.get(3).toString());
				
			}
		});
		
		int count = 0;
		for(List<Object> row : values){
			Element set = root.addElement(SET);
			
			
			if (row.get(0) != null){
				String label = row.get(0).toString();//URLEncoder.encode(row.get(0).toString(), "UTF-8");
				String val = null;
				set.addAttribute(SET_LABEL, label);
				if (row.get(1) != null){
					val = row.get(1).toString();
					set.addAttribute(SET_VALUE, val);
				}
				
			}
			
			boolean colorApplyed = false;
			if (palette != null){
				for(String s : palette.getKeys()){
					if (palette.getColor(s) != null && set.attributeValue(SET_LABEL).toLowerCase().contains(s.toLowerCase())){
						set.addAttribute(SET_COLOR, "#" + palette.getColor(s));
						colorApplyed = true;
						break;
					}
				}
			}
			
			
			if (!colorApplyed && colors != null){
				if (count < colors[0].length ){
					
					set.addAttribute(SET_COLOR, colors[0][count++]);
				}
				
			}
	//		setParameter('drillDrivenCell_1', 'ComponentFilterDefinition_1');
	//		setParameter('ComponentFilterDefinition_1', _sliderComponentFilterDefinition_1.getSelectedValue());
	//		setLocation();	
			if (drillProperties != null){
				String url = drillProperties.getProperty("url");
				String pName = drillProperties.getProperty("pName");
				String categorieAsValue = drillProperties.getProperty("categoryAsValue");
				String drillJs = drillProperties.getProperty("drillJs");
				String event = drillProperties.getProperty("event");
				
				
				String zoomDivName = drillProperties.getProperty("zoomDivName");
				if (zoomDivName != null){
					String zoomWidth = drillProperties.getProperty("zoomWidth");
					String zoomHeight = drillProperties.getProperty("zoomHeight");;
					root.addAttribute("clickURL",  "JavaScript:zoomComponent('" +zoomDivName + "', " + zoomWidth + "," + zoomHeight + ");");//   "j-zoomChart-" + zoomDivName + ",200,200,zoom");;
					//root.addAttribute("clickURL",  "JavaScript:alert('not implemented');");//   "j-zoomChart-" + zoomDivName + ",200,200,zoom");;
				}
				else{
					String link = "";
					
					//replace pName='XXX' by the value
					if (url == null && pName == null){
						continue;
					}
					else if (pName == null){
						link = url;
						if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
							link += row.get(1).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
							
						}
						else{

							link += row.get(2).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");

							
							
						}
					}
					else if (pName != null){
						if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
							if (oldWay){
								link +=" setParameter('" + pName + "', '" + URLEncoder.encode(row.get(1).toString(), "UTF-8") +"', true);";
							}
							else{
								link +=" setParameter('" + pName + "', '" + row.get(1).toString() +"', true);";
							}
							
//							link +=" setParameter('" + pName + "', '" + row.get(1).toString() + "');";
						}
						else{
							if (oldWay){
								link +=" setParameter('" + pName + "', '" + URLEncoder.encode(row.get(2).toString(), "UTF-8") +"', true);";
							}
							else{
								link +=" setParameter('" + pName + "', '" + row.get(2).toString() +"', true);";
							}
							
//							link +=" setParameter('" + pName + "', '" + row.get(2).toString() + "');";
						}
						if (event != null){
							link += event;
						}

					}
					if (drillJs != null){
						set.addAttribute(DRILL, "JavaScript:openUrl(&#39;" + link/*URLEncoder.encode(link, "UTF-8")*/ +"&#39;)");
					}
					else{
//						set.addAttribute(DRILL, link);//URLEncoder.encode( link , "UTF-8"));
						set.addAttribute(DRILL, "JavaScript:" + link);
					}
				}
				
				
				
				
			}
			
			
		
		}

		
		String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
		
		OutputFormat f = OutputFormat.createCompactFormat();
		f.setAttributeQuoteCharacter('\'');

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		XMLWriter writer = new XMLWriter(bos, f);

		writer.setEscapeText(false);
		writer.write(doc.getRootElement());
		writer.close();
		bos.toString();
//		return /*bom + */doc.asXML()/*.replace("&amp;", "&").replace("\"", "'")*/.replace("\n", "");
		
		if (oldWay){
			return bos.toString("UTF-8").replace("&apos;", "\\\"").replace("\n", "");
		}
		else{
			return bos.toString("UTF-8");//.replace("&apos;", "\"").replace("\n", "");
		}
		
	}

	/**
	 * Override the propertyName with specific ones
	 * called just before generating XML
	 */
	abstract protected void overrideProperties(Properties prop);


	
	public String createGaugeXml(Properties props, double min, double max, double target, double tolerance, double value, double minSeuil, double maxSeuil){
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
		  
		  Element trendPoint2 = trends.addElement("point"); 
		  trendPoint2.addAttribute("startValue", (target-t_perc) + "" + "");
		  trendPoint2.addAttribute("displayValue", (target-t_perc) + " " + unitF);
		  trendPoint2.addAttribute("color", "666666");
		  trendPoint2.addAttribute("useMarker", "1");
		  trendPoint2.addAttribute("markerColor", "F1f1f1");
		  trendPoint2.addAttribute("markerBorderColor", "666666");
		//  trendPoint2.addAttribute("markerRadius", "666666");
		  trendPoint2.addAttribute("markerTooltext", (target-t_perc) + " " + unitF);
		  
		  
		  Element trendPoint3 = trends.addElement("point"); 
		  trendPoint3.addAttribute("startValue", (target+t_perc) + "" + "");
		  trendPoint3.addAttribute("displayValue", (target+t_perc) + " " + unitF);
		  trendPoint3.addAttribute("color", "666666");
		  trendPoint3.addAttribute("useMarker", "1");
		  trendPoint3.addAttribute("markerColor", "F1f1f1");
		  trendPoint3.addAttribute("markerBorderColor", "666666");
		//  trendPoint3.addAttribute("markerRadius", "666666");
		  trendPoint3.addAttribute("markerTooltext", (target+t_perc) + " " + unitF);
		  
		  
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
			red2.addAttribute("code", props.getProperty(
					GaugeOptions.standardKeys[GaugeOptions.KEY_COLOR_BAD],
					"FF654F"));

		}	  
		  
		  
		  Element dial = root.addElement("dials").addElement("dial");
		  dial.addAttribute("value", value + "");
		  dial.addAttribute("rearExtension", "10"); 
		  dial.addAttribute("showValue", props.getProperty(GaugeOptions.standardKeys[GaugeOptions.KEY_SHOW_VALUES], "1"));
		  
		  String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
		  return /*bom + */root.asXML().replace("\"", "'");
	}

}
