package bpm.vanilla.map.core.design.fusionmap;

import java.util.HashMap;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FusionMapProperties {
	
	public static final String CUSTOM_PROPS = "CustomProps";
	
	//FONCTIONNAL ATTRIBUTES
	public static final String SHOW_LABELS = "showLabels";
	public static final String INCLUDE_NAME_IN_LABELS = "includeNameInLabels";
	public static final String INCLUDE_VALUE_IN_LABELS = "includeValueInLabels";
	public static final String SHOW_SHADOW = "showShadow";
	public static final String SHOW_BEVEL = "showBevel";
	
	//FONT
	public static final String BASE_FONT_SIZE = "baseFontSize";
	public static final String BASE_FONT_COLOR = "baseFontColor";
	
	//MAP COSMETICS
	public static final String BG_COLOR = "bgColor";
	public static final String BG_ALPHA = "bgAlpha";
	
	//MAP ENTITIES COSMETICS
	public static final String FILL_COLOR = "fillColor";
	public static final String FILL_ALPHA = "fillAlpha";
	public static final String HOVER_COLOR = "hoverColor";
	
	//NUMBER FORMAT
	public static final String FORMAT_NUMBER_SCALE = "formatNumberScale";
	public static final String NUMBER_PREFIX = "numberPrefix";
	public static final String DECIMAL_SEPARATOR = "decimalSeparator";
	public static final String THOUSAND_SEPARATOR = "thousandSeparator";
	public static final String DECIMALS = "decimals";
	
	//TOOLTIP
	public static final String SHOW_TOOLTIP = "showToolTip";
	public static final String TOOLTIP_BG_COLOR = "toolTipBgColor";
	
	//MAP PADDING & MARGINS
	public static final String LEGEND_PADDING = "legendPadding";
	public static final String MAP_LEFT_MARGIN = "mapLeftMargin";
	public static final String MAP_RIGHT_MARGIN = "mapRightMargin";
	public static final String MAP_TOP_MARGIN = "mapTopMargin";
	public static final String MAP_BOTTOM_MARGIN = "mapBottomMargin";
	
	//LEGEND
	public static final String SHOW_LEGEND = "showLegend";
	public static final String LEGEND_CAPTION = "legendCaption";
	public static final String LEGEND_POSITION = "legendPosition"; //BOTOM or RIGHT
	public static final String LEGEND_BG_COLOR = "legendBgColor";
	public static final String LEGEND_BG_ALPHA = "legendBgAlpha";

	public static final String[] FUSION_MAP_PROPERTIES = {
		SHOW_LABELS, 
		INCLUDE_NAME_IN_LABELS,
		INCLUDE_VALUE_IN_LABELS,
		SHOW_SHADOW,
		SHOW_BEVEL,
		BG_COLOR,
		BG_ALPHA,
		FILL_COLOR,
		FILL_ALPHA,
		HOVER_COLOR,
		FORMAT_NUMBER_SCALE,
		NUMBER_PREFIX,
		DECIMAL_SEPARATOR,
		THOUSAND_SEPARATOR,
		DECIMALS,
		SHOW_TOOLTIP,
		TOOLTIP_BG_COLOR,
		BASE_FONT_SIZE,
		BASE_FONT_COLOR,
		LEGEND_PADDING,
		MAP_LEFT_MARGIN,
		MAP_RIGHT_MARGIN,
		MAP_TOP_MARGIN,
		MAP_BOTTOM_MARGIN,
		SHOW_LEGEND,
		LEGEND_CAPTION,
		LEGEND_POSITION,
		LEGEND_BG_COLOR,
		LEGEND_BG_ALPHA
	};
	
	public static Properties buildDefaultProperties(){
		Properties props = new Properties();
		props.put(SHOW_LABELS, "1");
		props.put(INCLUDE_NAME_IN_LABELS, "1");
		props.put(INCLUDE_VALUE_IN_LABELS, "1");
		props.put(SHOW_SHADOW, "1");
		props.put(SHOW_BEVEL, "1");
		props.put(BG_COLOR, "");
		props.put(BG_ALPHA, "");
		props.put(FILL_COLOR, "");
		props.put(FILL_ALPHA, "70");
		props.put(HOVER_COLOR, "639ACE");
		props.put(FORMAT_NUMBER_SCALE, "0");
		props.put(NUMBER_PREFIX, "");
		props.put(DECIMAL_SEPARATOR, ",");
		props.put(THOUSAND_SEPARATOR, " ");
		props.put(DECIMALS, "2");
		props.put(SHOW_TOOLTIP, "1");
		props.put(TOOLTIP_BG_COLOR, "");
		props.put(BASE_FONT_SIZE, "9");
		props.put(BASE_FONT_COLOR, "");
		props.put(LEGEND_PADDING, "0");
		props.put(MAP_LEFT_MARGIN, "0");
		props.put(MAP_RIGHT_MARGIN, "0");
		props.put(MAP_TOP_MARGIN, "0");
		props.put(MAP_BOTTOM_MARGIN, "0");
		props.put(SHOW_LEGEND, "1");
		props.put(LEGEND_CAPTION, "");
		props.put(LEGEND_POSITION, "RIGHT");
		props.put(LEGEND_BG_COLOR, "");
		props.put(LEGEND_BG_ALPHA, "");
		
		return props;
	}
	
	public static Properties buildPropertiesFromXml(String xml){
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			
			Properties props = new Properties();
			Element pr = root.element(CUSTOM_PROPS);
			if(pr != null){
				for(Object obj : pr.elements("property")){
					Element el = (Element)obj;
					props.put(el.element("name").getText(), el.element("value").getText());
				}
			}
			
			return props;
		} catch (Exception e) {
			e.printStackTrace();
			return buildDefaultProperties();
		}
	}
	
	public static String buildPropertiesXml(Properties props){
		StringBuilder builder = new StringBuilder("<root>\n");
		builder.append("    <" + CUSTOM_PROPS + ">\n");
		for(Object obj : props.keySet()){
			builder.append("        <property>\n");
			String propName = (String)obj;
			builder.append("            <name>" + propName + "</name>\n");
			builder.append("            <value>" + props.get(propName) + "</value>\n");
			builder.append("        </property>\n");
		}
		builder.append("    </" + CUSTOM_PROPS + ">\n");
		builder.append("</root>");
		
		return builder.toString();
	}
	
	public static String buildPropertiesXml(HashMap<String, String> props){
		StringBuilder builder = new StringBuilder("<root>\n");
		builder.append("    <" + CUSTOM_PROPS + ">\n");
		for(Object obj : props.keySet()){
			builder.append("        <property>\n");
			String propName = (String)obj;
			builder.append("            <name>" + propName + "</name>\n");
			builder.append("            <value>" + props.get(propName) + "</value>\n");
			builder.append("        </property>\n");
		}
		builder.append("    </" + CUSTOM_PROPS + ">\n");
		builder.append("</root>");
		
		return builder.toString();
	}
	
	public static String getPropertiesXmlForMap(Properties props){
		StringBuilder builder = new StringBuilder();
		for(String prop : FUSION_MAP_PROPERTIES){
			if(props.get(prop) != null && !props.get(prop).toString().isEmpty()){
				builder.append(" ");
				builder.append(prop);
				builder.append("='");
				builder.append(props.get(prop));
				builder.append("'");
			}
		}
		
		return builder.toString();
	}
}
